package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.FieldAccessor;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;
import ru.saidgadjiev.ormnext.core.validator.entity.PrimaryKeyValidator;

import java.lang.reflect.Field;
import java.sql.SQLException;

/**
 * Created by said on 30.10.17.
 */
public class ForeignColumnType implements IForeignDatabaseColumnType {

    public static final String ID_SUFFIX = "_id";

    private IDatabaseColumnType foreignPrimaryKey;

    private boolean foreignAutoCreate;

    private Class<?> foreignFieldClass;

    private Class<?> ownerClass;

    private DataPersister dataPersister;

    private String foreignTableName;

    private Field field;

    private FieldAccessor fieldAccessor;

    private String columnName;

    private String tableName;

    private int dataType;

    private FetchType fetchType;

    @Override
    public Field getField() {
        return field;
    }

    public boolean isForeignAutoCreate() {
        return foreignAutoCreate;
    }

    public Class<?> getCollectionObjectClass() {
        return foreignFieldClass;
    }

    @Override
    public ForeignColumnKey getForeignColumnKey() {
        return new ForeignColumnKey(getOwnerTableName(), getColumnName());
    }

    @Override
    public DataPersister getDataPersister() {
        return dataPersister;
    }

    @Override
    public int getDataType() {
        return dataType;
    }

    public IDatabaseColumnType getForeignPrimaryKey() {
        return foreignPrimaryKey;
    }

    @Override
    public Object access(Object object) throws SQLException {
        try {
            return fieldAccessor.access(object);
        } catch (Throwable ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void assign(Object object, Object value) {
        fieldAccessor.assign(object, value);
    }

    @Override
    public String getColumnName() {
        if (columnName.endsWith(ID_SUFFIX)) {
            return columnName;
        }

        return columnName + ID_SUFFIX;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }

    public String getForeignColumnName() {
        return foreignPrimaryKey.getColumnName();
    }

    @Override
    public boolean isForeignFieldType() {
        return true;
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {
        visitor.visit(this);
        visitor.finish(this);
    }

    public Class<?> getOwnerClass() {
        return ownerClass;
    }

    @Override
    public String getOwnerTableName() {
        return tableName;
    }

    @Override
    public boolean insertable() {
        return true;
    }

    @Override
    public boolean updatable() {
        return true;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public static ForeignColumnType build(Field field) {
        if (!field.isAnnotationPresent(ForeignColumn.class)) {
            return null;
        }
        ForeignColumn foreignColumn = field.getAnnotation(ForeignColumn.class);
        new PrimaryKeyValidator().validate(field.getType());
        ForeignColumnType foreignColumnType = new ForeignColumnType();
        IDatabaseColumnType foreignPrimaryKey = DatabaseEntityMetadataUtils.resolvePrimaryKey(field.getType()).get();
        DataPersister<?> dataPersister = foreignPrimaryKey.getDataPersister();

        foreignColumnType.foreignAutoCreate = foreignColumn.foreignAutoCreate();
        foreignColumnType.foreignPrimaryKey = foreignPrimaryKey;
        foreignColumnType.foreignTableName = DatabaseEntityMetadataUtils.resolveTableName(foreignPrimaryKey.getField().getDeclaringClass());
        foreignColumnType.foreignFieldClass = field.getType();
        foreignColumnType.ownerClass = field.getDeclaringClass();
        foreignColumnType.dataPersister = dataPersister;
        foreignColumnType.dataType = foreignPrimaryKey.getDataType();
        foreignColumnType.field = field;
        foreignColumnType.fetchType = foreignColumn.fetchType();
        foreignColumnType.fieldAccessor = new FieldAccessor(field);
        foreignColumnType.columnName = foreignColumn.columnName().isEmpty() ? field.getName().toLowerCase() : foreignColumn.columnName();
        foreignColumnType.tableName = DatabaseEntityMetadataUtils.resolveTableName(field.getDeclaringClass());

        return foreignColumnType;
    }
}
