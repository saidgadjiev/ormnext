package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.field.SqlType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute.AttributeConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent attribute definition.
 *
 * @author Said Gadjiev
 * @see CreateTableQuery
 */
public class AttributeDefinition implements QueryElement {

    /**
     * Attribute constraints.
     *
     * @see AttributeConstraint
     */
    private List<AttributeConstraint> attributeConstraints = new ArrayList<>();

    /**
     * Database column type.
     */
    private DatabaseColumnType databaseColumnType;

    /**
     * Create a new instance.
     *
     * @param databaseColumnType target column type
     */
    public AttributeDefinition(DatabaseColumnType databaseColumnType) {
        this.databaseColumnType = databaseColumnType;
    }

    /**
     * Return current column name.
     *
     * @return current column name
     */
    public String getName() {
        return databaseColumnType.columnName();
    }

    /**
     * Return current column type.
     *
     * @return current column type
     */
    public SqlType getSqlType() {
        return databaseColumnType.ormNextSqlType();
    }

    /**
     * Return current column length.
     *
     * @return current column length
     */
    public int getLength() {
        return databaseColumnType.length();
    }

    /**
     * Return current attribute constraints.
     *
     * @return current attribute constraints
     */
    public List<AttributeConstraint> getAttributeConstraints() {
        return attributeConstraints;
    }

    /**
     * Is id?
     *
     * @return true if attr is id
     */
    public boolean isId() {
        return databaseColumnType.id();
    }

    /**
     * Is generated?
     *
     * @return true if attr is generated
     */
    public boolean isGenerated() {
        return databaseColumnType.generated();
    }

    /**
     * Return current database column type.
     *
     * @return current database column type
     */
    public DatabaseColumnType getDatabaseColumnType() {
        return databaseColumnType;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            attributeConstraints.forEach(attributeConstraint -> attributeConstraint.accept(visitor));
        }
    }
}
