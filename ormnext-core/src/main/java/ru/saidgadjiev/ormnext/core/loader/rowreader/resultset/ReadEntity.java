package ru.saidgadjiev.ormnext.core.loader.rowreader.resultset;

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
import java.util.Map;
import java.util.Set;

/**
 * Read values from result set.
 *
 * @author Said Gadjiev
 */
public class ReadEntity implements EntityMetadataVisitor {

    /**
     * Result set context.
     */
    private final ResultSetContext resultSetContext;

    /**
     * Entity context.
     */
    private final EntityContext entityContext;

    /**
     * Read values.
     */
    private final Map<String, ResultSetValue> values = new HashMap<>();

    /**
     * Entity aliases.
     */
    private final EntityAliases entityAliases;

    /**
     * Database results.
     */
    private final DatabaseResults databaseResults;

    /**
     * Result columns.
     */
    private final Set<String> resultColumns;

    /**
     * Result row.
     */
    private final ResultSetRow resultSetRow;

    /**
     * Finish read flag.
     */
    private boolean skip = false;

    /**
     * Create a new instance.
     *
     * @param entityContext    target entity context
     * @param resultSetContext target result set context
     * @param resultSetRow     target result set row
     */
    ReadEntity(EntityContext entityContext,
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
        String alias = entityAliases.getAliasByColumnName(databaseColumnType.columnName());

        if (resultColumns.contains(alias)) {
            Object value = databaseColumnType.dataPersister().readValue(databaseResults, alias);

            value = ArgumentUtils.processConvertersToJavaValue(value, databaseColumnType).getValue();
            ResultSetValue resultSetValue = new ResultSetValue(value, databaseResults.wasNull());

            resultSetRow.add(alias, new ResultSetValue(value, databaseResults.wasNull()));
            values.put(alias, resultSetValue);

            checkProcessingState(databaseColumnType, resultSetValue);
        }

        return false;
    }

    @Override
    public void finish(SimpleDatabaseColumnTypeImpl databaseColumnType) {

    }

    @Override
    public void finish(DatabaseEntityMetadata<?> entityMetadata) {
        resultSetRow.addAll(values);
        resultSetRow.addValues(entityAliases, values);
    }

    /**
     * Check is finish read results.
     *
     * @param databaseColumnType target column type
     * @param resultSetValue target read value
     * @throws SQLException any SQL exceptions
     */
    private void checkProcessingState(SimpleDatabaseColumnTypeImpl databaseColumnType,
                                      ResultSetValue resultSetValue) throws SQLException {
        if (databaseColumnType.id()) {
            ResultSetContext.EntityProcessingState processingState = resultSetContext.getProcessingState(
                    entityContext.getUid(),
                    resultSetValue.getValue()
            );

            if (processingState != null) {
                skip = true;
                values.putAll(processingState.getValues());
            }
        }
    }
}
