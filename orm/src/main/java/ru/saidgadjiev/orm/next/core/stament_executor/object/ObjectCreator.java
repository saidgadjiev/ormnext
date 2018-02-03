package ru.saidgadjiev.orm.next.core.stament_executor.object;

import ru.saidgadjiev.orm.next.core.dao.BaseDaoImpl;
import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.dao.DaoManager;
import ru.saidgadjiev.orm.next.core.field.field_type.DBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.query.core.CreateQuery;
import ru.saidgadjiev.orm.next.core.query.core.common.UpdateValue;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectCreator<T> {

    private ConnectionSource dataSource;

    private TableInfo<T> tableInfo;

    private CreateQuery query;

    private AtomicInteger index = new AtomicInteger();

    private Map<Integer, Object> args = new HashMap<>();

    public ObjectCreator(ConnectionSource dataSource, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.tableInfo = tableInfo;
    }

    public ObjectCreator newObject() throws Exception {
        this.query = CreateQuery.buildQuery(tableInfo.getTableName());

        return this;
    }

    public ObjectCreator<T> createBase(T object) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (fieldType.isId() && fieldType.isGenerated()) {
                continue;
            }
            Object value = fieldType.access(object);

            if (value != null) {
                args.put(index.incrementAndGet(), value);
                query.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }
        }

        return this;
    }

    public ObjectCreator<T> createForeign(T object) throws Exception {
        for (ForeignFieldType fieldType : tableInfo.toForeignFieldTypes()) {
            Object foreignObject = fieldType.access(object);
            TableInfo<?> foreignTableInfo = TableInfo.TableInfoCache.build(fieldType.getForeignFieldClass());

            if (foreignObject != null) {
                if (fieldType.isForeignAutoCreate()) {
                    Dao<Object, ?> foreignDao = (BaseDaoImpl<Object, ?>) DaoManager.createDAOWithTableInfo(dataSource, foreignTableInfo);

                    foreignDao.create(foreignObject);
                }

                //TODO: эту проверку заменить на fieldType.getForeignPrimarykey
                if (foreignTableInfo.getPrimaryKey().isPresent()) {
                    Object value = fieldType.getForeignPrimaryKey().access(foreignObject);

                    args.put(index.incrementAndGet(), value);
                    query.add(new UpdateValue(
                            fieldType.getColumnName(),
                            new Param())
                    );
                }
            }
        }

        return this;
    }

    public Map<Integer, Object> getArgs() {
        return args;
    }

    public CreateQuery query() {
        return query;
    }
}
