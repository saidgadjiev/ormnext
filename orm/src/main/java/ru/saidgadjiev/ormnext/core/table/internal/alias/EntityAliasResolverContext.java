package ru.saidgadjiev.ormnext.core.table.internal.alias;

import ru.saidgadjiev.ormnext.core.common.AliasResolver;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EntityAliasResolverContext {

    private Map<String, EntityAliases> resolvedEntityAliases = new HashMap<>();

    private AliasResolver aliasResolver = new AliasResolver();

    public EntityAliases resolveAliases(String uid, DatabaseEntityMetadata<?> entityMetadata) {
        if (resolvedEntityAliases.containsKey(uid)) {
            return resolvedEntityAliases.get(uid);
        }
        String tableAlias = aliasResolver.createAlias(entityMetadata.getTableName());
        Map<String, String> columnAliases = new LinkedHashMap<>();
        Map<String, String> propertyNameAliases = new HashMap<>();

        for (IDatabaseColumnType columnType: entityMetadata.getFieldTypes()) {
            if (columnType.isForeignCollectionFieldType()) {
                continue;
            }
            if (columnType.isId()) {
                continue;
            }
            String resolvedAlias = aliasResolver.createAlias(columnType.getColumnName());

            columnAliases.put(columnType.getColumnName(), resolvedAlias);
            propertyNameAliases.put(columnType.getFieldName(), resolvedAlias);
        }
        IDatabaseColumnType primaryKey = entityMetadata.getPrimaryKey();
        String keyAlias = aliasResolver.createAlias(primaryKey.getColumnName());

        columnAliases.put(primaryKey.getColumnName(), keyAlias);
        propertyNameAliases.put(primaryKey.getFieldName(), keyAlias);
        EntityAliases entityAliases = new EntityAliases(tableAlias, columnAliases, propertyNameAliases, keyAlias, primaryKey.getDataPersister());

        resolvedEntityAliases.putIfAbsent(uid, entityAliases);

        return entityAliases;
    }



    public EntityAliases getAliases(String uid) {
        return resolvedEntityAliases.get(uid);
    }
}
