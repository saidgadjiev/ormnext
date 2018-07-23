package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.ArgumentUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by said on 23.07.2018.
 */
public class ReadEntity implements EntityMetadataVisitor {

    private Map<String, ResultSetValue> values = new LinkedHashMap<>();

    private EntityAliases entityAliases;

    private DatabaseResults databaseResults;

    private Set<String> resultColumns;

    public ReadEntity(EntityAliases entityAliases,
                      DatabaseResults databaseResults,
                      Set<String> resultColumns) {
        this.entityAliases = entityAliases;
        this.databaseResults = databaseResults;
        this.resultColumns = resultColumns;
    }

    @Override
    public boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        return true;
    }

    @Override
    public boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException  {
        String alias = entityAliases.getAliasByColumnName(foreignColumnType.columnName());

        if (resultColumns.contains(alias)) {
            Object value = foreignColumnType.dataPersister().readValue(
                    databaseResults,
                    entityAliases.getAliasByColumnName(foreignColumnType.columnName())
            );

            value = ArgumentUtils.processConvertersToJavaValue(value, foreignColumnType).getValue();
            values.put(alias, new ResultSetValue(value, databaseResults.wasNull()));
        }

        return false;
    }

    @Override
    public boolean start(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) {
        return false;
    }

    @Override
    public void finish(ForeignColumnTypeImpl foreignColumnType) {

    }

    @Override
    public void finish(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) {

    }

    @Override
    public boolean start(SimpleDatabaseColumnTypeImpl databaseColumnType) throws SQLException {
        String alias = entityAliases.getAliasByColumnName(databaseColumnType.columnName());

        if (resultColumns.contains(alias)) {
            Object value = databaseColumnType.dataPersister().readValue(databaseResults, alias);

            value = ArgumentUtils.processConvertersToJavaValue(value, databaseColumnType).getValue();
            values.put(alias, new ResultSetValue(value, databaseResults.wasNull()));
        }

        return false;
    }

    @Override
    public void finish(SimpleDatabaseColumnTypeImpl databaseColumnType) {

    }

    public Map<String, ResultSetValue> getValues() {
        return values;
    }
}
