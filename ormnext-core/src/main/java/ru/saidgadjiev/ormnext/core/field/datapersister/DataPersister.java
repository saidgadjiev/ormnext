package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.dialect.BaseDialect;
import ru.saidgadjiev.ormnext.core.field.SqlType;
import ru.saidgadjiev.ormnext.core.query.visitor.element.AttributeDefinition;
import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.SqlLiteral;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Column data persister.
 *
 * @author Said Gadjiev
 */
public interface DataPersister {

    /**
     * Associated java classes with this type.
     *
     * @return associated classes
     * @see ru.saidgadjiev.ormnext.core.field.DataPersisterManager
     * @see ru.saidgadjiev.ormnext.core.field.DataType
     */
    List<Class<?>> getAssociatedClasses();

    /**
     * Associated ormnext sql type.
     *
     * @return data type
     * @see SqlType
     */
    SqlType getOrmNextSqlType();

    /**
     * Foreign sql type.
     *
     * @return foreign sql type
     *
     *@see SqlType
     */
    SqlType getForeignOrmNextSqlType();

    /**
     * Java sql type.
     *
     * @return sql type
     * @see java.sql.Types
     */
    int getSqlType();

    /**
     * Read value from database results by column name.
     *
     * @param databaseResults target results
     * @param columnLabel     target column name
     * @return read value
     * @throws SQLException any SQL exceptions
     */
    Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException;

    /**
     * Set value to prepared statement {@link PreparedStatement}.
     *
     * @param preparedStatement target prepared statement
     * @param index             target index
     * @param value             target value
     * @throws SQLException any SQL exceptions
     */
    void setObject(PreparableObject preparedStatement, int index, Object value) throws SQLException;

    /**
     * Create sql literal which will be directly append to sql query.
     *
     * @param value target value
     * @return sql literal
     */
    SqlLiteral createLiteral(Object value);

    /**
     * Cast requested value to primary key type.
     *
     * @param value target value
     * @return converted value
     */
    Object convertToPrimaryKey(Object value);

    /**
     * {@link SqlType#OTHER} sql present.
     *
     * @param baseDialect target dialect
     * @param def target attr def
     * @return sql present
     */
    String getOtherTypeSql(BaseDialect baseDialect, AttributeDefinition def);
}
