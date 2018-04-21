package ru.saidgadjiev.orm.next.core.dao.visitor;

import org.apache.log4j.Logger;
import ru.saidgadjiev.orm.next.core.common.UIDGenerator;
import ru.saidgadjiev.orm.next.core.dao.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnKey;
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
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.CollectionEntityAliases;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliasResolverContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.CollectionInitializer;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.EntityInitializer;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.util.*;

public class DefaultEntityMetadataVisitor implements EntityMetadataVisitor {

    private final static Logger LOGGER = Logger.getLogger(DefaultEntityMetadataVisitor.class);

    private FromJoinedTables fromJoinedTables;

    private MetaModel metaModel;

    private EntityAliasResolverContext entityAliasResolverContext;

    private UIDGenerator uidGenerator;

    private SelectColumnsList selectColumnsList = new SelectColumnsList();

    private Map<String, EntityInitializer> entityInitializerMap = new LinkedHashMap<>();

    private List<CollectionInitializer> collectionInitializers = new ArrayList<>();

    private Set<ForeignColumnKey> visitedForeignColumnKey = new HashSet<>();

    private Stack<String> parentUidStack = new Stack<>();

    public DefaultEntityMetadataVisitor(DatabaseEntityMetadata mainEntityMetadata,
                                        MetaModel metaModel,
                                        EntityAliasResolverContext entityAliasResolverContext,
                                        UIDGenerator uidGenerator,
                                        EntityInitializer rootInitializer) {
        this.entityAliasResolverContext = entityAliasResolverContext;
        this.uidGenerator = uidGenerator;

        parentUidStack.push(rootInitializer.getUid());
        this.fromJoinedTables = new FromJoinedTables(new TableRef(mainEntityMetadata.getTableName()).alias(new Alias(rootInitializer.getEntityAliases().getTableAlias())));
        this.metaModel = metaModel;

        appendSelectColumns(rootInitializer.getEntityAliases(), mainEntityMetadata);
    }

    public void visit(ForeignColumnType foreignColumnType) {
        if (visitedForeignColumnKey.contains(foreignColumnType.getForeignColumnKey())) {
            LOGGER.debug("Detected circular references!");
            return;
        }
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getPersister(foreignColumnType.getForeignFieldClass()).getMetadata();
        String nextUID = uidGenerator.nextUID();
        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        appendJoin(foreignColumnType, ownerAliases, foreignEntityAliases);
        appendSelectColumns(foreignEntityAliases, foreignMetaData);

        entityInitializerMap.put(nextUID, new EntityInitializer(nextUID, foreignEntityAliases, metaModel.getPersister(foreignMetaData.getTableClass())));
        visitedForeignColumnKey.add(foreignColumnType.getForeignColumnKey());
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);
    }

    @Override
    public void visit(ForeignCollectionColumnType foreignCollectionColumnType) {
        DatabaseEntityMetadata<?> ownerMetaData = metaModel.getPersister(foreignCollectionColumnType.getOwnerClass()).getMetadata();
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getPersister(foreignCollectionColumnType.getForeignFieldClass()).getMetadata();
        String nextUID = uidGenerator.nextUID();

        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        Expression onExpression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(
                new Equals(
                        new ColumnSpec(ownerMetaData.getPrimaryKey().getColumnName()).alias(new Alias(ownerAliases.getTableAlias())),
                        new ColumnSpec(foreignCollectionColumnType.getForeignColumnType().getColumnName()).alias(new Alias(foreignEntityAliases.getTableAlias()))
                )
        );
        onExpression.add(andCondition);

        fromJoinedTables.add(new LeftJoin(new TableRef(foreignCollectionColumnType.getForeignTableName()).alias(new Alias(foreignEntityAliases.getTableAlias())), onExpression));
        appendSelectColumns(foreignEntityAliases, foreignMetaData);

        collectionInitializers.add(
                new CollectionInitializer(
                        parentUidStack.peek(),
                        new CollectionEntityAliases(foreignEntityAliases.getKeyAlias(), foreignEntityAliases.getAliasByPropertyName(foreignCollectionColumnType.getForeignColumnKey().getColumnName()),
                                foreignMetaData.getTableClass()),
                        foreignCollectionColumnType
                )
        );
        entityInitializerMap.put(nextUID, new EntityInitializer(nextUID, foreignEntityAliases, metaModel.getPersister(foreignMetaData.getTableClass())));
        visitedForeignColumnKey.add(foreignCollectionColumnType.getForeignColumnKey());
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);
    }

    @Override
    public void finish(ForeignColumnType foreignColumnType) {
        parentUidStack.pop();
    }

    @Override
    public void finish(ForeignCollectionColumnType foreignCollectionColumnType) {
        parentUidStack.pop();
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

    public List<EntityInitializer> getEntityInitializers() {
        return new ArrayList<>(entityInitializerMap.values());
    }

    public List<CollectionInitializer> getCollectionInitializers() {
        return collectionInitializers;
    }
}
/**
 * Получаем объект который имеет коллекцию через кеш сессии по id. Передаем в коллекшен инициализер алиасы
 */
