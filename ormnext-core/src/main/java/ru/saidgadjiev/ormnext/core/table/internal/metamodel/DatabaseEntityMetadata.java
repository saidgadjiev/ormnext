package ru.saidgadjiev.ormnext.core.table.internal.metamodel;

import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.field.fieldtype.*;
import ru.saidgadjiev.ormnext.core.table.DatabaseEntity;
import ru.saidgadjiev.ormnext.core.table.Index;
import ru.saidgadjiev.ormnext.core.table.Unique;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityElement;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.FieldTypeUtils;
import ru.saidgadjiev.ormnext.core.validator.entity.EntityValidator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils.getColumnNameByPropertyName;
import static ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils.resolveTableName;

/**
 * This class represent meta info entity class eg. holds entity class, resolved entity column types, table name.
 * It is immutable class.
 *
 * @author Said Gadjiev
 */
public final class DatabaseEntityMetadata<T> implements EntityElement {

    /**
     * Entity class.
     */
    private Class<T> tableClass;

    /**
     * Resolved database column types.
     *
     * @see SimpleDatabaseColumnTypeImpl
     */
    private List<SimpleDatabaseColumnTypeImpl> databaseColumnTypes;

    /**
     * Resolved foreign column types.
     *
     * @see ForeignColumnTypeImpl
     */
    private List<ForeignColumnTypeImpl> foreignColumnypes;

    /**
     * Resolved foreign collection column types.
     *
     * @see ForeignCollectionColumnTypeImpl
     */
    private List<ForeignCollectionColumnTypeImpl> foreignCollectionColumnTypes;

    /**
     * Resolved unique columns.
     *
     * @see UniqueColumns
     */
    private List<UniqueColumns> uniqueColumns;

    /**
     * Resolved indexes.
     *
     * @see IndexColumns
     */
    private List<IndexColumns> indexColumns;

    /**
     * All resolved column types together.
     *
     * @see DatabaseColumnType
     */
    private List<DatabaseColumnType> fieldTypes;

    /**
     * Resolved primary key type.
     */
    private DatabaseColumnType primaryKeyFieldType;

    /**
     * Table name.
     */
    private String tableName;

    /**
     * Create a new instance. It may be only from {@link #build(Class)} method.
     *
     * @param tableClass    entity class
     * @param uniqueColumns resolved unique columns
     * @param indexColumns  resolved index columns
     * @param tableName     resolved table name
     * @param columnTypes   resolved column types.
     */
    private DatabaseEntityMetadata(Class<T> tableClass,
                                   List<UniqueColumns> uniqueColumns,
                                   List<IndexColumns> indexColumns,
                                   String tableName,
                                   List<DatabaseColumnType> columnTypes) {
        this.tableClass = tableClass;
        this.tableName = tableName;
        this.indexColumns = indexColumns;
        this.primaryKeyFieldType = columnTypes
                .stream()
                .filter(DatabaseColumnType::id)
                .findAny()
                .orElse(null);
        this.databaseColumnTypes = columnTypes
                .stream()
                .filter(DatabaseColumnType::databaseColumnType)
                .map(idbFieldType -> (SimpleDatabaseColumnTypeImpl) idbFieldType)
                .collect(Collectors.toList());
        this.foreignColumnypes = columnTypes
                .stream()
                .filter(DatabaseColumnType::foreignColumnType)
                .map(idbFieldType -> (ForeignColumnTypeImpl) idbFieldType)
                .collect(Collectors.toList());
        this.foreignCollectionColumnTypes = columnTypes
                .stream()
                .filter(DatabaseColumnType::foreignCollectionColumnType)
                .map(idbFieldType -> (ForeignCollectionColumnTypeImpl) idbFieldType)
                .collect(Collectors.toList());
        this.uniqueColumns = uniqueColumns;
        this.fieldTypes = columnTypes;
    }

    /**
     * Return entity class.
     *
     * @return entity class
     */
    public Class<T> getTableClass() {
        return tableClass;
    }

    /**
     * Return table name.
     *
     * @return table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return primary key column type.
     *
     * @return primary key column type
     */
    public DatabaseColumnType getPrimaryKeyColumnType() {
        return primaryKeyFieldType;
    }

    /**
     * Return resolved database column types.
     *
     * @return resolved database column types
     */
    public List<SimpleDatabaseColumnTypeImpl> toDatabaseColumnTypes() {
        return Collections.unmodifiableList(databaseColumnTypes);
    }

    /**
     * Resolved resolved foreign column types.
     *
     * @return resolved foreign column types
     */
    public List<ForeignColumnTypeImpl> toForeignColumnTypes() {
        return Collections.unmodifiableList(foreignColumnypes);
    }

    /**
     * Resolved resolved foreign collection column types.
     *
     * @return resolved foreign collection column types
     */
    public List<ForeignCollectionColumnTypeImpl> toForeignCollectionColumnTypes() {
        return Collections.unmodifiableList(foreignCollectionColumnTypes);
    }

