package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.FieldAccessor;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.field.ReferenceAction;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;
import ru.saidgadjiev.ormnext.core.validator.entity.PrimaryKeyValidator;

import java.lang.reflect.Field;

/**
 * This class represent column type annotated with {@link ForeignColumn}.
 *
 * @author said gadjiev
 */
public class ForeignColumnType extends BaseDatabaseColumnType implements IForeignDatabaseColumnType {

    /**
     * Foreign column suffix.
     */
    public static final String ID_SUFFIX = "_id";

    /**
     * Primary key reference column type.
     */
    private IDatabaseColumnType foreignPrimaryKey;

    /**
     * Is foreign auto create.
     */
    private boolean foreignAutoCreate;

    /**
     * Foreign field class.
     */
    private Class<?> foreignFieldClass;

    /**
     * Foreign table name.
     */
    private String foreignTableName;

    /**
     * Current field which annotated by {@link ForeignColumn}.
     */
    private Field field;

    /**
     * Field accessor.
     */
    private FieldAccessor fieldAccessor;

    /**
     * Column name.
     */
    private String columnName;

    /**
     * Owner table name.
     */
    private String tableName;

    /**
     * Fetch type.
     *
     * @see FetchType
     */
    private FetchType fetchType;

    /**
     * References action on delete.
     */
    private ReferenceAction onUpdate;

    /**
     * References action on update.
     */
    private ReferenceAction onDelete;

    /**
     * Column type.
     */
    private IDatabaseColumnType databaseColumnType;

    @Override
    public Field getField() {
        return field;
    }

    /**
     * Is foreign auto create.
     *
     * @return true if foreign auto create
     */
    @Override
    public boolean foreignAutoCreate() {
        return foreignAutoCreate;
    }

    /**
     * Return foreign field class.
     *
     * @return foreign field class
     */
    public Class<?> getForeignFieldClass() {
        return foreignFieldClass;
    }

    @Override
    public ColumnKey getColumnKey() {
        return new ColumnKey(getTableName(), columnName());
    }

    @Override
    public DataPersister dataPersister() {
        return foreignPrimaryKey.dataPersister();
    }

    @Override
    public int dataType() {
        return foreignPrimaryKey.dataType();
    }

    /**
     * Return current primary key reference.
     *
     * @return current primary key reference.
     */
    public IDatabaseColumnType getForeignPrimaryKey() {
        return foreignPrimaryKey;
    }

    @Override
    public String defaultDefinition() {
        return databaseColumnType == null ? super.defaultDefinition() : databaseColumnType.defaultDefinition();
    }

    @Override
    public boolean notNull() {
        return databaseColumnType == null ? super.notNull() : databaseColumnType.notNull();
    }

    @Override
    public boolean defaultIfNull() {
        return databaseColumnType == null ? super.defaultIfNull() : databaseColumnType.defaultIfNull();
    }

    @Override
    public boolean unique() {
        return databaseColumnType == null ? super.unique() : databaseColumnType.unique();
    }

    @Override
    public Object access(Object object) {
        return fieldAccessor.access(object);
    }

    @Override
    public void assign(Object object, Object value) {
        fieldAccessor.assign(object, value);
    }

    @Override
    public String columnName() {
        if (columnName.endsWith(ID_SUFFIX)) {
            return columnName;
        }

        return columnName + ID_SUFFIX;
    }

    @Override
    public String getForeignTableName() {
        return foreignTableName;
    }

    @Override
    public String getForeignColumnName() {
        return foreignPrimaryKey.columnName();
    }

    @Override
    public boolean foreignColumnType() {
        return true;
    }

    @Override
    public String getTableName() {
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

    @Override
    public boolean defineInCreateTable() {
        return databaseColumnType == null ? super.defineInCreateTable() : databaseColumnType.defineInCreateTable();
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }

    /**
     * On delete action.
     *
     * @return on delte action
     * @see ReferenceAction
     */
    public ReferenceAction getOnUpdate() {
        return onUpdate;
    }

    /**
     * On update action.
     *
     * @return on update action
     * @see ReferenceAction
     */
    public ReferenceAction getOnDelete() {
        return onDelete;
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {
        if (visitor.start(this)) {
            visitor.finish(this);
        }
    }

    /**
     * Method for build new instance by field.
     *
     * @param field target field
     * @return new instance
     */
    public static ForeignColumnType build(Field field) {
        if (!field.isAnnotationPresent(ForeignColumn.class)) {
            return null;
        }
        ForeignColumn foreignColumn = field.getAnnotation(ForeignColumn.class);
        new PrimaryKeyValidator().validate(field.getType());
        ForeignColumnType foreignColumnType = new ForeignColumnType();
        IDatabaseColumnType foreignPrimaryKey = DatabaseEntityMetadataUtils.resolvePrimaryKey(field.getType()).get();

        foreignColumnType.foreignAutoCreate = foreignColumn.foreignAutoCreate();
        foreignColumnType.foreignPrimaryKey = foreignPrimaryKey;
        foreignColumnType.foreignTableName = DatabaseEntityMetadataUtils
                .resolveTableName(foreignPrimaryKey.getField().getDeclaringClass());
        foreignColumnType.foreignFieldClass = field.getType();
        foreignColumnType.field = field;
        foreignColumnType.fetchType = foreignColumn.fetchType();
        foreignColumnType.fieldAccessor = new FieldAccessor(field);
        foreignColumnType.columnName = foreignColumn.columnName().isEmpty()
                ? field.getName().toLowerCase() : foreignColumn.columnName();
        foreignColumnType.tableName = DatabaseEntityMetadataUtils.resolveTableName(field.getDeclaringClass());
        foreignColumnType.databaseColumnType = DatabaseColumnType.build(field);
        foreignColumnType.onDelete = foreignColumn.onDelete();
        foreignColumnType.onUpdate = foreignColumn.onUpdate();

        return foreignColumnType;
    }
}
