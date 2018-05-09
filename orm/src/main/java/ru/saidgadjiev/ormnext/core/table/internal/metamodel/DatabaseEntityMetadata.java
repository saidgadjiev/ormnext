package ru.saidgadjiev.ormnext.core.table.internal.metamodel;

import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.field.fieldtype.*;
import ru.saidgadjiev.ormnext.core.table.DatabaseEntity;
import ru.saidgadjiev.ormnext.core.table.Index;
import ru.saidgadjiev.ormnext.core.table.Unique;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityElement;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;
import ru.saidgadjiev.ormnext.core.validator.entity.EntityValidator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public final class DatabaseEntityMetadata<T> implements EntityElement {

    private Class<T> tableClass;

    private List<DatabaseColumnType> databaseColumnTypes;

    private List<ForeignColumnType> foreignColumnypes;

    private List<ForeignCollectionColumnType> foreignCollectionColumnTypes;

    private List<UniqueFieldType> uniqueFieldTypes;

    private List<IndexFieldType> indexFieldTypes;

    private List<IDatabaseColumnType> fieldTypes;

    private IDatabaseColumnType primaryKeyFieldType;

    private String tableName;

    private DatabaseEntityMetadata(Class<T> tableClass,
                                   List<UniqueFieldType> uniqueFieldTypes,
                                   List<IndexFieldType> indexFieldTypes,
                                   String tableName,
                                   List<IDatabaseColumnType> fieldTypes) {
        this.tableClass = tableClass;
        this.tableName = tableName;
        this.indexFieldTypes = indexFieldTypes;
        this.primaryKeyFieldType = fieldTypes
                .stream()
                .filter(IDatabaseColumnType::isId)
                .findAny()
                .orElse(null);
        this.databaseColumnTypes = fieldTypes
                .stream()
                .filter(IDatabaseColumnType::isDbFieldType)
                .map(idbFieldType -> (DatabaseColumnType) idbFieldType)
                .collect(Collectors.toList());
        this.foreignColumnypes = fieldTypes
                .stream()
                .filter(IDatabaseColumnType::isForeignFieldType)
                .map(idbFieldType -> (ForeignColumnType) idbFieldType)
                .collect(Collectors.toList());
        this.foreignCollectionColumnTypes = fieldTypes
                .stream()
                .filter(IDatabaseColumnType::isForeignCollectionFieldType)
                .map(idbFieldType -> (ForeignCollectionColumnType) idbFieldType)
                .collect(Collectors.toList());
        this.uniqueFieldTypes = uniqueFieldTypes;
        this.fieldTypes = fieldTypes;
    }

    public Class<T> getTableClass() {
        return tableClass;
    }

    public String getTableName() {
        return tableName;
    }

    public IDatabaseColumnType getPrimaryKey() {
        return primaryKeyFieldType;
    }

    public List<UniqueFieldType> getUniqueFieldTypes() {
        return uniqueFieldTypes;
    }

    public List<DatabaseColumnType> toDBFieldTypes() {
        return Collections.unmodifiableList(databaseColumnTypes);
    }

    public List<ForeignColumnType> toForeignFieldTypes() {
        return Collections.unmodifiableList(foreignColumnypes);
    }

    public List<ForeignCollectionColumnType> toForeignCollectionFieldTypes() {
        return Collections.unmodifiableList(foreignCollectionColumnTypes);
    }

    public List<IDatabaseColumnType> getFieldTypes() {
        return Collections.unmodifiableList(fieldTypes);
    }

    public List<IndexFieldType> getIndexFieldTypes() {
        return indexFieldTypes;
    }

    public static <T> DatabaseEntityMetadata<T> build(Class<T> clazz) {
        new EntityValidator().validate(clazz);
        String tableName = DatabaseEntityMetadataUtils.resolveTableName(clazz);

        List<IDatabaseColumnType> fieldTypes = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            FieldTypeUtils.create(field).ifPresent(fieldTypes::add);
        }

        return new DatabaseEntityMetadata<>(
                clazz,
                resolveUniques(fieldTypes, clazz),
                resolveIndexes(fieldTypes, tableName, clazz),
                DatabaseEntityMetadataUtils.resolveTableName(clazz),
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

        for (String propertyName : index.columns()) {
            columns.add(fieldTypes
                    .stream()
                    .filter(idbFieldType -> {
                        if (idbFieldType.isForeignCollectionFieldType()) {
                            return false;
                        }
                        return idbFieldType.getField().getName().equals(propertyName);
                    })
                    .findAny()
                    .orElseThrow(() -> new PropertyNotFoundException(clazz, propertyName))
                    .getColumnName());
        }

        return columns;
    }

    private static List<String> validateUnique(List<IDatabaseColumnType> fieldTypes, Unique unique, Class<?> clazz) {
        List<String> columns = new ArrayList<>();

        for (String propertyName : unique.columns()) {
            columns.add(fieldTypes
                    .stream()
                    .filter(idbFieldType -> {
                        if (idbFieldType.isForeignCollectionFieldType()) {
                            return false;
                        }

                        return idbFieldType.getField().getName().equals(propertyName);
                    })
                    .findAny()
                    .orElseThrow(() -> new PropertyNotFoundException(clazz, propertyName))
                    .getColumnName());
        }

        return columns;
    }

    public String getColumnNameByPropertyName(String propertyName) {
        for (IDatabaseColumnType fieldType: fieldTypes) {
            if (fieldType.isForeignCollectionFieldType()) {
                continue;
            }

            if (fieldType.getFieldName().equals(propertyName)) {
                return fieldType.getColumnName();
            }
        }

        throw new PropertyNotFoundException(tableClass, propertyName);
    }

    public IDatabaseColumnType getDataTypeByPropertyName(String propertyName) {
        for (IDatabaseColumnType fieldType: fieldTypes) {
            if (fieldType.isForeignCollectionFieldType()) {
                continue;
            }

            if (fieldType.getFieldName().equals(propertyName)) {
                return fieldType;
            }
        }

        throw new PropertyNotFoundException(tableClass, propertyName);
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {
        if (visitor.visit(this)) {
            for (IDatabaseColumnType columnType: fieldTypes) {
                columnType.accept(visitor);
            }
        }
    }
}


