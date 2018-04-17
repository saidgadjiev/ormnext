package ru.saidgadjiev.orm.next.core.dao.visitor;

import ru.saidgadjiev.orm.next.core.common.UIDGenerator;
import ru.saidgadjiev.orm.next.core.dao.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromJoinedTables;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumn;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.core.condition.Equals;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.core.join.LeftJoin;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliasResolverContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.EntityInitializer;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.util.*;

public class DefaultEntityMetadataVisitor implements EntityMetadataVisitor {

    private FromJoinedTables fromJoinedTables;

    private MetaModel metaModel;

    private EntityAliasResolverContext entityAliasResolverContext = new EntityAliasResolverContext();

    private SelectColumnsList selectColumnsList = new SelectColumnsList();

    private Map<Class<?>, String> metaDataUidMap = new LinkedHashMap<>();

    private UIDGenerator uidGenerator = new UIDGenerator();

    public DefaultEntityMetadataVisitor(DatabaseEntityMetadata mainEntityMetadata, MetaModel metaModel) {
        String nextUID = uidGenerator.nextUID();

        EntityAliases entityAliases = entityAliasResolverContext.resolveAliases(nextUID, mainEntityMetadata);
        metaDataUidMap.put(mainEntityMetadata.getTableClass(), nextUID);
        this.fromJoinedTables = new FromJoinedTables(new TableRef(mainEntityMetadata.getTableName()).alias(new Alias(entityAliases.getTableAlias())));
        this.metaModel = metaModel;

        appendSelectColumns(entityAliases, mainEntityMetadata);
    }

    public void visit(ForeignColumnType foreignColumnType) {
        DatabaseEntityMetadata<?> ownerMetadata = metaModel.getMetaData(foreignColumnType.getOwnerClass()).getMetadata();
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(metaDataUidMap.get(ownerMetadata.getTableClass()));

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getMetaData(foreignColumnType.getForeignFieldClass()).getMetadata();
        String nextUID = uidGenerator.nextUID();

        metaDataUidMap.put(foreignMetaData.getTableClass(), nextUID);
        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        appendJoin(foreignColumnType, ownerAliases, foreignEntityAliases);
        appendSelectColumns(foreignEntityAliases, foreignMetaData);

        foreignMetaData.accept(this);
    }

    private void appendJoin(ForeignColumnType foreignColumnType, EntityAliases ownerAliases, EntityAliases foreignEntityAliases) {
        Expression onExpression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(
                new Equals(
                        new ColumnSpec(foreignColumnType.getColumnName()).alias(new Alias(ownerAliases.getTableAlias())),
                        new ColumnSpec(foreignColumnType.getForeignPrimaryKey().getColumnName()).alias(new Alias(foreignEntityAliases.getTableAlias()))
                )
        );
        onExpression.add(andCondition);
        fromJoinedTables.add(new LeftJoin(new TableRef(foreignColumnType.getForeignTableName()).alias(new Alias(foreignEntityAliases.getTableAlias())), onExpression));
    }

    private void appendSelectColumns(EntityAliases aliases, DatabaseEntityMetadata<?> databaseEntityMetadata) {
        List<IDatabaseColumnType> columnTypes = databaseEntityMetadata.getFieldTypes();

        for (int i = 0; i < columnTypes.size(); ++i) {
            IDatabaseColumnType columnType = columnTypes.get(i);

            if (columnTypes.get(i).isForeignCollectionFieldType()) {
                continue;
            }
            ColumnSpec columnSpec = new ColumnSpec(columnType.getColumnName()).alias(new Alias(aliases.getTableAlias()));
            DisplayedColumnSpec displayedColumnSpec = new DisplayedColumn(columnSpec);

            displayedColumnSpec.setAlias(new Alias(aliases.getColumnAliases().get(i)));
            selectColumnsList.addColumn(displayedColumnSpec);
        }
    }

    public FromJoinedTables getFromJoinedTables() {
        return fromJoinedTables;
    }

    public EntityAliasResolverContext getEntityAliasResolverContext() {
        return entityAliasResolverContext;
    }

    @Override
    public boolean visit(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        return true;
    }

    public SelectColumnsList getSelectColumnsList() {
        return selectColumnsList;
    }

    public Map<Class<?>, String> getMetaDataUidMap() {
        return metaDataUidMap;
    }
}
