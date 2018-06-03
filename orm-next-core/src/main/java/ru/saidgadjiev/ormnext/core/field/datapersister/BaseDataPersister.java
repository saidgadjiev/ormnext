package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.SqlLiteral;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Base database type that defines persistance methods for the various data types.
 *
 * @author said gadjiev
 */
public abstract class BaseDataPersister implements DataPersister {

    /**
     * Associated classes for this type.
     */
    private final Class<?>[] classes;

    private final int sqlType;

    /**
     * Create a new instance.
     * @param classes associated classes for this type
     * @param sqlType
     */
    protected BaseDataPersister(Class<?>[] classes, int sqlType) {
        this.classes = classes;
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
    public final void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, sqlType);
        } else {
            setNonNullObject(preparedStatement, index, value);
        }
    }

    protected abstract void setNonNullObject(PreparedStatement preparedStatement,
                                             int index,
                                             Object value) throws SQLException;

}
