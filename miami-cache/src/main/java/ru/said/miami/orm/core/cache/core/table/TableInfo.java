package ru.said.miami.orm.core.cache.core.table;


import ru.said.miami.orm.core.cache.core.field.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 19.03.17.
 */

@SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
public class TableInfo {

    private FieldWrapper id;
    private List<FieldWrapper> fields = new ArrayList<>();
    private List<MethodWrapper> methods = new ArrayList<>();
    private List<FieldWrapper> oneToOneRelations = new ArrayList<>();
    private List<FieldWrapper> oneToManyRelations = new ArrayList<>();
    private List<FieldWrapper> manyToManyRelations = new ArrayList<>();
    private List<FieldWrapper> manyToOneRelations = new ArrayList<>();
    private Class table;
    private String name;

    public TableInfo(Class dbTable) {
        for (Field field: dbTable.getDeclaredFields()) {
            fields.add(new FieldWrapper(field));
        }
        for (Method method: dbTable.getDeclaredMethods()) {
            methods.add(new MethodWrapper(method));
        }
        this.name = ((Table) dbTable.getAnnotation(Table.class)).name();
        this.table = dbTable;
    }

    public FieldWrapper getId() {
        if (id == null) {
            for (FieldWrapper fieldWrapper: fields) {
                if (fieldWrapper.isAnnotationPresent(TableField.class)
                        && ((TableField) fieldWrapper.getAnnotation(TableField.class)).id() ) {
                    this.id = fieldWrapper;
                }
            }
        }

        return id;
    }

    public List<FieldWrapper> getOneToOneRelations() {
        if (oneToOneRelations.isEmpty()) {
            for (FieldWrapper fieldWrapper : fields) {
                if (fieldWrapper.isAnnotationPresent(OneToOne.class)) {
                    oneToOneRelations.add(fieldWrapper);
                }
            }
        }

        return oneToOneRelations;
    }

    public List<FieldWrapper> getOneToManyRelations() {
        if (oneToManyRelations.isEmpty()) {
            for (FieldWrapper fieldWrapper : fields) {
                if (fieldWrapper.isAnnotationPresent(OneToMany.class)) {
                    oneToManyRelations.add(fieldWrapper);
                }
            }
        }

        return oneToManyRelations;
    }

    public List<FieldWrapper> getManyToManyRelations() {
        if (manyToManyRelations.isEmpty()) {
            for (FieldWrapper fieldWrapper : fields) {
                if (fieldWrapper.isAnnotationPresent(ManyToMany.class)) {
                    manyToManyRelations.add(fieldWrapper);
                }
            }
        }

        return manyToManyRelations;
    }

    public List<FieldWrapper> getManyToOneRelations() {
        if (manyToOneRelations.isEmpty()) {
            for (FieldWrapper fieldWrapper : fields) {
                if (fieldWrapper.isAnnotationPresent(ManyToOne.class)) {
                    manyToOneRelations.add(fieldWrapper);
                }
            }
        }

        return manyToOneRelations;
    }

    public String getTableName() {
        return name;
    }

    public List<MethodWrapper> getMethods() {
        return methods;
    }

    public MethodWrapper getMethodByName(String name) {
        for (MethodWrapper methodWrapper: methods) {
            if (methodWrapper.getName().equals(name)) {
                return methodWrapper;
            }
        }

        return null;
    }

    public List<FieldWrapper> getFields() {
        return fields;
    }

    public Class getTable() {
        return table;
    }

    public FieldWrapper getFieldByMappedNameInOneToManyRelation(String name) {
        for (FieldWrapper fieldWrapper : oneToManyRelations) {
            if (((OneToMany) fieldWrapper.getAnnotation(OneToMany.class)).mappedBy().equals(name)) {
                return fieldWrapper;
            }
        }

        return null;
    }

    public FieldWrapper getFieldByMappedByNameInChild(String name) {
        for (FieldWrapper fieldWrapper : manyToOneRelations) {
            if (fieldWrapper.getName().equals(name)) {
                return fieldWrapper;
            }
        }

        return null;
    }

    public FieldWrapper getFieldByNameInManyToManyRelation(String name) {
        for (FieldWrapper fieldWrapper : manyToManyRelations) {
            if (fieldWrapper.getName().equals(name)) {
                return fieldWrapper;
            }
        }

        return null;
    }

}
