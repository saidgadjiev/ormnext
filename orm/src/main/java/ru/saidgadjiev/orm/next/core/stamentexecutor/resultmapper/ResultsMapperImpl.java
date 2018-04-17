package ru.saidgadjiev.orm.next.core.stamentexecutor.resultmapper;

import ru.saidgadjiev.orm.next.core.criteria.impl.Criteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.dao.SessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.field.FetchType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignCollectionFieldType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnTypeFactory;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.GenericResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.collection.LazyList;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.collection.LazySet;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

public class ResultsMapperImpl<T> implements ResultsMapper<T> {

    private ConnectionSource dataSource;

    private Map<String, String> columnAliasMap;

    private DatabaseEntityMetadata<T> databaseEntityMetadata;

    private Set<Class<?>> parents;

    public ResultsMapperImpl(ConnectionSource dataSource,
                             DatabaseEntityMetadata<T> databaseEntityMetadata,
                             Map<String, String> columnAliasMap,
                             Set<Class<?>> parents) {
        this.dataSource = dataSource;
        this.columnAliasMap = columnAliasMap;
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.parents = parents;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {
        T object = (T) newObject(databaseEntityMetadata.getConstructor());

        parents.add(databaseEntityMetadata.getTableClass());

        for (IDatabaseColumnType fieldType : databaseEntityMetadata.getFieldTypes()) {
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

    private void buildBase(T object, DatabaseResults data, IDatabaseColumnType fieldType) throws Exception {
        if (fieldType.isDbFieldType()) {
            String columnName = databaseEntityMetadata.getTableName() + "." + fieldType.getColumnName();

            if (columnAliasMap.containsKey(columnName)) {
                Object value = fieldType.getDataPersister().parseSqlToJava(fieldType, data.getObject(columnAliasMap.get(columnName)));

                fieldType.assign(object, value);
            }
        }
    }

    private void buildForeign(T object, DatabaseResults data, IDatabaseColumnType fieldType, Set<Class<?>> parents) throws Exception {
        ForeignColumnType foreignColumnType = (ForeignColumnType) fieldType;

        if (!parents.contains(foreignColumnType.getForeignFieldClass())) {
            DatabaseEntityMetadata<?> foreignDatabaseEntityMetadata = TableInfoManager.buildOrGet(foreignColumnType.getForeignFieldClass());
            Object foreignObject = new ResultsMapperImpl<>(dataSource, foreignDatabaseEntityMetadata, columnAliasMap, new HashSet<>(parents)).mapResults(data);

            foreignColumnType.assign(object, foreignObject);
        }
    }

    private void buildForeignCollection(T object, IDatabaseColumnType fieldType, Set<Class<?>> parents) throws Exception {
        ForeignCollectionFieldType foreignCollectionFieldType = (ForeignCollectionFieldType) fieldType;
        DatabaseEntityMetadata<Object> foreignDatabaseEntityMetadata = TableInfoManager.buildOrGet((Class<Object>) foreignCollectionFieldType.getForeignFieldClass());
        Session foreignDao = new SessionManagerImpl(dataSource, null).getCurrentSession();

        if (databaseEntityMetadata.getPrimaryKey().isPresent() && foreignDatabaseEntityMetadata.getPrimaryKey().isPresent()) {
            IDatabaseColumnType idField = databaseEntityMetadata.getPrimaryKey().get();
            ForeignColumnType foreignField = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(foreignCollectionFieldType.getForeignField());
            SelectStatement<?> selectStatement = new SelectStatement<>(foreignDatabaseEntityMetadata.getTableClass());

            selectStatement.where(new Criteria().add(Restrictions.eq(foreignField.getFieldName(), idField.access(object))));
            DefaultVisitor visitor = new DefaultVisitor(dataSource.getDatabaseType());

            selectStatement.accept(visitor);
            if (foreignCollectionFieldType.getFetchType().equals(FetchType.EAGER)) {
                try (GenericResults genericResults = foreignDao.query(selectStatement)) {
                    ResultsMapper resultsMapper = new ResultsMapperImpl<>(dataSource, foreignDatabaseEntityMetadata, columnAliasMap, new HashSet<>(parents));
                    List objects = genericResults.getResults(resultsMapper);

                    for (Object foreignObject : objects) {
                        foreignField.assign(foreignObject, object);
                    }

                    foreignCollectionFieldType.addAll(object, objects);
                }
            } else {
                Collection<?> collection = (Collection<?>) foreignCollectionFieldType.access(object);
                Supplier<List> fetcher = () -> {
                    try {
                        try (GenericResults genericResults = foreignDao.query(selectStatement)) {
                            ResultsMapper resultsMapper = new ResultsMapperImpl<>(dataSource, foreignDatabaseEntityMetadata, columnAliasMap, new HashSet<>(parents));
                            List results = genericResults.getResults(resultsMapper);

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
                        foreignCollectionFieldType.assign(object, new LazyList(fetcher, (List) collection));
                        break;
                    case SET:
                        foreignCollectionFieldType.assign(object, new LazySet(fetcher, (Set) collection));
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
