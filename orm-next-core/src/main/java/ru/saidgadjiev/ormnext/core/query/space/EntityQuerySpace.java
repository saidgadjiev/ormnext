package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.databasetype.DatabaseType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.loader.Argument;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.visitor.element.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.from.FromJoinedTables;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.from.FromTable;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.select.SelectColumnsList;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedColumn;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedOperand;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.UpdateValue;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Equals;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.table.ForeignKeyConstraint;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.table.UniqueConstraint;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.CountAll;
import ru.saidgadjiev.ormnext.core.query.visitor.element.join.LeftJoin;
import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.Param;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Query space for entity. It contains prepared join expression, resolved aliases and select columns list
 * and methods for create statements for different operations eg. select by id, select all...
 *
 * @author said gadjiev
 */
public class EntityQuerySpace {

    /**
     * SelectQuery columns.
     *
     * @see SelectColumnsList
     */
    private SelectColumnsList selectColumnsList = new SelectColumnsList();

    /**
     * Join expression.
     *
     * @see FromJoinedTables
     */
    private FromJoinedTables fromJoinedTables;

    /**
     * Table aliases.
     *
     * @see EntityAliases
     */
    private EntityAliases rootEntityAliases;

    /**
     * Table meta data.
     *
     * @see DatabaseEntityMetadata
     */
    private DatabaseEntityMetadata<?> rootEntityMetaData;

    /**
     * Create a new instance.
     *
     * @param rootEntityMetaData root table meta data
     * @param rootEntityAliases  root table aliases
     */
    public EntityQuerySpace(DatabaseEntityMetadata<?> rootEntityMetaData, EntityAliases rootEntityAliases) {
        this.rootEntityMetaData = rootEntityMetaData;
        this.rootEntityAliases = rootEntityAliases;
        TableRef tableRef = new TableRef(rootEntityMetaData.getTableName(), rootEntityAliases.getTableAlias());

        fromJoinedTables = new FromJoinedTables(tableRef);
    }

    /**
     * Append join.
     *
     * @param foreignColumnType target column
     * @param ownerAliases      owner aliases
     * @param joinTableAliases  join table aliases
     */
    public void appendJoin(ForeignColumnType foreignColumnType,
                           EntityAliases ownerAliases,
                           EntityAliases joinTableAliases) {
        Expression onExpression = new Expression();
        AndCondition andCondition = new AndCondition();
        IDatabaseColumnType foreignPrimaryKeyType = foreignColumnType.getForeignPrimaryKey();

        andCondition.add(
                new Equals(
                        new ColumnSpec(foreignColumnType.getColumnName(), ownerAliases.getTableAlias()),
                        new ColumnSpec(foreignPrimaryKeyType.getColumnName(), joinTableAliases.getTableAlias())
                )
        );
        onExpression.add(andCondition);
        TableRef joinedTableRef = new TableRef(
                foreignColumnType.getForeignTableName(),
                joinTableAliases.getTableAlias()
        );

        fromJoinedTables.add(new LeftJoin(joinedTableRef, onExpression));
    }

    /**
     * Append join for {@link ru.saidgadjiev.ormnext.core.field.ForeignCollectionField} column.
     *
     * @param ownerPrimaryKeyColumnName   owner primary key column name
     * @param foreignCollectionColumnType column type
     * @param ownerAliases                owner aliases
     * @param joinTableAliases            join table aliases
     */
    public void appendCollectionJoin(String ownerPrimaryKeyColumnName,
                                     ForeignCollectionColumnType foreignCollectionColumnType,
                                     EntityAliases ownerAliases,
                                     EntityAliases joinTableAliases) {
        Expression onExpression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(
                new Equals(
                        new ColumnSpec(ownerPrimaryKeyColumnName, ownerAliases.getTableAlias()),
                        new ColumnSpec(
                                foreignCollectionColumnType.getForeignColumnName(),
                                joinTableAliases.getTableAlias()
                        )
                )
        );
        onExpression.add(andCondition);
        TableRef joinedTableRef = new TableRef(
                foreignCollectionColumnType.getForeignTableName(),
                joinTableAliases.getTableAlias()
        );

        fromJoinedTables.add(new LeftJoin(joinedTableRef, onExpression));
    }

