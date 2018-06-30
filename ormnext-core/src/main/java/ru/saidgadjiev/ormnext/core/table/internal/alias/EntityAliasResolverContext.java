package ru.saidgadjiev.ormnext.core.table.internal.alias;

import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entity alias context.
 *
 * @author Said Gadjiev
 */
public class EntityAliasResolverContext {

    /**
     * Resolved aliases by uid generated from {@link UIDGenerator}.
     * @see EntityAliases
     */
    private Map<String, EntityAliases> resolvedEntityAliases = new HashMap<>();

    /**
     * Unique alias creator.
     * @see AliasCreator
     */
    private AliasCreator aliasCreator = new AliasCreator();

    /**
     * Create aliases for uid.
     * @param uid target uid
     * @param entityMetadata target entity metadata
     * @return entity aliases
     */
    public EntityAliases resolveAliases(String uid, DatabaseEntityMetadata<?> entityMetadata) {
        String tableAlias = aliasCreator.createAlias(entityMetadata.getTableName());
        Map<String, String> columnAliases = new LinkedHashMap<>();
        Map<String, String> propertyNameAliases = new HashMap<>();

        DatabaseColumnType primaryKey = entityMetadata.getPrimaryKeyColumnType();
        String keyAlias = aliasCreator.createAlias(primaryKey.columnName());

        propertyNameAliases.put(primaryKey.getField().getName(), keyAlias);
        columnAliases.put(primaryKey.columnName(), keyAlias);

        for (DatabaseColumnType columnType: entityMetadata.getDisplayedColumnTypes()) {
            if (columnType.id()) {
                continue;
            }
            String resolvedAlias = aliasCreator.createAlias(columnType.columnName());

            columnAliases.put(columnType.columnName(), resolvedAlias);
            propertyNameAliases.put(columnType.getField().getName(), resolvedAlias);
        }
        EntityAliases entityAliases = new EntityAliases(tableAlias, columnAliases, propertyNameAliases, keyAlias);

        resolvedEntityAliases.putIfAbsent(uid, entityAliases);

        return entityAliases;
    }

    /**
     * Return resolved aliases by uid.
     * @param uid target uid
     * @return entity aliases
     */
    public EntityAliases getAliases(String uid) {
        return resolvedEntityAliases.get(uid);
    }
}
