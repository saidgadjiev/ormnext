package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.dao.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by said on 30.10.17.
 */
public class ForeignColumnType implements IDatabaseColumnType {

    private static final String ID_SUFFIX = "_id";

    private IDatabaseColumnType foreignPrimaryKey;

    private boolean foreignAutoCreate;

    private Class<?> foreignFieldClass;

    private Class<?> ownerClass;

    private DataPersister dataPersister;

    private String foreignTableName;

    private Field field;

    private FieldAccessor fieldAccessor;

    private String columnName;

    private int dataType;

    @Override
    public Field getField() {
        return field;
    }

    public boolean isForeignAutoCreate() {
        return foreignAutoCreate;
    }

    public Class<?> getForeignFieldClass() {
        return foreignFieldClass;
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
    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return fieldAccessor.access(object);
    }

    @Override
    public void assign(Object object, Object value) {
        try {
            fieldAccessor.assign(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
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
    }

    public Class<?> getOwnerClass() {
        return ownerClass;
    }

    public void setOwnerClass(Class<?> ownerClass) {
        this.ownerClass = ownerClass;
    }
}
