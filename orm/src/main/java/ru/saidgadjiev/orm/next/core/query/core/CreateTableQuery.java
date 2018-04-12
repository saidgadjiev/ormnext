package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.field.DataPersisterManager;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.Default;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.NotNullConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.PrimaryKeyConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.table.ForeignKeyConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.table.TableConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.table.UniqueConstraint;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateTableQuery implements QueryElement {

    private List<AttributeDefinition> attributeDefinitions;

    private List<TableConstraint> tableConstraints = new ArrayList<>();

    private String typeName;

    private boolean ifNotExists;

    private CreateTableQuery(String typeName,
                             boolean ifNotExists,
                             List<AttributeDefinition> attributeDefinitions) {
        this.ifNotExists = ifNotExists;
        this.typeName = typeName;
        this.attributeDefinitions = attributeDefinitions;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public String getTypeName() {
        return typeName;
    }

    public List<AttributeDefinition> getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public List<TableConstraint> getTableConstraints() {
        return tableConstraints;
    }

    public static CreateTableQuery buildQuery(TableInfo<?> tableInfo, boolean ifNotExists) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        for (IDBFieldType dbFieldType : tableInfo.getFieldTypes()) {
            if (dbFieldType.isForeignCollectionFieldType()) {
                continue;
            }
            AttributeDefinition attributeDefinition = new AttributeDefinition(dbFieldType.getColumnName(), dbFieldType.getDataType(), dbFieldType.getLength());

            if (dbFieldType.isId()) {
                attributeDefinition.getAttributeConstraints().add(new PrimaryKeyConstraint(dbFieldType.isGenerated()));
            }
            if (dbFieldType.isNotNull()) {
                attributeDefinition.getAttributeConstraints().add(new NotNullConstraint());
            }
            if (dbFieldType.getDefaultValue() != null) {
                Literal<?> literal = DataPersisterManager
                        .lookup(dbFieldType.getDefaultValue().getClass())
                        .getLiteral(dbFieldType, dbFieldType.getDefaultValue());
                attributeDefinition.getAttributeConstraints().add(new Default<>(literal));
            }
            attributeDefinitions.add(attributeDefinition);
        }
        CreateTableQuery createTableQuery = new CreateTableQuery(
                tableInfo.getTableName(),
                ifNotExists,
                attributeDefinitions
        );

        for (ForeignFieldType foreignFieldType : tableInfo.toForeignFieldTypes()) {
            createTableQuery
                    .getTableConstraints()
                    .add(new ForeignKeyConstraint(
                            foreignFieldType.getForeignTableName(),
                            foreignFieldType.getForeignColumnName(),
                            foreignFieldType.getColumnName())
                    );
        }

        createTableQuery.getTableConstraints().addAll(tableInfo.getUniqueFieldTypes()
                .stream()
                .map(UniqueConstraint::new)
                .collect(Collectors.toList()));

        return createTableQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            attributeDefinitions.forEach(attributeDefinition -> attributeDefinition.accept(visitor));
            tableConstraints.forEach(tableConstraint -> tableConstraint.accept(visitor));
        }
    }
}
