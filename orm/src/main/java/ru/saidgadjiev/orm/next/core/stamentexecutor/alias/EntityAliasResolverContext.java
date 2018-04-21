package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.util.*;

public class EntityAliasResolverContext {

    private Map<String, EntityAliases> resolvedEntityAliases = new HashMap<>();

    private AliasResolver aliasResolver = new AliasResolverImpl();

    public EntityAliases resolveAliases(String uid, DatabaseEntityMetadata<?> entityMetadata) {
        if (resolvedEntityAliases.containsKey(uid)) {
            return resolvedEntityAliases.get(uid);
        }
        String tableAlias = aliasResolver.createAlias(entityMetadata.getTableName());
        Map<String, String> columnAliases = new LinkedHashMap<>();

        for (IDatabaseColumnType columnType: entityMetadata.getFieldTypes()) {
            if (columnType.isForeignCollectionFieldType()) {
                continue;
            }
            if (columnType.isId()) {
                continue;
            }
            columnAliases.put(columnType.getColumnName(), aliasResolver.createAlias(columnType.getColumnName()));
        }
        IDatabaseColumnType primaryKey = entityMetadata.getPrimaryKey();
        EntityAliases entityAliases = new EntityAliases(tableAlias, columnAliases, aliasResolver.createAlias(primaryKey.getColumnName()));

        resolvedEntityAliases.putIfAbsent(uid, entityAliases);

        return entityAliases;
    }



    public EntityAliases getAliases(String uid) {
        return resolvedEntityAliases.get(uid);
    }
}
