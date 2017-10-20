package ru.said.miami.orm.core.query.core.object;

import ru.said.miami.orm.core.dao.BaseDaoImpl;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.core.CreateQuery;
import ru.said.miami.orm.core.query.core.FieldConverter;
import ru.said.miami.orm.core.query.core.UpdateValue;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;

public class ObjectCreator<T> {

    private DataSource dataSource;

    private TableInfo<T> tableInfo;

    private CreateQuery query;

    public ObjectCreator(DataSource dataSource, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.tableInfo = tableInfo;
    }

    public ObjectCreator newObject(T object) throws Exception {
        this.query = CreateQuery.buildQuery(tableInfo.getTableName(), null, object);

        return this;
    }

    public ObjectCreator<T> createBase(T object) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (fieldType.isId() && fieldType.isGenerated() || fieldType.isForeign()) {
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
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (fieldType.isForeign()) {
                Object foreignObject = fieldType.access(object);
                TableInfo<?> foreignTableInfo = TableInfo.buildTableInfo(fieldType.getForeignFieldType());

                if (fieldType.isForeignAutoCreate()) {
                    Dao<Object, ?> foreignDao = (BaseDaoImpl<Object, ?>) DaoManager.createDAOWithTableInfo(dataSource, foreignTableInfo);

                    foreignDao.create(foreignObject);
                }

                if (foreignTableInfo.getIdField().isPresent()) {
                    query.add(new UpdateValue(
                            fieldType.getColumnName(),
                            FieldConverter.getInstanse().convert(fieldType.getDataType(), foreignTableInfo.getIdField().get().access(foreignObject)))
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
