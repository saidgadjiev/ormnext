package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;

import java.sql.SQLException;

/**
 * Created by said on 23.07.2018.
 */
public class WriteEntity implements EntityMetadataVisitor {

    private ResultSetContext context;

    private ResultSetContext.EntityProcessingState processingState;

    private EntityAliases entityAliases;

    private DatabaseEntityPersister persister;

    public WriteEntity(ResultSetContext context,
                       ResultSetContext.EntityProcessingState processingState,
                       EntityAliases entityAliases,
                       DatabaseEntityPersister persister) {
        this.context = context;
        this.processingState = processingState;
        this.entityAliases = entityAliases;
        this.persister = persister;
    }

    @Override
    public boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata) throws SQLException {
        return true;
    }

    @Override
    public boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException {
        String alias = entityAliases.getAliasByColumnName(foreignColumnType.columnName());

        if (context.isResultColumn(alias)) {
            ResultSetValue resultSetValue = processingState.getValues().get(alias);

            if (foreignColumnType.getFetchType().equals(FetchType.LAZY)) {
                Object proxy = persister.createProxy(
                        context.getSession(),
                        foreignColumnType.getForeignFieldClass(),
                        foreignColumnType.getForeignDatabaseColumnType().getField().getName(),
                        resultSetValue.getValue()
                );

                foreignColumnType.assign(processingState.getEntityInstance(), proxy);
            } else {
                    Object value = context.getEntry(
                            foreignColumnType.getForeignFieldClass(),
                            resultSetValue.getValue()
                    );

                    if (value == null) {
                        String propertyName = foreignColumnType.getForeignDatabaseColumnType()
                                .getField()
                                .getName();

                        SelectStatement<?> selectStatement = new SelectStatement<>(
                                foreignColumnType.getForeignFieldClass()
                        );

                        selectStatement.where(new Criteria()
                                .add(Restrictions.eq(propertyName, resultSetValue.getValue()))
                        );

                        value = context.getSession().uniqueResult(selectStatement);
                    }

                    foreignColumnType.assign(processingState.getEntityInstance(), value);
            }
        }

        return false;
    }

    @Override
    public boolean start(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) throws SQLException {
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
        if (!databaseColumnType.id()) {
            String alias = entityAliases.getAliasByColumnName(databaseColumnType.columnName());

            if (context.isResultColumn(alias)) {
                ResultSetValue resultSetValue = processingState.getValues().get(alias);

                databaseColumnType.assign(processingState.getEntityInstance(), resultSetValue.getValue());
            }
        }

        return false;
    }

    @Override
    public void finish(SimpleDatabaseColumnTypeImpl databaseColumnType) {

    }

    @Override
    public void finish(DatabaseEntityMetadata<?> entityMetadata) {

    }
}
