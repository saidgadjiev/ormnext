package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.dialect.BaseDialect;
import ru.saidgadjiev.ormnext.core.field.SqlType;
import ru.saidgadjiev.ormnext.core.query.visitor.element.AttributeDefinition;
import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.SqlLiteral;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Base database type that defines persistance methods for the various data types.
 *
 * @author Said Gadjiev
 */
public abstract class BaseDataPersister implements DataPersister {

    /**
     * Associated classes for this type.
     */
    private final Class<?>[] classes;

    /**
     * Associated java sql type.
     *
     * @see java.sql.Types
     */
    private final int javaSqlType;

    /**
     * Associated ormnext sql type.
     *
     * @see SqlType
     */
    private SqlType sqlType;

    /**
     * Create a new instance.
     *
     * @param classes     associated classes for this type
     * @param javaSqlType target associated sql type
     * @param sqlType     target associated sql type
     */
    protected BaseDataPersister(Class<?>[] classes, int javaSqlType, SqlType sqlType) {
        this.classes = classes;
        this.javaSqlType = javaSqlType;
        this.sqlType = sqlType;
    }

    @Override
    public String toString() {
        return "classes=" + Arrays.toString(classes);
    }

    @Override
    public List<Class<?>> getAssociatedClasses() {
        return Arrays.asList(classes);
    }

    @Override
    public SqlLiteral createLiteral(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SqlType getOrmNextSqlType() {
        return sqlType;
    }

    @Override
    public SqlType getForeignOrmNextSqlType() {
        return sqlType;
    }

    @Override
    public final void setObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, javaSqlType);
        } else {
            setNonNullObject(preparedStatement, index, value);
        }
    }

    @Override
    public Object convertToPrimaryKey(Object value) {
        return value;
    }

    /**
     * Set non null value to prepared statement.
     *
     * @param preparedStatement target statement
     * @param index             target value index
     * @param value             target value
     * @throws SQLException any SQL exceptions
     */
    protected abstract void setNonNullObject(PreparableObject preparedStatement,
                                             int index,
                                             Object value) throws SQLException;

    @Override
    public String getOtherTypeSql(BaseDialect baseDialect, AttributeDefinition def) {
        return null;
    }

    @Override
    public int getSqlType() {
        return javaSqlType;
    }
}
