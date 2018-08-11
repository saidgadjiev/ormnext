package ru.saidgadjiev.ormnext.core.query.criteria;

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
 * Created by said on 11.08.2018.
 */
public class StatementCompiler {

    private final MetaModel metaModel;

    public StatementCompiler(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    public SelectStatementCompileResult compile(SelectStatement<?> selectStatement) {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(selectStatement.getEntityClass());
        SelectQuery selectQuery = entityPersister.getEntityQuerySpace().getSelectQuery(selectStatement);

        return new SelectStatementCompileResult(
                selectQuery,
                getArguments(entityPersister.getMetadata(), selectStatement)
        );
    }

    public DeleteStatementCompileResult compile(DeleteStatement deleteStatement) {
        DatabaseEntityPersister persister = metaModel.getPersister(deleteStatement.getEntityClass());
        DeleteQuery deleteQuery = persister.getEntityQuerySpace().getDeleteQuery(deleteStatement);

        return new DeleteStatementCompileResult(deleteQuery, getArguments(persister.getMetadata(), deleteStatement));
    }

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

    public static class SelectStatementCompileResult {

        private final SelectQuery selectQuery;

        private final Map<Integer, Argument> arguments;

        public SelectStatementCompileResult(SelectQuery selectQuery, Map<Integer, Argument> arguments) {
            this.selectQuery = selectQuery;
            this.arguments = arguments;
        }

        public SelectQuery getSelectQuery() {
            return selectQuery;
        }

        public Map<Integer, Argument> getArguments() {
            return arguments;
        }
    }

    public static class DeleteStatementCompileResult {

        private final DeleteQuery deleteQuery;

        private final Map<Integer, Argument> arguments;

        public DeleteStatementCompileResult(DeleteQuery deleteQuery, Map<Integer, Argument> arguments) {
            this.deleteQuery = deleteQuery;
            this.arguments = arguments;
        }

        public DeleteQuery getDeleteQuery() {
            return deleteQuery;
        }

        public Map<Integer, Argument> getArguments() {
            return arguments;
        }
    }

    public static class UpdateStatementCompileResult {

        private final UpdateQuery updateQuery;

        private final Map<Integer, Argument> arguments;

        public UpdateStatementCompileResult(UpdateQuery updateQuery, Map<Integer, Argument> arguments) {
            this.updateQuery = updateQuery;
            this.arguments = arguments;
        }

        public UpdateQuery getUpdateQuery() {
            return updateQuery;
        }

        public Map<Integer, Argument> getArguments() {
            return arguments;
        }
    }
}