    /**
     * Return all resolved column types.
     *
     * @return all resolved column types
     */
    public List<DatabaseColumnType> getColumnTypes() {
        return Collections.unmodifiableList(fieldTypes);
    }

    /**
     * Return unique columns.
     *
     * @return unique columns
     */
    public List<UniqueColumns> getUniqueColumns() {
        return Collections.unmodifiableList(uniqueColumns);
    }

    /**
     * Return index columns.
     *
     * @return index columns
     */
    public List<IndexColumns> getIndexColumns() {
        return Collections.unmodifiableList(indexColumns);
    }

    /**
     * Build new entity meta data by entity class.
     *
     * @param clazz target class
     * @param <T>   entity class type
     * @return built entity meta data
     */
    public static <T> DatabaseEntityMetadata<T> build(Class<T> clazz) {
        new EntityValidator().validate(clazz);
        String tableName = resolveTableName(clazz);

        List<DatabaseColumnType> fieldTypes = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            FieldTypeUtils.create(field).ifPresent(fieldTypes::add);
        }

        return new DatabaseEntityMetadata<>(
                clazz,
                resolveUniques(fieldTypes, clazz),
                resolveIndexes(fieldTypes, tableName, clazz),
                resolveTableName(clazz),
                fieldTypes
        );
    }

    /**
     * Resolve unique columns.
     *
     * @param columnTypes resolved column types
     * @param tClass      entity class
     * @param <T>         entity class type
     * @return resolved unique columns
     */
    private static <T> List<UniqueColumns> resolveUniques(List<DatabaseColumnType> columnTypes, Class<T> tClass) {
        List<UniqueColumns> uniqueColumns = new ArrayList<>();
        if (tClass.isAnnotationPresent(DatabaseEntity.class)) {
            Unique[] uniques = tClass.getAnnotation(DatabaseEntity.class).uniqueConstraints();

            for (Unique unique : uniques) {
                uniqueColumns.add(new UniqueColumns(validateUnique(columnTypes, unique, tClass)));
            }
        }

        return uniqueColumns;
    }

    /**
     * Resolve index columns.
     *
     * @param columnTypes resolved column types
     * @param tableName   resolved table name
     * @param tClass      entity class
     * @param <T>         entity class type
     * @return resolved index columns
     */
    private static <T> List<IndexColumns> resolveIndexes(List<DatabaseColumnType> columnTypes,
                                                         String tableName,
                                                         Class<T> tClass) {
        List<IndexColumns> uniqueFieldTypes = new ArrayList<>();

        if (tClass.isAnnotationPresent(DatabaseEntity.class)) {
            Index[] indexes = tClass.getAnnotation(DatabaseEntity.class).indexes();

            for (Index index : indexes) {
                uniqueFieldTypes.add(
                        new IndexColumns(
                                index.name(),
                                index.unique(),
                                tableName,
                                validateIndex(columnTypes, index, tClass)
                        )
                );
            }
        }

        return uniqueFieldTypes;
    }

    /**
     * Check index property names and return index columns if it is valid index.
     * Entity class has property annotated with {@link ru.saidgadjiev.ormnext.core.field.DatabaseColumn}
     * or {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn} or not,
     * if not then throw {@link PropertyNotFoundException}.
     *
     * @param columnTypes resolved column types
     * @param index       target index
     * @param clazz       entity class
     * @return resolved index columns
     */
    private static List<String> validateIndex(List<DatabaseColumnType> columnTypes, Index index, Class<?> clazz) {
        List<String> columns = new ArrayList<>();

        for (String propertyName : index.columns()) {
            columns.add(getColumnNameByPropertyName(columnTypes, propertyName)
                    .orElseThrow(() -> new PropertyNotFoundException(clazz, propertyName)));
        }

        return columns;
    }

    /**
     * Check unique property names and return unique columns if it is valid unique constraint.
     * Entity class has property annotated with {@link ru.saidgadjiev.ormnext.core.field.DatabaseColumn}
     * or {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn} or not,
     * if not then throw {@link PropertyNotFoundException}.
     *
     * @param columnTypes resolved column types
     * @param unique      resolved unique
     * @param clazz       entity class
     * @return resolved unique columns
     */
    private static List<String> validateUnique(List<DatabaseColumnType> columnTypes, Unique unique, Class<?> clazz) {
        List<String> columns = new ArrayList<>();

        for (String propertyName : unique.columns()) {
            columns.add(getColumnNameByPropertyName(columnTypes, propertyName)
                    .orElseThrow(() -> new PropertyNotFoundException(clazz, propertyName)));
        }

        return columns;
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {
        if (visitor.start(this)) {
            for (DatabaseColumnType columnType : fieldTypes) {
                columnType.accept(visitor);
            }
        }
    }
}


