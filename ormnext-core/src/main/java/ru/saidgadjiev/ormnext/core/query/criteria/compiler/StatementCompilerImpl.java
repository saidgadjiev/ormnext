package ru.saidgadjiev.ormnext.core.query.criteria.compiler;

import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.loader.Argument;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.DeleteQuery;
import ru.saidgadjiev.ormnext.core.query.visitor.element.SelectQuery;
import ru.saidgadjiev.ormnext.core.query.visitor.element.UpdateQuery;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Statement compiler impl.
 *
 * @author Said Gadjiev
 */
public class StatementCompilerImpl implements StatementCompiler {

    /**
     * Meta model.
     */
    private final MetaModel metaModel;

    /**
     * Create a new instance.
     *
     * @param metaModel target meta model
     */
    public StatementCompilerImpl(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public SelectStatementCompileResult compile(SelectStatement<?> selectStatement) {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(selectStatement.getEntityClass());
        SelectQuery selectQuery = entityPersister.getEntityQuerySpace().getSelectQuery(selectStatement);

        return new SelectStatementCompileResult(
                selectQuery,
                getArguments(entityPersister.getMetadata(), selectStatement)
        );
    }

    @Override
    public DeleteStatementCompileResult compile(DeleteStatement deleteStatement) {
        DatabaseEntityPersister persister = metaModel.getPersister(deleteStatement.getEntityClass());
        DeleteQuery deleteQuery = persister.getEntityQuerySpace().getDeleteQuery(deleteStatement);

        return new DeleteStatementCompileResult(deleteQuery, getArguments(persister.getMetadata(), deleteStatement));
    }

    @Override
    public UpdateStatementCompileResult compile(UpdateStatement updateStatement) {
        DatabaseEntityPersister persister = metaModel.getPersister(updateStatement.getEntityClass());
        UpdateQuery updateQuery = persister.getEntityQuerySpace().getUpdateQuery(updateStatement);

        return new UpdateStatementCompileResult(updateQuery, getArguments(persister.getMetadata(), updateStatement));
    }

    /**
     * Obtain arguments {@link Argument} from criteria executeQuery.
     *
     * @param metadata          target table meta data
     * @param criteriaStatement target criteria executeQuery
     * @return argument index map
     */
    private Map<Integer, Argument> getArguments(DatabaseEntityMetadata<?> metadata,
                                                CriteriaStatement criteriaStatement) {
        Map<Integer, Argument> args = new HashMap<>();
        AtomicInteger index = new AtomicInteger();

        for (CriterionArgument criterionArgument : criteriaStatement.getArgs()) {
            DatabaseColumnType columnType =
                    getDataTypeByPropertyName(metadata.getColumnTypes(), criterionArgument.getProperty())
                            .orElseThrow(() -> new PropertyNotFoundException(
                                    metadata.getTableClass(),
                                    criterionArgument.getProperty())
                            );

            for (Object arg : criterionArgument.getValues()) {
                args.put(index.incrementAndGet(), new Argument(columnType.dataPersister(), arg));
            }
        }

        for (Map.Entry<Integer, Object> entry : criteriaStatement.getUserProvidedArgs().entrySet()) {
            DataPersister dataPersister = DataPersisterManager.lookup(entry.getValue().getClass());

            args.put(entry.getKey(), new Argument(dataPersister, entry.getValue()));
        }

        return args;
    }

    /**
     * Find column type by property name in requested column types.
     *
     * @param columnTypes  target column types
     * @param propertyName target property name
     * @return optional column type
     */
    private static Optional<DatabaseColumnType> getDataTypeByPropertyName(List<DatabaseColumnType> columnTypes,
                                                                          String propertyName) {
        for (DatabaseColumnType columnType : columnTypes) {
            if (columnType.foreignCollectionColumnType()) {
                continue;
            }

            if (columnType.getField().getName().equals(propertyName)) {
                return Optional.of(columnType);
            }
        }

        return Optional.empty();
    }

    /**
     * Select statement compile result.
     *
     * @see SelectStatement
     */
    public static final class SelectStatementCompileResult implements StatementCompileResult<SelectQuery> {

        /**
         * Compiled query.
         */
        private final SelectQuery selectQuery;

        /**
         * Query arguments.
         */
        private final Map<Integer, Argument> arguments;

        /**
         * Create a new instance.
         *
         * @param selectQuery compiled query
         * @param arguments query arguments
         */
        private SelectStatementCompileResult(SelectQuery selectQuery, Map<Integer, Argument> arguments) {
            this.selectQuery = selectQuery;
            this.arguments = arguments;
        }

        @Override
        public SelectQuery getQuery() {
            return selectQuery;
        }

        @Override
        public Map<Integer, Argument> getArguments() {
            return arguments;
        }
    }

    /**
     * Delete statement compile result.
     *
     * @see DeleteStatement
     */
    public static final class DeleteStatementCompileResult implements StatementCompileResult<DeleteQuery> {

        /**
         * Compiled query.
         */
        private final DeleteQuery deleteQuery;

        /**
         * Query arguments.
         */
        private final Map<Integer, Argument> arguments;

        /**
         * Create a new instance.
         *
         * @param deleteQuery compiled query
         * @param arguments query arguments
         */
        private DeleteStatementCompileResult(DeleteQuery deleteQuery, Map<Integer, Argument> arguments) {
            this.deleteQuery = deleteQuery;
            this.arguments = arguments;
        }

        @Override
        public DeleteQuery getQuery() {
            return deleteQuery;
        }

        @Override
        public Map<Integer, Argument> getArguments() {
            return arguments;
        }
    }

    /**
     * Update statement compile result.
     *
     * @see UpdateStatement
     */
    public static final class UpdateStatementCompileResult implements StatementCompileResult<UpdateQuery> {

        /**
         * Compiled query.
         */
        private final UpdateQuery updateQuery;

        /**
         * Query arguments.
         */
        private final Map<Integer, Argument> arguments;

        /**
         * Create a new instance.
         *
         * @param updateQuery compiled query
         * @param arguments query arguments
         */
        private UpdateStatementCompileResult(UpdateQuery updateQuery, Map<Integer, Argument> arguments) {
            this.updateQuery = updateQuery;
            this.arguments = arguments;
        }

        @Override
        public UpdateQuery getQuery() {
            return updateQuery;
        }

        @Override
        public Map<Integer, Argument> getArguments() {
            return arguments;
        }
    }
}
