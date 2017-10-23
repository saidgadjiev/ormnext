package ru.said.miami.orm.core.dao;

import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Класс для создания DAO
 */
public class DaoManager {

    public static <T, ID> Dao<T, ID> createDAO(DataSource dataSource, Class<T> clazz) throws SQLException {
        try {
            return BaseDaoImpl.createDao(dataSource, TableInfo.buildTableInfo(clazz));
        } catch (NoSuchMethodException | NoSuchFieldException ex) {
            throw new SQLException(ex);
        }
    }

    public static <T, ID> Dao<T, ?> createDAOWithTableInfo(DataSource dataSource, TableInfo<T> tableInfo) {
        return BaseDaoImpl.createDao(dataSource, tableInfo);
    }
}
