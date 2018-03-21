package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import ru.saidgadjiev.orm.next.core.criteria.impl.Criteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.field.FetchType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignCollectionFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldTypeFactory;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stament_executor.GenericResults;
import ru.saidgadjiev.orm.next.core.stament_executor.object.collection.LazyList;
import ru.saidgadjiev.orm.next.core.stament_executor.object.collection.LazySet;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ResultsMapperImpl<T> implements ResultsMapper<T> {

    private ConnectionSource dataSource;

    private List<IDBFieldType> resultFields;

    private TableInfo<T> tableInfo;

    private Set<Class<?>> parents;

    public ResultsMapperImpl(ConnectionSource dataSource,
                             TableInfo<T> tableInfo,
                             List<IDBFieldType> resultFields,
                             Set<Class<?>> parents) {
        this.dataSource = dataSource;
        this.resultFields = resultFields;
        this.tableInfo = tableInfo;
        this.parents = parents;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {
        T object = (T) newObject(tableInfo.getConstructor());

        parents.add(tableInfo.getTableClass());

        for (IDBFieldType fieldType : resultFields) {
            if (fieldType.isDbFieldType()) {
                buildBase(object, results, fieldType);
            } else if (fieldType.isForeignFieldType()) {
                buildForeign(object, results, fieldType, parents);
            } else if (fieldType.isForeignCollectionFieldType()) {
                buildForeignCollection(object, fieldType, parents);
            }
        }

        return object;
    }

    private void buildBase(T object, DatabaseResults data, IDBFieldType fieldType) throws Exception {
        if (fieldType.isDbFieldType()) {
            Object value = fieldType.getDataPersister().parseSqlToJava(fieldType, data.getObject(fieldType.getColumnName()));

            fieldType.assign(object, value);
        }
    }

    private void buildForeign(T object, DatabaseResults data, IDBFieldType fieldType, Set<Class<?>> parents) throws Exception {
        ForeignFieldType foreignFieldType = (ForeignFieldType) fieldType;

        if (!parents.contains(foreignFieldType.getForeignFieldClass())) {
            TableInfo<?> foreignTableInfo = TableInfo.build(foreignFieldType.getForeignFieldClass());
            Session foreignDao = new BaseSessionManagerImpl(dataSource).forClass(foreignTableInfo.getTableClass());
            SelectStatement<?> selectStatement = new SelectStatement<>(foreignFieldType.getForeignFieldClass());

            selectStatement.where(new Criteria().add(Restrictions.eq(foreignTableInfo.getPrimaryKey().get().getColumnName(), data.getObject(foreignFieldType.getColumnName()))));
            DefaultVisitor visitor = new DefaultVisitor(dataSource.getDatabaseType());

            selectStatement.accept(visitor);
            Object foreignObject = foreignDao.query(visitor.getQuery(), selectStatement.getArgs()).getFirstResult(new ResultsMapperImpl<>(dataSource, foreignTableInfo, foreignTableInfo.getFieldTypes(), parents));

            foreignFieldType.assign(object, foreignObject);
        }
    }

    private void buildForeignCollection(T object, IDBFieldType fieldType, Set<Class<?>> parents) throws Exception {
        ForeignCollectionFieldType foreignCollectionFieldType = (ForeignCollectionFieldType) fieldType;
        TableInfo<Object> foreignTableInfo = TableInfo.build((Class<Object>) foreignCollectionFieldType.getForeignFieldClass());
        Session<Object, Object> foreignDao = new BaseSessionManagerImpl(dataSource).forClass(foreignTableInfo.getTableClass());

        if (tableInfo.getPrimaryKey().isPresent() && foreignTableInfo.getPrimaryKey().isPresent()) {
            IDBFieldType idField = tableInfo.getPrimaryKey().get();
            ForeignFieldType foreignField = (ForeignFieldType) new ForeignFieldTypeFactory().createFieldType(foreignCollectionFieldType.getForeignField());
            SelectStatement<?> selectStatement = new SelectStatement<>(foreignTableInfo.getTableClass());

            selectStatement.where(new Criteria().add(Restrictions.eq(foreignField.getFieldName(), idField.access(object))));
            DefaultVisitor visitor = new DefaultVisitor(dataSource.getDatabaseType());

            selectStatement.accept(visitor);
            if (foreignCollectionFieldType.getFetchType().equals(FetchType.EAGER)) {
                try (GenericResults<Object> genericResults = foreignDao.query(visitor.getQuery(), new HashMap<Integer, Object>() {{
                    put(1, idField.access(object));
                }})) {
                    List<Object> objects = genericResults.getResults(new ResultsMapperImpl<>(dataSource, foreignTableInfo, foreignTableInfo.getFieldTypes(), parents));

                    for (Object foreignObject : objects) {
                        foreignField.assign(foreignObject, object);
                    }

                    foreignCollectionFieldType.addAll(object, objects);
                }
            } else {
                Collection<?> collection = (Collection<?>) foreignCollectionFieldType.access(object);
                Supplier<List<Object>> fetcher = () -> {
                    try {
                        try (GenericResults<Object> genericResults = foreignDao.query(visitor.getQuery(), new HashMap<Integer, Object>() {{
                            put(1, idField.access(object));
                        }})) {
                            List<Object> results = genericResults.getResults(new ResultsMapperImpl<>(dataSource, foreignTableInfo, foreignTableInfo.getFieldTypes(), parents));

                            for (Object foreignObject : results) {
                                foreignField.assign(foreignObject, object);
                            }

                            return results;
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                };

                switch (foreignCollectionFieldType.getCollectionType()) {
                    case LIST:
                        foreignCollectionFieldType.assign(object, new LazyList<Object>(fetcher, (List) collection));
                        break;
                    case SET:
                        foreignCollectionFieldType.assign(object, new LazySet<Object>(fetcher, (Set) collection));
                        break;
                    case UNDEFINED:
                        throw new IllegalArgumentException("Collection with type " + foreignCollectionFieldType.getField().getType() + " not supported lazy loading! Use [List, Set].");
                }
            }
        }

    }

    private Object newObject(Constructor<?> constructor) throws Exception {
        Object object;

        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
            object = constructor.newInstance();
            constructor.setAccessible(false);
        } else {
            object = constructor.newInstance();
        }

        return object;
    }
}
