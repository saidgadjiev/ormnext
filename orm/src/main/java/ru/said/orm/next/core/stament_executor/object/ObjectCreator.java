package ru.said.orm.next.core.stament_executor.object;

import ru.said.orm.next.core.dao.BaseDaoImpl;
import ru.said.orm.next.core.dao.Dao;
import ru.said.orm.next.core.dao.DaoManager;
import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.field.field_type.ForeignFieldType;
import ru.said.orm.next.core.query.core.CreateQuery;
import ru.said.orm.next.core.query.core.common.UpdateValue;
import ru.said.orm.next.core.stament_executor.FieldConverter;
import ru.said.orm.next.core.support.ConnectionSource;
import ru.said.orm.next.core.table.TableInfo;

public class ObjectCreator<T> {

    private ConnectionSource dataSource;

    private TableInfo<T> tableInfo;

    private CreateQuery query;

    public ObjectCreator(ConnectionSource dataSource, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.tableInfo = tableInfo;
    }

    public ObjectCreator newObject(T object) throws Exception {
        this.query = CreateQuery.buildQuery(tableInfo.getTableName());

        return this;
    }

    public ObjectCreator<T> createBase(T object) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (fieldType.isId() && fieldType.isGenerated()) {
                continue;
            }
            query.add(new UpdateValue(
                    fieldType.getColumnName(),
                    FieldConverter.getInstanse().convert(fieldType.getDataType(), fieldType.access(object)))
            );
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
                if (foreignTableInfo.getPrimaryKeys().isPresent()) {
                    query.add(new UpdateValue(
                            fieldType.getColumnName(),
                            FieldConverter.getInstanse().convert(fieldType.getDataType(), fieldType.getForeignPrimaryKey().access(foreignObject)))
                    );
                }
            }
        }

        return this;
    }

    public CreateQuery query() {
        return query;
    }
}
