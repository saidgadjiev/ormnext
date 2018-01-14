package ru.said.miami.orm.core.stamentExecutor.object;

import ru.said.miami.orm.core.dao.BaseDaoImpl;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;
import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.field.fieldTypes.ForeignFieldType;
import ru.said.miami.orm.core.query.core.CreateQuery;
import ru.said.miami.orm.core.stamentExecutor.FieldConverter;
import ru.said.miami.orm.core.query.core.common.UpdateValue;
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
