package ru.saidgadjiev.orm.next.core.db;

import ru.saidgadjiev.orm.next.core.query.core.AttributeDefinition;

import java.sql.SQLException;

public interface DatabaseType {

    String appendPrimaryKey(boolean generated);

    default void loadDriver() throws SQLException {
        String className = getDriverClassName();

        if (className != null) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver class was not found for " + getDatabaseName()
                        + " database.  Missing jar with class " + className + ".", e);
            }
        }
    }

    String getDatabaseName();

    String getDriverClassName();

    String appendNoColumn();

    default String typeToSql(int type, AttributeDefinition attributeDefinition) {
        return null;
    }

    default String getEntityNameEscape() {
        return "`";
    }

    default String getValueEscape() {
        return "'";
    }
}
