package ru.saidgadjiev.ormnext.core.table.internal.query_space;

import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.field.field_type.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.field_type.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.field_type.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.query.core.clause.from.FromJoinedTables;
import ru.saidgadjiev.ormnext.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.ormnext.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumn;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedOperand;
import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.core.common.UpdateValue;
import ru.saidgadjiev.ormnext.core.query.core.condition.Equals;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.core.constraints.attribute.Default;
import ru.saidgadjiev.ormnext.core.query.core.constraints.attribute.NotNullConstraint;
import ru.saidgadjiev.ormnext.core.query.core.constraints.attribute.PrimaryKeyConstraint;
import ru.saidgadjiev.ormnext.core.query.core.constraints.table.ForeignKeyConstraint;
import ru.saidgadjiev.ormnext.core.query.core.constraints.table.UniqueConstraint;
import ru.saidgadjiev.ormnext.core.query.core.function.CountAll;
import ru.saidgadjiev.ormnext.core.query.core.join.LeftJoin;
import ru.saidgadjiev.ormnext.core.query.core.literals.Param;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Query space for entity. It contains prepared join expression, resolved aliases and select columns list
 * and methods for create statements for different operations eg. select by id, select all...
 */
public class EntityQuerySpace {

    /**
     * Select columns.
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
     * Create new instance.
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
    public Select getSelectById() {
        Select selectById = new Select();

        selectById.setSelectColumnsStrategy(selectColumnsList);
        selectById.setFrom(fromJoinedTables);
        AndCondition andCondition = new AndCondition();
        Expression where = new Expression();
        ColumnSpec idColumnSpec = new ColumnSpec(
                rootEntityMetaData.getPrimaryKey().getColumnName(),
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
    public Select getSelectAll() {
        Select selectAll = new Select();

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
    public CreateQuery getCreateQuery(Collection<IDatabaseColumnType> resultColumnTypes) {
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

    /**
     * Make and return create table statement.
     *
     * @param ifNotExist append if not exist
     * @return create table statement
     */
    public CreateTableQuery createTableQuery(boolean ifNotExist) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        for (IDatabaseColumnType dbFieldType : rootEntityMetaData.getColumnTypes()) {
            if (dbFieldType.isForeignCollectionColumnType()) {
                continue;
            }
            AttributeDefinition attributeDefinition = new AttributeDefinition(
                    dbFieldType.getColumnName(),
                    dbFieldType.getDataType(),
                    dbFieldType.getLength()
            );

            if (dbFieldType.isId()) {
                attributeDefinition.getAttributeConstraints().add(new PrimaryKeyConstraint(dbFieldType.isGenerated()));
            }
            if (dbFieldType.isNotNull()) {
                attributeDefinition.getAttributeConstraints().add(new NotNullConstraint());
            }
            if (dbFieldType.getDefaultDefinition() != null) {
                attributeDefinition.getAttributeConstraints().add(new Default(dbFieldType.getDefaultDefinition()));
            }
            attributeDefinitions.add(attributeDefinition);
        }
        CreateTableQuery createTableQuery = new CreateTableQuery(
                rootEntityMetaData.getTableName(),
                ifNotExist,
                attributeDefinitions
        );

        for (ForeignColumnType foreignColumnType : rootEntityMetaData.toForeignColumnTypes()) {
            createTableQuery
                    .getTableConstraints()
                    .add(new ForeignKeyConstraint(
                            foreignColumnType.getForeignTableName(),
                            foreignColumnType.getForeignColumnName(),
                            foreignColumnType.getColumnName())
                    );
        }

        createTableQuery.getTableConstraints().addAll(rootEntityMetaData.getUniqueColumns()
                .stream()
                .map(UniqueConstraint::new)
                .collect(Collectors.toList()));

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
     * @param updateColumnTypes update columns
     * @return update statement
     */
    public UpdateQuery getUpdateQuery(Collection<IDatabaseColumnType> updateColumnTypes) {
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
                rootEntityMetaData.getPrimaryKey().getColumnName(),
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
    public DeleteQuery getDeleteQuery() {
        DeleteQuery deleteQuery = new DeleteQuery(rootEntityMetaData.getTableName());
        AndCondition andCondition = new AndCondition();
        ColumnSpec idColumnSpec = new ColumnSpec(
                rootEntityMetaData.getPrimaryKey().getColumnName(),
                rootEntityMetaData.getTableName()
        );

        andCondition.add(new Equals(idColumnSpec, new Param()));
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
    public Select countOff() {
        Select select = new Select();
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectColumnsList.addColumn(new DisplayedOperand(new CountAll()));
        select.setSelectColumnsStrategy(selectColumnsList);

        return select;
    }

    /**
     * Make and return select statement.
     *
     * @param criteria target criteria query
     * @return select statement
     */
    public Select getByCriteria(CriteriaQuery criteria) {
        Select select = new Select();

        select.setSelectColumnsStrategy(selectColumnsList);
        select.setFrom(fromJoinedTables);
        select.setWhere(criteria.getWhere());
        select.setGroupBy(criteria.getGroupBy());
        select.setOrderBy(criteria.getOrderBy());
        select.setHaving(criteria.getHaving());
        select.setLimit(criteria.getLimit());
        select.setOffset(criteria.getOffset());
        select.accept(new QuerySpaceVisitor(rootEntityMetaData, rootEntityAliases));

        return select;
    }

    /**
     * Make and return select with long result column statement.
     *
     * @param criteria target criteria query
     * @return select with long result column statement
     */
    public Select getByCriteriaForLongResult(CriteriaQuery criteria) {
        Select select = new Select();

        select.setFrom(new FromTable(new TableRef(rootEntityMetaData.getTableName())));
        select.setWhere(criteria.getWhere());
        select.setGroupBy(criteria.getGroupBy());
        select.setOrderBy(criteria.getOrderBy());
        select.setHaving(criteria.getHaving());
        select.setLimit(criteria.getLimit());
        select.setOffset(criteria.getOffset());
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectColumnsList.addColumn(criteria.getSelectOperand());
        select.setSelectColumnsStrategy(selectColumnsList);
        select.accept(new QuerySpaceVisitor(rootEntityMetaData, rootEntityAliases));

        return select;
    }
}
