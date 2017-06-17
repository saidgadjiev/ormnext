package dao;

import org.sqlite.SQLiteDataSource;
import support.MiamiProperties;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by said on 12.06.17.
 */
public class DataSourceFactory {

    static DataSource createDataSource(String dbType, MiamiProperties properties) {
        switch (dbType) {
            case "sqlite":
                SQLiteDataSource dataSource = new SQLiteDataSource();

                dataSource.setUrl(properties.getUrl());

                return dataSource;
            default:
                break;
        }

        return null;
    }
}
