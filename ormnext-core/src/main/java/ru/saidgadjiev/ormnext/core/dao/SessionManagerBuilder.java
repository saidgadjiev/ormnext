package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection.source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.dialect.Dialect;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;
import java.util.*;

/**
 * Builder for session manager.
 *
 * @author Said Gadjiev
 */
public class SessionManagerBuilder {

    /**
     * Entity classes.
     */
    private Collection<Class<?>> entityClasses = new ArrayList<>();

    /**
     * Connection source.
     */
    private ConnectionSource connectionSource;

    /**
     * Database type.
     *
     * @see Dialect
     */
    private Dialect dialect;

    /**
     * Database engine.
     *
     * @see DatabaseEngine
     */
    private DatabaseEngine<?> databaseEngine;

    /**
     * Table operation.
     *
     * @see TableOperation
     */
    private TableOperation tableOperation = TableOperation.NO_ACTION;

    /**
     * Provide entity classes.
     *
     * @param classes target classes
     * @return this for chain
     */
    public SessionManagerBuilder entities(Class<?>... classes) {
        entityClasses.addAll(Arrays.asList(classes));

        return this;
    }

    /**
     * Provide connection source.
     *
     * @param connectionSource target connection source
     * @return this for chain
     */
    public SessionManagerBuilder connectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;

        return this;
    }

    /**
     * Provide database type.
     *
     * @param dialect target database type
     * @return this for chain
     */
    public SessionManagerBuilder dialect(Dialect dialect) {
        this.dialect = dialect;

        return this;
    }

    /**
     * Database engine.
     *
     * @param databaseEngine target database engine
     * @return this for chain
     */
    public SessionManagerBuilder databaseEngine(DatabaseEngine<?> databaseEngine) {
        this.databaseEngine = databaseEngine;

        return this;
    }

    /**
     * Provide table operation.
     *
     * @param tableOperation target table operation
     * @return this for chain
     * @see TableOperation
     */
    public SessionManagerBuilder tableOperation(TableOperation tableOperation) {
        this.tableOperation = tableOperation;

        return this;
    }

    /**
     * Create session manager. If database engine not provided use {@link DefaultDatabaseEngine}.
     *
     * @return this for chain
     * @throws SQLException any SQL exceptions
     */
    public SessionManager build() throws SQLException {
        DatabaseEngine engine = databaseEngine == null
                ? dialect == null
                ? null : new DefaultDatabaseEngine(dialect) : databaseEngine;

        if (engine == null) {
            throw new IllegalArgumentException("Please provide " + Dialect.class + " or " + DatabaseEngine.class);
        }
        MetaModel metaModel = new MetaModel(entityClasses);

        SessionManager sessionManager = new SessionManagerImpl(
                connectionSource,
                metaModel,
                engine
        );

        try (Session session = sessionManager.createSession()) {
            doTableOperations(entityClasses, session);
        }

        return sessionManager;
    }

    /**
     * Do table operation.
     *
     * @param classes target entity classes
     * @param session target session
     * @throws SQLException any SQL exceptions
     * @see TableOperation
     */
    private void doTableOperations(Collection<Class<?>> classes, Session session) throws SQLException {
        switch (tableOperation) {
            case CREATE:
                session.createTables(classes.toArray(new Class<?>[classes.size()]), true);

                break;
            case CLEAR:
                session.clearTables(classes.toArray(new Class<?>[classes.size()]));

                break;
            case DROP_CREATE:
                List<Class<?>> reversedEntityClasses = new ArrayList<>(classes);

                Collections.reverse(reversedEntityClasses);

                session.dropTables(
                        reversedEntityClasses.toArray(new Class<?>[reversedEntityClasses.size()]),
                        true
                );
                session.createTables(classes.toArray(new Class<?>[classes.size()]), true);
                break;
            default:
                break;
        }
    }
}
