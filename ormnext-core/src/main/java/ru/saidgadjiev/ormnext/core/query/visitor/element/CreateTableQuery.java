package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.table.TableConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent CREATE TABLE query.
 *
 * @author Said Gadjiev
 */
public class CreateTableQuery implements QueryElement {
    /**
     * Attribute definitions.
     * @see AttributeDefinition
     */
    private final List<AttributeDefinition> attributeDefinitions;

    /**
     * Table constraints.
     * @see TableConstraint
     */
    private List<TableConstraint> tableConstraints = new ArrayList<>();

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * If not exist.
     */
    private boolean ifNotExist;

    /**
     * Create a new instance.
     * @param tableName target table name
     * @param ifNotExist target if not exist
     * @param attributeDefinitions target attribute definitions
     */
    public CreateTableQuery(String tableName,
                             boolean ifNotExist,
                             List<AttributeDefinition> attributeDefinitions) {
        this.ifNotExist = ifNotExist;
        this.tableName = tableName;
        this.attributeDefinitions = attributeDefinitions;
    }

    /**
     * Return is if not exist query.
     * @return is if not exist query
     */
    public boolean isIfNotExist() {
        return ifNotExist;
    }

    /**
     * Return current table name.
     * @return current table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return current attribute definitions.
     * @return current attribute definitions
     */
    public List<AttributeDefinition> getAttributeDefinitions() {
        return attributeDefinitions;
    }

    /**
     * Return current table constraints.
     * @return current table constraints
     */
    public List<TableConstraint> getTableConstraints() {
        return tableConstraints;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            attributeDefinitions.forEach(attributeDefinition -> attributeDefinition.accept(visitor));
            tableConstraints.forEach(tableConstraint -> tableConstraint.accept(visitor));
        }
    }
}
