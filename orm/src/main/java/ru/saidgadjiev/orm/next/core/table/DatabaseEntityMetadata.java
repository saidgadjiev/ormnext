package ru.saidgadjiev.orm.next.core.table;

import ru.saidgadjiev.orm.next.core.field.field_type.*;
import ru.saidgadjiev.orm.next.core.utils.TableInfoUtils;
import ru.saidgadjiev.orm.next.core.validator.table.ForeignKeyValidator;
import ru.saidgadjiev.orm.next.core.validator.table.HasConstructorValidator;
import ru.saidgadjiev.orm.next.core.validator.table.IValidator;
import ru.saidgadjiev.orm.next.core.validator.table.PrimaryKeyValidator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.saidgadjiev.orm.next.core.utils.TableInfoUtils.lookupDefaultConstructor;

public final class DatabaseEntityMetadata<T> {

    private Class<T> tableClass;

    private List<DatabaseColumnType> databaseColumnTypes;

    private List<ForeignColumnype> foreignColumnypes;

    private List<ForeignCollectionFieldType> foreignCollectionFieldTypes;

    private List<UniqueFieldType> uniqueFieldTypes;

    private List<IndexFieldType> indexFieldTypes;

    private List<IDatabaseColumnType> fieldTypes;

    private IDatabaseColumnType primaryKeyFieldType;

    private String tableName;

    private Constructor<T> constructor;

    private static List<IValidator> validators = new ArrayList<IValidator>() {{
        add(new ForeignKeyValidator());
        add(new HasConstructorValidator());
        add(new PrimaryKeyValidator());
    }};

    private DatabaseEntityMetadata(Class<T> tableClass,
                                   Constructor<T> constructor,
                                   List<UniqueFieldType> uniqueFieldTypes,
                                   List<IndexFieldType> indexFieldTypes,
                                   String tableName,
                                   List<IDatabaseColumnType> fieldTypes) {
        this.tableClass = tableClass;
        this.tableName = tableName;
        this.constructor = constructor;
        this.indexFieldTypes = indexFieldTypes;
        this.primaryKeyFieldType = fieldTypes
                .stream()
                .filter(IDatabaseColumnType::isId)
                .findAny()
                .orElse(null);
        this.uniqueFieldTypes = uniqueFieldTypes;
        this.databaseColumnTypes = fieldTypes
                .stream()
                .filter(IDatabaseColumnType::isDbFieldType)
                .map(idbFieldType -> (DatabaseColumnType) idbFieldType)
                .collect(Collectors.toList());
        this.foreignColumnypes = fieldTypes
                .stream()
                .filter(IDatabaseColumnType::isForeignFieldType)
                .map(idbFieldType -> (ForeignColumnype) idbFieldType)
                .collect(Collectors.toList());
        this.foreignCollectionFieldTypes = fieldTypes
                .stream()
                .filter(IDatabaseColumnType::isForeignCollectionFieldType)
                .map(idbFieldType -> (ForeignCollectionFieldType) idbFieldType)
                .collect(Collectors.toList());
        this.fieldTypes = fieldTypes;
    }

    public Class<T> getTableClass() {
        return tableClass;
    }

    public String getTableName() {
        return tableName;
    }

    public Optional<IDatabaseColumnType> getPrimaryKey() {
        return Optional.ofNullable(primaryKeyFieldType);
    }