    /**
     * Append select columns.
     *
     * @param aliases                table aliases
     * @param databaseEntityMetadata table meta data
     */
    public void appendSelectColumns(EntityAliases aliases, DatabaseEntityMetadata<?> databaseEntityMetadata) {
        List<IDatabaseColumnType> columnTypes = databaseEntityMetadata.getColumnTypes();
        List<String> columnAliases = aliases.getColumnAliases();

        for (int i = 0, a = 0; i < columnTypes.size(); ++i) {
            IDatabaseColumnType columnType = columnTypes.get(i);

            if (columnTypes.get(i).isId()) {
                appendDisplayedColumn(columnType.getColumnName(), aliases.getTableAlias(), aliases.getKeyAlias());
                continue;
            }
            if (columnTypes.get(i).isForeignCollectionColumnType()) {
                continue;
            }
            appendDisplayedColumn(columnType.getColumnName(), aliases.getTableAlias(), columnAliases.get(a++));
        }
    }

    /**
     * Append column spec.
     *
     * @param columnName  column name
     * @param tableAlias  table alias
     * @param columnAlias column alias
     */
    private void appendDisplayedColumn(String columnName, String tableAlias, String columnAlias) {
        ColumnSpec columnSpec = new ColumnSpec(columnName, tableAlias);
        DisplayedColumnSpec displayedColumnSpec = new DisplayedColumn(columnSpec);

        displayedColumnSpec.setAlias(new Alias(columnAlias));
        selectColumnsList.addColumn(displayedColumnSpec);
    }

    /**
     * Make and return select by id.
     *
     * @return select statement
     */
    public SelectQuery getSelectById() {
        SelectQuery selectById = new SelectQuery();

        selectById.setSelectColumnsStrategy(selectColumnsList);
        selectById.setFrom(fromJoinedTables);
        AndCondition andCondition = new AndCondition();
        Expression where = new Expression();
        ColumnSpec idColumnSpec = new ColumnSpec(
                rootEntityMetaData.getPrimaryKeyColumnType().getColumnName(),
                rootEntityAliases.getTableAlias()
        );

        andCondition.add(new Equals(idColumnSpec, new Param()));
        where.getConditions().add(andCondition);
        selectById.setWhere(where);

        return selectById;
    }

    /**
     * Make and return select all.
     *
     * @return select statement
     */
    public SelectQuery getSelectAll() {
        SelectQuery selectAll = new SelectQuery();

        selectAll.setSelectColumnsStrategy(selectColumnsList);
        selectAll.setFrom(fromJoinedTables);

        return selectAll;
    }

    /**
     * Make and return create statement.
     *
     * @param resultColumnTypes result columns in statement
     * @return create statement
     */
    public CreateQuery getCreatedQuery(Collection<IDatabaseColumnType> resultColumnTypes) {
        CreateQuery createQuery = new CreateQuery(rootEntityMetaData.getTableName());
        InsertValues insertValues = new InsertValues();

        for (IDatabaseColumnType columnType : resultColumnTypes) {
            if (columnType.isId() && columnType.isGenerated()) {
                continue;
            }
            if (columnType.isForeignCollectionColumnType()) {
                continue;
            }
            createQuery.addColumnName(columnType.getColumnName());
            insertValues.add(new Param());
        }
        createQuery.add(insertValues);

        return createQuery;
    }

    public CreateQuery getCreateQueryCompiledStatement(Map<IDatabaseColumnType, Argument> argumentMap) {
        CreateQuery createQuery = new CreateQuery(rootEntityMetaData.getTableName());
        InsertValues insertValues = new InsertValues();

        for (Map.Entry<IDatabaseColumnType, Argument> entry : argumentMap.entrySet()) {
            IDatabaseColumnType columnType = entry.getKey();

            createQuery.addColumnName(columnType.getColumnName());
            insertValues.add(columnType.getDataPersister().createLiteral(entry.getValue().getValue()));
        }
        createQuery.add(insertValues);

        return createQuery;
    }

    /**
     * Make and return create table statement.
     *
     * @param ifNotExist append if not exist
     * @return create table statement
     */
    public CreateTableQuery createTableQuery(DatabaseType databaseType, boolean ifNotExist) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        CreateTableQuery createTableQuery = new CreateTableQuery(
                rootEntityMetaData.getTableName(),
                ifNotExist,
                attributeDefinitions
        );

