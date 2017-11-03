package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.field.ForeignFieldType;
import ru.said.miami.orm.core.query.core.constraints.attribute.GeneratedConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.NotNullConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.ReferencesConstraint;
import ru.said.miami.orm.core.query.core.constraints.table.TableConstraint;
import ru.said.miami.orm.core.query.core.constraints.table.UniqueConstraint;
import ru.said.miami.orm.core.query.core.defenitions.AttributeDefinition;
import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;
import ru.said.miami.orm.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateTableQuery implements Query<Boolean>, QueryElement {

    private List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

    private List<TableConstraint> tableConstraints = new ArrayList<>();

    private String typeName;

    private QueryVisitor visitor;

    private CreateTableQuery(String typeName, List<AttributeDefinition> attributeDefinitions, QueryVisitor defaultVisitor) {
        this.visitor = defaultVisitor;
        this.typeName = typeName;
        this.attributeDefinitions = attributeDefinitions;
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

    public static CreateTableQuery buildQuery(TableInfo<?> tableInfo) {
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
            AttributeDefinition attributeDefinition = new AttributeDefinition(foreignFieldType.getColumnName(), foreignFieldType.getDataType(), foreignFieldType.getDbFieldType().getLength());

            attributeDefinition.getAttributeConstraints()
                    .add(new ReferencesConstraint(
                            foreignFieldType.getForeignTableName(),
                            foreignFieldType.getForeignColumnName())
                    );
            attributeDefinitions.add(attributeDefinition);
        }
        CreateTableQuery createTableQuery = new CreateTableQuery(
                tableInfo.getTableName(),
                attributeDefinitions,
                new DefaultVisitor()
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

    @Override
    public Boolean execute(Connection connection) throws SQLException {
        this.accept(visitor);
        String sql = visitor.getQuery();

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);

            return true;
        }
    }
}