    public List<UniqueFieldType> getUniqueFieldTypes() {
        return uniqueFieldTypes;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public List<DatabaseColumnType> toDBFieldTypes() {
        return Collections.unmodifiableList(databaseColumnTypes);
    }

    public List<ForeignColumnype> toForeignFieldTypes() {
        return Collections.unmodifiableList(foreignColumnypes);
    }

    public List<ForeignCollectionFieldType> toForeignCollectionFieldTypes() {
        return Collections.unmodifiableList(foreignCollectionFieldTypes);
    }

    public List<IDatabaseColumnType> getFieldTypes() {
        return Collections.unmodifiableList(fieldTypes);
    }

    public List<IndexFieldType> getIndexFieldTypes() {
        return indexFieldTypes;
    }

    public static <T> DatabaseEntityMetadata<T> build(Class<T> clazz) {
        for (IValidator validator : validators) {
            validator.validate(clazz);
        }
        List<IDatabaseColumnType> fieldTypes = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            FieldTypeUtils.create(field).ifPresent(fieldTypes::add);
        }
        String tableName = TableInfoUtils.resolveTableName(clazz);

        return new DatabaseEntityMetadata<>(
                clazz,
                (Constructor<T>) lookupDefaultConstructor(clazz)
                        .orElseThrow(() -> new IllegalArgumentException("Class " + clazz + " doesn't have default constructor")),
                resolveUniques(fieldTypes, clazz),
                resolveIndexes(fieldTypes, tableName, clazz),
                TableInfoUtils.resolveTableName(clazz),
                fieldTypes
        );
    }

    private static <T> List<UniqueFieldType> resolveUniques(List<IDatabaseColumnType> fieldTypes, Class<T> tClass) {
        List<UniqueFieldType> uniqueFieldTypes = new ArrayList<>();
        if (tClass.isAnnotationPresent(DatabaseEntity.class)) {
            Unique[] uniques = tClass.getAnnotation(DatabaseEntity.class).uniqueConstraints();

            for (Unique unique : uniques) {
                uniqueFieldTypes.add(new UniqueFieldType(validateUnique(fieldTypes, unique, tClass)));
            }
        }

        return uniqueFieldTypes;
    }

    private static <T> List<IndexFieldType> resolveIndexes(List<IDatabaseColumnType> fieldTypes, String tableName, Class<T> tClass) {
        List<IndexFieldType> uniqueFieldTypes = new ArrayList<>();

        if (tClass.isAnnotationPresent(DatabaseEntity.class)) {
            Index[] indexes = tClass.getAnnotation(DatabaseEntity.class).indexes();

            for (Index index : indexes) {
                uniqueFieldTypes.add(new IndexFieldType(index.name(), index.unique(), tableName, validateIndex(fieldTypes, index, tClass)));
            }
        }

        return uniqueFieldTypes;
    }

    private static List<String> validateIndex(List<IDatabaseColumnType> fieldTypes, Index index, Class<?> clazz) {
        List<String> columns = new ArrayList<>();

        for (String columnName : index.columns()) {
            columns.add(fieldTypes
                    .stream()
                    .filter(idbFieldType -> {
                        if (idbFieldType.isForeignCollectionFieldType()) {
                            return false;
                        }
                        return idbFieldType.getField().getName().equals(columnName);
                    })
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Indexed column [" + columnName + "] not found in " + clazz))
                    .getColumnName());
        }

        return columns;
    }

    private static List<String> validateUnique(List<IDatabaseColumnType> fieldTypes, Unique unique, Class<?> clazz) {
        List<String> columns = new ArrayList<>();

        for (String columnName : unique.columns()) {
            columns.add(fieldTypes
                    .stream()
                    .filter(idbFieldType -> {
                        if (idbFieldType.isForeignCollectionFieldType()) {
                            return false;
                        }

                        return idbFieldType.getField().getName().equals(columnName);
                    })
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Unique column [" + columnName + "] not found in " + clazz))
                    .getColumnName());
        }

        return columns;
    }

    public String getPersistenceName(String fieldName) {
        for (IDatabaseColumnType fieldType: fieldTypes) {
            if (fieldType.isForeignCollectionFieldType()) {
                continue;
            }

            if (fieldType.getFieldName().equals(fieldName)) {
                return fieldType.getColumnName();
            }
        }

        throw new IllegalArgumentException("Field " + fieldName + " not exists in " + tableClass);
    }
}


