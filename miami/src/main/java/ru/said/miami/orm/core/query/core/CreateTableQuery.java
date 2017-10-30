package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.UniqueFieldType;
import ru.said.miami.orm.core.query.AttributeDefenition;
import ru.said.miami.orm.core.query.core.constraints.PrimaryKeyConstraint;
import ru.said.miami.orm.core.query.core.constraints.UniqueConstraint;
import ru.said.miami.orm.core.query.core.defenitions.PrimaryKeyAttributeDefenition;
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

    private List<AttributeDefenition> attributeDefenitions = new ArrayList<>();

    private List<TableConstraint> tableConstraints = new ArrayList<>();

    private String typeName;

    private QueryVisitor visitor;

    private CreateTableQuery(String typeName, List<AttributeDefenition> attributeDefenitions, QueryVisitor defaultVisitor) {
        this.visitor = defaultVisitor;
        this.typeName = typeName;
        this.attributeDefenitions = attributeDefenitions;
    }

    public String getTypeName() {
        return typeName;
    }

    public List<AttributeDefenition> getAttributeDefenitions() {
        return attributeDefenitions;
    }

    public List<TableConstraint> getTableConstraints() {
        return tableConstraints;
    }

    public static CreateTableQuery buildQuery(TableInfo<?> tableInfo) {
        List<AttributeDefenition> attributeDefenitions = new ArrayList<>();

        attributeDefenitions.addAll(
                tableInfo.toDBFieldTypes()
                        .stream()
                        .map(DBFieldTypeDefenition::new)
                        .collect(Collectors.toList())
        );
        attributeDefenitions.addAll(
                tableInfo.toForeignFieldTypes()
                        .stream()
                        .map(ForeignFieldTypeDefenition::new)
                        .collect(Collectors.toList())
        );
        attributeDefenitions.add(new PrimaryKeyAttributeDefenition(tableInfo.getIdField().get()));

        CreateTableQuery createTableQuery = new CreateTableQuery(
                tableInfo.getTableName(),
                attributeDefenitions,
                new DefaultVisitor()
        );
        createTableQuery.getTableConstraints().add(new PrimaryKeyConstraint(tableInfo.getIdField().get()));
        for (UniqueFieldType uniqueFieldType: tableInfo.getUniqueFieldTypes()) {
            createTableQuery.getTableConstraints().add(new UniqueConstraint(uniqueFieldType));
        }

        return createTableQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        for (AttributeDefenition attributeDefenition: attributeDefenitions) {
            attributeDefenition.accept(visitor);
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
