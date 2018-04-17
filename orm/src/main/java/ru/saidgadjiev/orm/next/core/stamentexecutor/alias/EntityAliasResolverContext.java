package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityAliasResolverContext {

    private Map<String, EntityAliases> resolvedAliases = new HashMap<>();

    public AliasResolver aliasResolver = new AliasResolverImpl();

    public EntityAliases resolveAliases(String uid, DatabaseEntityMetadata<?> entityMetadata) {
        if (resolvedAliases.containsKey(uid)) {
            return resolvedAliases.get(uid);
        }
        String tableAlias = aliasResolver.createAlias(entityMetadata.getTableName());
        List<String> columnAliases = new ArrayList<>();

        for (IDatabaseColumnType columnType: entityMetadata.getFieldTypes()) {
            if (columnType.isForeignCollectionFieldType()) {
                continue;
            }
            columnAliases.add(aliasResolver.createAlias(columnType.getColumnName()));
        }
        EntityAliases entityAliases = new EntityAliasesImpl(tableAlias, columnAliases);

        resolvedAliases.putIfAbsent(uid, entityAliases);

        return entityAliases;
    }

    public EntityAliases getAliases(String uid) {
        return resolvedAliases.get(uid);
    }
}
