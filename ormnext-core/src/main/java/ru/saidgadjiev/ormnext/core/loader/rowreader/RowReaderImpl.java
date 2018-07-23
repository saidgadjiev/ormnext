package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.CollectionContext;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.EntityContext;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static ru.saidgadjiev.ormnext.core.loader.ResultSetContext.*;

/**
 * Reader implementation.
 *
 * @author Said Gadjiev
 */
public class RowReaderImpl implements RowReader {

    /**
     * Entity initializers.
     * @see EntityContext
     */
    private Collection<EntityContext> entityContexts;

    /**
     * Collection initializers.
     * @see CollectionContext
     */
    private Collection<CollectionContext> collectionContexts;

    /**
     * Root entity initializer.
     * @see EntityContext
     */
    private EntityContext rootEntityContext;

    /**
     * Create a new row reader.
     * @param entityContexts entity initializers
     * @param collectionContexts collection initializers
     * @param rootEntityContext root entity initializer
     */
    public RowReaderImpl(Collection<EntityContext> entityContexts,
                         Collection<CollectionContext> collectionContexts,
                         EntityContext rootEntityContext) {
        this.entityContexts = entityContexts;
        this.collectionContexts = collectionContexts;
        this.rootEntityContext = rootEntityContext;
    }

    @Override
    public RowResult startRead(ResultSetContext resultSetContext) throws SQLException {
        ResultSetRow resultSetRow = new ResultSetRow();

        ReadEntity rootReadEntity = new ReadEntity(
                rootEntityContext.getEntityAliases(),
                resultSetContext.getDatabaseResults(),
                resultSetContext.getResultColumns()
        );

        rootEntityContext.getMetadata().accept(rootReadEntity);
        resultSetRow.addAll(rootReadEntity.getValues());
        for (EntityContext entityContext : entityContexts) {
            ReadEntity readEntity = new ReadEntity(
                    entityContext.getEntityAliases(),
                    resultSetContext.getDatabaseResults(),
                    resultSetContext.getResultColumns()
            );

            entityContext.getMetadata().accept(readEntity);
            resultSetRow.addAll(readEntity.getValues());
        }
        resultSetContext.setCurrentRow(resultSetRow);

        CreateProcessingState rootState = new CreateProcessingState(
                resultSetContext,
                rootEntityContext.getPersister(),
                rootEntityContext.getUid(),
                rootEntityContext.getEntityAliases()
        );

        rootEntityContext.getMetadata().accept(rootState);

        for (EntityContext entityContext : entityContexts) {
            CreateProcessingState state = new CreateProcessingState(
                    resultSetContext,
                    entityContext.getPersister(),
                    entityContext.getUid(),
                    entityContext.getEntityAliases()
            );

            entityContext.getMetadata().accept(state);
        }

        for (CollectionContext collectionContext : collectionContexts) {
            ReadCollection readCollection = new ReadCollection(
                    collectionContext.getAliases(),
                    resultSetContext,
                    collectionContext.getUid()
            );

            collectionContext.getMetadata().accept(readCollection);
        }

        Object readId = resultSetRow.get(rootEntityContext.getEntityAliases().getKeyAlias()).getValue();
        EntityProcessingState entityProcessingState = resultSetContext.getProcessingState(
                rootEntityContext.getUid(),
                readId
        );


        return new RowResult(
                entityProcessingState.getKey(),
                entityProcessingState.getEntityInstance(),
                entityProcessingState.isNew()
        );
    }

    @Override
    public void finishRead(ResultSetContext resultSetContext) throws SQLException {
        writeEntity(resultSetContext, rootEntityContext);

        for (EntityContext entityContext : entityContexts) {
            writeEntity(resultSetContext, entityContext);
        }
        for (CollectionContext collectionContext : collectionContexts) {
            Map<Object, EntityProcessingState> processingStates = resultSetContext.getProcessingStates(
                    collectionContext.getUid()
            );

            if (processingStates != null) {
                for (Map.Entry<Object, EntityProcessingState> entry : processingStates.entrySet()) {
                    WriteCollection writeCollection = new WriteCollection(
                            resultSetContext,
                            entry.getValue(),
                            collectionContext.getCollectionLoader()
                    );

                    collectionContext.getMetadata().accept(writeCollection);
                }
            }
        }
    }

    private void writeEntity(ResultSetContext resultSetContext, EntityContext entityContext) throws SQLException {
        Map<Object, EntityProcessingState> processingStates = resultSetContext.getProcessingStates(
                entityContext.getUid()
        );

        if (processingStates != null) {
            for (Map.Entry<Object, EntityProcessingState> entry : processingStates.entrySet()) {
                WriteEntity writeEntity = new WriteEntity(
                        resultSetContext,
                        entry.getValue(),
                        entityContext.getEntityAliases(),
                        entityContext.getPersister()
                );

                entityContext.getMetadata().accept(writeEntity);
            }
        }
    }
}
