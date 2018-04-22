package ru.saidgadjiev.orm.next.core.table.internal.queryspace;

import ru.saidgadjiev.orm.next.core.dao.CriteriaQuery;
import ru.saidgadjiev.orm.next.core.field.DataPersisterManager;
import ru.saidgadjiev.orm.next.core.field.fieldtype.*;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromJoinedTables;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.DisplayedColumn;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.DisplayedColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.core.common.UpdateValue;
import ru.saidgadjiev.orm.next.core.query.core.condition.Equals;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.Default;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.NotNullConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.PrimaryKeyConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.table.ForeignKeyConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.table.UniqueConstraint;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.core.join.LeftJoin;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;
import ru.saidgadjiev.orm.next.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class EntityQuerySpace  {

    private SelectColumnsList selectColumnsList = new SelectColumnsList();

    private FromJoinedTables fromJoinedTables;

    private EntityAliases rootEntityAliases;

    private DatabaseEntityMetadata<?> rootEntityMetaData;

    public EntityQuerySpace(DatabaseEntityMetadata<?> rootEntityMetaData, EntityAliases rootEntityAliases) {
        this.rootEntityMetaData = rootEntityMetaData;
        this.rootEntityAliases = rootEntityAliases;
        fromJoinedTables = new FromJoinedTables(new TableRef(rootEntityMetaData.getTableName()).alias(new Alias(rootEntityAliases.getTableAlias())));
    }

    public void appendJoin(ForeignColumnType foreignColumnType, EntityAliases ownerAliases, EntityAliases joinTableAliases) {
        Expression onExpression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(
                new Equals(
                        new ColumnSpec(foreignColumnType.getColumnName()).alias(new Alias(ownerAliases.getTableAlias())),
                        new ColumnSpec(foreignColumnType.getForeignPrimaryKey().getColumnName()).alias(new Alias(joinTableAliases.getTableAlias()))
                )
        );
        onExpression.add(andCondition);
        fromJoinedTables.add(new LeftJoin(new TableRef(foreignColumnType.getForeignTableName()).alias(new Alias(joinTableAliases.getTableAlias())), onExpression));
    }

    public void appendCollectionJoin(String ownerPrimaryKeyColumnName,
                                     ForeignCollectionColumnType foreignCollectionColumnType,
                                     EntityAliases ownerAliases,
                                     EntityAliases joinTableAliases) {
        Expression onExpression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(
                new Equals(
                        new ColumnSpec(ownerPrimaryKeyColumnName).alias(new Alias(ownerAliases.getTableAlias())),
                        new ColumnSpec(foreignCollectionColumnType.getForeignColumnName()).alias(new Alias(joinTableAliases.getTableAlias()))
                )
        );
        onExpression.add(andCondition);
        fromJoinedTables.add(new LeftJoin(new TableRef(foreignCollectionColumnType.getForeignTableName()).alias(new Alias(joinTableAliases.getTableAlias())), onExpression));
    }

    public void appendSelectColumns(EntityAliases aliases, DatabaseEntityMetadata<?> databaseEntityMetadata) {
        List<IDatabaseColumnType> columnTypes = databaseEntityMetadata.getFieldTypes();

        for (int i = 0, a = 0; i < columnTypes.size(); ++i) {
            IDatabaseColumnType columnType = columnTypes.get(i);

            if (columnTypes.get(i).isId()) {
                appendDisplayedColumn(columnType.getColumnName(), aliases.getTableAlias(), aliases.getKeyAlias());
                continue;
            }
            if (columnTypes.get(i).isForeignCollectionFieldType()) {
                continue;
            }
            appendDisplayedColumn(columnType.getColumnName(), aliases.getTableAlias(), aliases.getColumnAliases().get(a++));
        }
    }

    private void appendDisplayedColumn(String columnName, String tableAlias, String columnAlias) {
        ColumnSpec columnSpec = new ColumnSpec(columnName).alias(new Alias(tableAlias));
        DisplayedColumnSpec displayedColumnSpec = new DisplayedColumn(columnSpec);

        displayedColumnSpec.setAlias(new Alias(columnAlias));
        selectColumnsList.addColumn(displayedColumnSpec);
    }

    public Select getSelectById() {
        Select selectById = new Select();

        selectById.setSelectColumnsStrategy(selectColumnsList);
        selectById.setFrom(fromJoinedTables);
        AndCondition andCondition = new AndCondition();
        Expression where = new Expression();

        andCondition.add(new Equals(new ColumnSpec(rootEntityMetaData.getPrimaryKey().getColumnName()).alias(new Alias(rootEntityAliases.getTableAlias())), new Param()));
        where.getConditions().add(andCondition);
        selectById.setWhere(where);

        return selectById;
    }

    public Select getSelectAll() {
        Select selectAll = new Select();

        selectAll.setSelectColumnsStrategy(selectColumnsList);
        selectAll.setFrom(fromJoinedTables);

        return selectAll;
    }

    public CreateQuery getCreateQuery() {
        CreateQuery createQuery = new CreateQuery(rootEntityMetaData.getTableName());
        InsertValues insertValues = new InsertValues();

        for (IDatabaseColumnType columnType : rootEntityMetaData.getFieldTypes()) {
            if (columnType.isId() && columnType.isGenerated()) {
                continue;
            }
            if (columnType.isForeignCollectionFieldType()) {
                continue;
            }
            createQuery.addColumnName(columnType.getColumnName());
            insertValues.add(new UpdateValue(
                    columnType.getColumnName(),
                    new Param())
            );
        }
        createQuery.add(insertValues);

        return createQuery;
    }

    public CreateQuery getCreateQuery(int objectCount) {
        CreateQuery createQuery = new CreateQuery(rootEntityMetaData.getTableName());

        for (int i = 0; i <  objectCount; ++i) {
            InsertValues insertValues = new InsertValues();

            for (IDatabaseColumnType columnType : rootEntityMetaData.getFieldTypes()) {
                if (columnType.isId() && columnType.isGenerated()) {
                    continue;
                }
                if (columnType.isForeignCollectionFieldType()) {
                    continue;
                }
                insertValues.add(new UpdateValue(
                        columnType.getColumnName(),
                        new Param())
                );
            }
            createQuery.add(insertValues);
        }

        return createQuery;
    }

    public CreateTableQuery createTableQuery(boolean ifNotExists) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        for (IDatabaseColumnType dbFieldType : rootEntityMetaData.getFieldTypes()) {
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
                rootEntityMetaData.getTableName(),
                ifNotExists,
                attributeDefinitions
        );

        for (ForeignColumnType foreignColumnType : rootEntityMetaData.toForeignFieldTypes()) {
            createTableQuery
                    .getTableConstraints()
                    .add(new ForeignKeyConstraint(
                            foreignColumnType.getForeignTableName(),
                            foreignColumnType.getForeignColumnName(),
                            foreignColumnType.getColumnName())
                    );
        }

        createTableQuery.getTableConstraints().addAll(rootEntityMetaData.getUniqueFieldTypes()
                .stream()
                .map(UniqueConstraint::new)
                .collect(Collectors.toList()));

        return createTableQuery;
    }

    public DropTableQuery getDropTableQuery(boolean ifExists) {
        return new DropTableQuery(
                rootEntityMetaData.getTableName(),
                ifExists
        );
    }

    public UpdateQuery getUpdateQuery() {
        UpdateQuery updateQuery = new UpdateQuery(rootEntityMetaData.getTableName());

        for (DatabaseColumnType fieldType : rootEntityMetaData.toDBFieldTypes()) {
            updateQuery.add(
                    new UpdateValue(
                            fieldType.getColumnName(),
                            new Param())
            );
        }
        AndCondition andCondition = new AndCondition();

        andCondition.add(new Equals(new ColumnSpec(rootEntityMetaData.getPrimaryKey().getColumnName()).alias(new Alias(rootEntityMetaData.getTableName())), new Param()));
        updateQuery.getWhere().getConditions().add(andCondition);

        return updateQuery;
    }

    public DeleteQuery getDeleteQuery() {
        DeleteQuery deleteQuery = new DeleteQuery(rootEntityMetaData.getTableName());
        AndCondition andCondition = new AndCondition();

        andCondition.add(new Equals(new ColumnSpec(rootEntityMetaData.getPrimaryKey().getColumnName()).alias(new Alias(rootEntityMetaData.getTableName())), new Param()));
        deleteQuery.getWhere().getConditions().add(andCondition);

        return deleteQuery;
    }

    public Iterator<CreateIndexQuery> getCreateIndexQuery() {
        Iterator<IndexFieldType> indexFieldTypeIterator = rootEntityMetaData.getIndexFieldTypes().iterator();

        return new Iterator<CreateIndexQuery>() {
            @Override
            public boolean hasNext() {
                return indexFieldTypeIterator.hasNext();
            }

            @Override
            public CreateIndexQuery next() {
                return new CreateIndexQuery(indexFieldTypeIterator.next());
            }
        };
    }

    public Iterator<DropIndexQuery> getDropIndexQuery() {
        Iterator<IndexFieldType> indexFieldTypeIterator = rootEntityMetaData.getIndexFieldTypes().iterator();

        return new Iterator<DropIndexQuery>() {
            @Override
            public boolean hasNext() {
                return indexFieldTypeIterator.hasNext();
            }

            @Override
            public DropIndexQuery next() {
                return new DropIndexQuery(indexFieldTypeIterator.next().getName());
            }
        };
    }

    public Select countOff() {
        Select select = new Select();
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectColumnsList.addColumn(new DisplayedOperand(new CountAll()));
        select.setSelectColumnsStrategy(selectColumnsList);

        return select;
    }

    public Select getByCriteria(CriteriaQuery criteria) {
        Select select = new Select();

        select.setSelectColumnsStrategy(selectColumnsList);
        select.setFrom(fromJoinedTables);
        QuerySpaceVisitor visitor = new QuerySpaceVisitor(rootEntityMetaData, rootEntityAliases);

        criteria.accept(visitor);
        select.setWhere(criteria.getWhere());
        select.setGroupBy(criteria.getGroupBy());
        select.setOrderBy(criteria.getOrderBy());
        select.setHaving(criteria.getHaving());
        select.setLimit(criteria.getLimit());
        select.setOffset(criteria.getOffset());

        return select;
    }
}