        for (IDatabaseColumnType columnType : rootEntityMetaData.getColumnTypes()) {
            if (columnType.isForeignCollectionColumnType()) {
                continue;
            }
            AttributeDefinition attributeDefinition = new AttributeDefinition(
                    columnType.getColumnName(),
                    columnType.getDataType(),
                    columnType.getLength()
            );

            if (columnType.isId()) {
                attributeDefinition.getAttributeConstraints().add(new PrimaryKeyConstraint(columnType.isGenerated()));
            }
            if (columnType.isNotNull()) {
                attributeDefinition.getAttributeConstraints().add(new NotNullConstraint());
            }
            if (columnType.getDefaultDefinition() != null) {
                attributeDefinition.getAttributeConstraints().add(new Default(columnType.getDefaultDefinition()));
            }
            if (columnType.unique()) {
                attributeDefinition.getAttributeConstraints().add(new UniqueAttributeConstraint());
            }
            if (columnType.isForeignColumnType()) {
                ForeignColumnType foreignColumnType = (ForeignColumnType) columnType;

                if (!databaseType.supportTableForeignConstraint()) {
                    attributeDefinition.getAttributeConstraints().add(
                            new ReferencesConstraint(
                                    foreignColumnType.getForeignTableName(),
                                    foreignColumnType.getColumnName(),
                                    foreignColumnType.getOnDelete(),
                                    foreignColumnType.getOnUpdate())
                    );
                } else {
                    createTableQuery
                            .getTableConstraints()
                            .add(new ForeignKeyConstraint(
                                            foreignColumnType.getForeignTableName(),
                                            foreignColumnType.getForeignColumnName(),
                                            foreignColumnType.getColumnName(),
                                            foreignColumnType.getOnDelete(),
                                            foreignColumnType.getOnUpdate()
                                    )
                            );
                }
            }
            attributeDefinitions.add(attributeDefinition);
        }

        if (databaseType.supportTableUniqueConstraint()) {
            createTableQuery.getTableConstraints().addAll(rootEntityMetaData.getUniqueColumns()
                    .stream()
                    .map(UniqueConstraint::new)
                    .collect(Collectors.toList()));
        }

