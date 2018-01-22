package ru.said.orm.next.core.query.core;

import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.field.field_type.ForeignFieldType;
import ru.said.orm.next.core.query.core.constraints.attribute.GeneratedConstraint;
import ru.said.orm.next.core.query.core.constraints.attribute.NotNullConstraint;
import ru.said.orm.next.core.query.core.constraints.attribute.ReferencesConstraint;
import ru.said.orm.next.core.query.core.constraints.table.TableConstraint;
import ru.said.orm.next.core.query.core.constraints.table.UniqueConstraint;
import ru.said.orm.next.core.query.visitor.QueryElement;
import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.table.TableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateTableQuery implements QueryElement {

    private List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

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

        for (DBFieldType dbFieldType: tableInfo.toDBFieldTypes()) {
            AttributeDefinition attributeDefinition = new AttributeDefinition(dbFieldType.getColumnName(), dbFieldType.getDataType(), dbFieldType.getLength());

            if (dbFieldType.isId() && dbFieldType.isGenerated()) {
                attributeDefinition.getAttributeConstraints().add(new GeneratedConstraint());
            }
            if (dbFieldType.isNotNull()) {
                attributeDefinition.getAttributeConstraints().add(new NotNullConstraint());
            }
            attributeDefinitions.add(attributeDefinition);
        }
        for (ForeignFieldType foreignFieldType: tableInfo.toForeignFieldTypes()) {
            AttributeDefinition attributeDefinition = new AttributeDefinition(foreignFieldType.getColumnName(), foreignFieldType.getDataType(), foreignFieldType.getLength());

            attributeDefinition.getAttributeConstraints()
                    .add(new ReferencesConstraint(
                            foreignFieldType.getForeignTableName(),
                            foreignFieldType.getForeignColumnName())
                    );
            attributeDefinitions.add(attributeDefinition);
        }

        CreateTableQuery createTableQuery = new CreateTableQuery(
                tableInfo.getTableName(),
                ifNotExists,
                attributeDefinitions
        );

        createTableQuery.getTableConstraints().addAll(tableInfo.getUniqueFieldTypes()
                .stream()
                .map(UniqueConstraint::new)
                .collect(Collectors.toList()));

        return createTableQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinition.accept(visitor);
        }
        for (TableConstraint tableConstraint: tableConstraints) {
            tableConstraint.accept(visitor);
        }
        visitor.finish(this);
    }
}
