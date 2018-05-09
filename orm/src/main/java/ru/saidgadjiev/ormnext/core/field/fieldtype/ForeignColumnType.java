package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.FieldAccessor;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseMetaDataUtils;
import ru.saidgadjiev.ormnext.core.validator.datapersister.GeneratedTypeValidator;
import ru.saidgadjiev.ormnext.core.validator.entity.PrimaryKeyValidator;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseMetaDataUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    public void assign(Object object, Object value) throws SQLException {
        try {
            fieldAccessor.assign(object, value);
        } catch (Throwable ex) {
            throw new SQLException(ex);
        }
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setFieldAccessor(FieldAccessor fieldAccessor) {
        this.fieldAccessor = fieldAccessor;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
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

    public void setForeignPrimaryKey(IDatabaseColumnType foreignPrimaryKey) {
        this.foreignPrimaryKey = foreignPrimaryKey;
    }

    public void setForeignAutoCreate(boolean foreignAutoCreate) {
        this.foreignAutoCreate = foreignAutoCreate;
    }

    public void setForeignFieldClass(Class<?> foreignFieldClass) {
        this.foreignFieldClass = foreignFieldClass;
    }

    public void setDataPersister(DataPersister dataPersister) {
        this.dataPersister = dataPersister;
    }

    public void setForeignTableName(String foreignTableName) {
        this.foreignTableName = foreignTableName;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
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

    public void setOwnerTableName(String ownerTableName) {
        this.tableName = ownerTableName;
    }

    public void setOwnerClass(Class<?> ownerClass) {
        this.ownerClass = ownerClass;
    }

    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
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
        IDatabaseColumnType foreignPrimaryKey = DatabaseMetaDataUtils.resolvePrimaryKey(field.getType()).get();
        DataPersister<?> dataPersister = foreignPrimaryKey.getDataPersister();

        new GeneratedTypeValidator(field, foreignPrimaryKey.isGenerated()).validate(dataPersister);
        foreignColumnType.setForeignAutoCreate(foreignColumn.foreignAutoCreate());
        foreignColumnType.setForeignPrimaryKey(foreignPrimaryKey);
        foreignColumnType.setForeignTableName(DatabaseMetaDataUtils.resolveTableName(foreignPrimaryKey.getField().getDeclaringClass()));
        foreignColumnType.setForeignFieldClass(field.getType());
        foreignColumnType.setOwnerClass(field.getDeclaringClass());
        foreignColumnType.setDataPersister(dataPersister);
        foreignColumnType.setDataType(foreignPrimaryKey.getDataType());
        foreignColumnType.setField(field);
        foreignColumnType.setFetchType(foreignColumn.fetchType());
        foreignColumnType.setFieldAccessor(new FieldAccessor(field));
        foreignColumnType.setColumnName(foreignColumn.columnName().isEmpty() ? field.getName().toLowerCase() : foreignColumn.columnName());

        return foreignColumnType;
    }
}