        return createTableQuery;
    }

    /**
     * Make and return drop table statement.
     *
     * @param ifExist append if exist
     * @return drop table statement
     */
    public DropTableQuery getDropTableQuery(boolean ifExist) {
        return new DropTableQuery(
                rootEntityMetaData.getTableName(),
                ifExist
        );
    }

    /**
     * Make and return update statement.
     *
     * @return update statement
     */
    public UpdateQuery getUpdateByIdCompiledQuery(Map<IDatabaseColumnType, Argument> argumentMap,
                                                  Argument id) {
        UpdateQuery updateQuery = new UpdateQuery(rootEntityMetaData.getTableName());

        for (Map.Entry<IDatabaseColumnType, Argument> entry : argumentMap.entrySet()) {
            IDatabaseColumnType columnType = entry.getKey();

            updateQuery.add(
                    new UpdateValue(
                            columnType.getColumnName(),
                            columnType.getDataPersister().createLiteral(entry.getValue().getValue()))
            );
        }
        IDatabaseColumnType primaryKeyType = rootEntityMetaData.getPrimaryKeyColumnType();
        AndCondition andCondition = new AndCondition();
        ColumnSpec idColumnSpec = new ColumnSpec(
                primaryKeyType.getColumnName(),
                rootEntityMetaData.getTableName()
        );

        andCondition.add(new Equals(idColumnSpec, primaryKeyType.getDataPersister().createLiteral(id.getValue())));
        updateQuery.getWhere().getConditions().add(andCondition);

        return updateQuery;
    }

    /**
     * Make and return update statement.
     *
     * @param updateColumnTypes update columns
     * @return update statement
     */
    public UpdateQuery getUpdateByIdQuery(Collection<IDatabaseColumnType> updateColumnTypes) {
        UpdateQuery updateQuery = new UpdateQuery(rootEntityMetaData.getTableName());

        for (IDatabaseColumnType fieldType : updateColumnTypes) {
            updateQuery.add(
                    new UpdateValue(
                            fieldType.getColumnName(),
                            new Param())
            );
        }
        AndCondition andCondition = new AndCondition();
        ColumnSpec idColumnSpec = new ColumnSpec(
                rootEntityMetaData.getPrimaryKeyColumnType().getColumnName(),
                rootEntityMetaData.getTableName()
        );

        andCondition.add(new Equals(idColumnSpec, new Param()));
        updateQuery.getWhere().getConditions().add(andCondition);

        return updateQuery;
    }

    /**
     * Make and return delete statement.
     *
     * @return delete statement
     */
    public DeleteQuery getDeleteByIdQuery() {
        DeleteQuery deleteQuery = new DeleteQuery(rootEntityMetaData.getTableName());
        AndCondition andCondition = new AndCondition();
        ColumnSpec idColumnSpec = new ColumnSpec(
                rootEntityMetaData.getPrimaryKeyColumnType().getColumnName(),
                rootEntityMetaData.getTableName()
        );

        andCondition.add(new Equals(idColumnSpec, new Param()));
        deleteQuery.getWhere().getConditions().add(andCondition);

        return deleteQuery;
    }

    public DeleteQuery getDeleteByIdCompiledQuery(Argument id) {
        DeleteQuery deleteQuery = new DeleteQuery(rootEntityMetaData.getTableName());
        IDatabaseColumnType columnType = rootEntityMetaData.getPrimaryKeyColumnType();
        AndCondition andCondition = new AndCondition();
        ColumnSpec idColumnSpec = new ColumnSpec(
                rootEntityMetaData.getPrimaryKeyColumnType().getColumnName(),
                rootEntityMetaData.getTableName()
        );

        andCondition.add(new Equals(idColumnSpec, columnType.getDataPersister().createLiteral(id.getValue())));
        deleteQuery.getWhere().getConditions().add(andCondition);

        return deleteQuery;
    }

    /**
     * Make and return create index statement.
     *
     * @return create index statement
     */
    public Collection<CreateIndexQuery> getCreateIndexQuery() {
        return rootEntityMetaData.getIndexColumns()
                .stream()
                .map(CreateIndexQuery::new)
                .collect(Collectors.toList());
    }

    /**
     * Make and return drop index statement.
     *
     * @return drop index statement
     */
    public Collection<DropIndexQuery> getDropIndexQuery() {
        return rootEntityMetaData.getIndexColumns()
                .stream()
                .map(indexFieldType -> new DropIndexQuery(indexFieldType.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Make and return select count star statement.
     *
     * @return select count star statement
     */
    public SelectQuery countOff() {
        SelectQuery selectQuery = new SelectQuery();
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectQuery.setFrom(new FromTable(new TableRef(rootEntityMetaData.getTableName())));
        selectColumnsList.addColumn(new DisplayedOperand(new CountAll()));
        selectQuery.setSelectColumnsStrategy(selectColumnsList);

        return selectQuery;
    }

    /**
     * Make and return select statement.
     *
     * @param selectStatement target select query
     * @return select statement
     */
    public SelectQuery getBySelectStatement(SelectStatement selectStatement) {
        SelectQuery selectQuery = new SelectQuery();

        selectQuery.setSelectColumnsStrategy(selectColumnsList);
        selectQuery.setFrom(fromJoinedTables);
        selectQuery.setWhere(selectStatement.getWhere());
        selectQuery.setGroupBy(selectStatement.getGroupBy());
        selectQuery.setOrderBy(selectStatement.getOrderBy());
        selectQuery.setHaving(selectStatement.getHaving());
        selectQuery.setLimit(selectStatement.getLimit());
        selectQuery.setOffset(selectStatement.getOffset());
        selectQuery.accept(new QuerySpaceVisitor(rootEntityMetaData, rootEntityAliases));

        return selectQuery;
    }

    /**
     * Make and return select with long result column statement.
     *
     * @param selectStatement target select query
     * @return select with long result column statement
     */
    public SelectQuery getSelectForLongResult(SelectStatement selectStatement) {
        SelectQuery selectQuery = new SelectQuery();

        selectQuery.setFrom(new FromTable(new TableRef(rootEntityMetaData.getTableName())));
        selectQuery.setWhere(selectStatement.getWhere());
        selectQuery.setGroupBy(selectStatement.getGroupBy());
        selectQuery.setOrderBy(selectStatement.getOrderBy());
        selectQuery.setHaving(selectStatement.getHaving());
        selectQuery.setLimit(selectStatement.getLimit());
        selectQuery.setOffset(selectStatement.getOffset());
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectColumnsList.addColumn(selectStatement.getSelectOperand());
        selectQuery.setSelectColumnsStrategy(selectColumnsList);
        selectQuery.accept(new QuerySpaceVisitor(rootEntityMetaData, rootEntityAliases));

        return selectQuery;
    }
}
