package ru.saidgadjiev.ormnext.core.query.visitor.element;

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
     * Column name.
     */
    private final String columnName;

    /**
     * Column type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.DataType
     */
    private final int dataType;

    /**
     * Column length.
     */
    private final int length;

    /**
     * Attribute constraints.
     *
     * @see AttributeConstraint
     */
    private List<AttributeConstraint> attributeConstraints = new ArrayList<>();

    /**
     * Is id?.
     */
    private boolean id;

    /**
     * Is generated?
     */
    private boolean generated;

    /**
     * Create a new instance.
     *
     * @param columnName target column name.
     * @param dataType   target column type
     * @param length     target column length
     * @param id         true if attr id
     * @param generated  true if attr generated
     */
    public AttributeDefinition(String columnName, int dataType, int length, boolean id, boolean generated) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.length = length;
        this.id = id;
        this.generated = generated;
    }

    /**
     * Return current column name.
     *
     * @return current column name
     */
    public String getName() {
        return columnName;
    }

    /**
     * Return current column type.
     *
     * @return current column type
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Return current column length.
     *
     * @return current column length
     */
    public int getLength() {
        return length;
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
        return id;
    }

    /**
     * Is generated?
     *
     * @return true if attr is generated
     */
    public boolean isGenerated() {
        return generated;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            attributeConstraints.forEach(attributeConstraint -> attributeConstraint.accept(visitor));
        }
    }
}
