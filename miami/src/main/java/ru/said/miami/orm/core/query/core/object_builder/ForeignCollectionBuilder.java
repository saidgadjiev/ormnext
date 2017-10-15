package ru.said.miami.orm.core.query.core.object_builder;

import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.field.ForeignCollectionFieldType;
import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.query.core.query_builder.QueryBuilder;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by said on 14.10.17.
 */
public class ForeignCollectionBuilder extends ObjectPartBuilder {

    private TableInfo<?> tableInfo;
    private DataSource dataSource;

    public ForeignCollectionBuilder(TableInfo<?> tableInfo, DataSource dataSource) {
        this.tableInfo = tableInfo;
        this.dataSource = dataSource;
    }

    @Override
    public boolean check(IMiamiData data, Object object) throws Exception {
        for (ForeignCollectionFieldType fieldType : tableInfo.toForeignCollectionFieldTypes()) {
            TableInfo<?> foreignTableInfo = TableInfo.buildTableInfo(fieldType.getForeignFieldClass());
            Dao<?, ?> foreignDao = DaoManager.createDAOWithTableInfo(dataSource, foreignTableInfo);
            QueryBuilder<?> queryBuilder = foreignDao.queryBuilder();

            if (tableInfo.getIdField().isPresent() && foreignTableInfo.getIdField().isPresent()) {
                DBFieldType idField = tableInfo.getIdField().get();
                DBFieldType foreignField = DBFieldType.buildFieldType(fieldType.getForeignField());

                queryBuilder.where().eq(foreignField.getFieldName(), String.valueOf(idField.getValue(object)));
                List<?> foreignObjects = queryBuilder.execute();

                for (Object foreignObject : foreignObjects) {
                    foreignField.assignField(foreignObject, object);
                }

                fieldType.addAll(object, foreignObjects);
            }
        }

        return checkNext(data, object);
    }
}
