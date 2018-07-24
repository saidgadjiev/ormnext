package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.EntityContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.ArgumentUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by said on 23.07.2018.
 */
public class ReadEntity implements EntityMetadataVisitor {

    private final ResultSetContext resultSetContext;

    private final EntityContext entityContext;

    private Map<String, ResultSetValue> values = new HashMap<>();

    private EntityAliases entityAliases;

    private DatabaseResults databaseResults;

    private Set<String> resultColumns;

    private ResultSetRow resultSetRow;

    private boolean skip = false;

    public ReadEntity(EntityContext entityContext,
                      ResultSetContext resultSetContext,
                      ResultSetRow resultSetRow) {
        this.entityContext = entityContext;
        this.resultSetContext = resultSetContext;
        this.entityAliases = entityContext.getEntityAliases();
        this.databaseResults = resultSetContext.getDatabaseResults();
        this.resultColumns = resultSetContext.getResultColumns();
        this.resultSetRow = resultSetRow;
    }

    @Override
    public boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        return true;
    }

    @Override
    public boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException {
        if (skip) {
            return false;
        }
        String alias = entityAliases.getAliasByColumnName(foreignColumnType.columnName());

        if (resultColumns.contains(alias)) {
            Object value = foreignColumnType.dataPersister().readValue(
                    databaseResults,
                    entityAliases.getAliasByColumnName(foreignColumnType.columnName())
            );

            value = ArgumentUtils.processConvertersToJavaValue(value, foreignColumnType).getValue();
            ResultSetValue resultSetValue = new ResultSetValue(value, databaseResults.wasNull());

            resultSetRow.add(alias, resultSetValue);
            values.put(alias, resultSetValue);
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
        if (skip) {
            return false;
        }
        if (databaseColumnType.id()) {
            checkProcessingState(databaseColumnType);
        } else {
            String alias = entityAliases.getAliasByColumnName(databaseColumnType.columnName());

            if (resultColumns.contains(alias)) {
                Object value = databaseColumnType.dataPersister().readValue(databaseResults, alias);

                value = ArgumentUtils.processConvertersToJavaValue(value, databaseColumnType).getValue();
                ResultSetValue resultSetValue = new ResultSetValue(value, databaseResults.wasNull());

                resultSetRow.add(alias, new ResultSetValue(value, databaseResults.wasNull()));
                values.put(alias, resultSetValue);
            }
        }

        return false;
    }

    @Override
    public void finish(SimpleDatabaseColumnTypeImpl databaseColumnType) {

    }

    @Override
    public void finish(DatabaseEntityMetadata<?> entityMetadata) {
        resultSetRow.addValues(entityAliases, values);
    }

    private void checkProcessingState(SimpleDatabaseColumnTypeImpl databaseColumnType) throws SQLException {
        String alias = entityAliases.getKeyAlias();

        if (resultSetContext.isResultColumn(alias)) {
            Object key = databaseColumnType.dataPersister().readValue(databaseResults, alias);
            ResultSetContext.EntityProcessingState processingState = resultSetContext.getProcessingState(
                    entityContext.getUid(),
                    key
            );

            key = ArgumentUtils.processConvertersToJavaValue(key, databaseColumnType).getValue();
            ResultSetValue resultSetValue = new ResultSetValue(key, databaseResults.wasNull());

            resultSetRow.add(alias, resultSetValue);
            values.put(alias, resultSetValue);

            if (processingState != null) {
                skip = true;
            }
        }
    }
}
