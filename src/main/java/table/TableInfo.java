package table;

import field.*;
import utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 19.03.17.
 */
public class TableInfo<T> {

    private Field id;
    private String name;
    private List<Field> oneToOneRelations = new ArrayList<>();
    private List<Field> oneToManyRelations = new ArrayList<>();
    private List<Field> manyToOneRelations = new ArrayList<>();
    private List<Field> manyToManyRelations = new ArrayList<>();
    private List<Field> fields = new ArrayList<>();
    private Class<T> table;

    public TableInfo(Class<T> dbTable) {
        this.name = dbTable.getAnnotation(DBTable.class).name();
        this.table = dbTable;
        for (Field field : dbTable.getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                if (field.getAnnotation(DBField.class).id()) {
                    id = field;
                } else if (field.isAnnotationPresent(OneToOne.class)) {
                    oneToOneRelations.add(field);
                } else if (field.isAnnotationPresent(ManyToOne.class)) {
                    manyToOneRelations.add(field);
                } else {
                    fields.add(field);
                }
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                oneToManyRelations.add(field);
            } else if (field.isAnnotationPresent(ManyToMany.class)) {
                manyToManyRelations.add(field);
            }
        }
        fields.add(id);
    }

    public Field getId() {
        return id;
    }

    public List<Field> getOneToOneRelations() {
        return oneToOneRelations;
    }

    public List<Field> getOneToManyRelations() {
        return oneToManyRelations;
    }

    public List<Field> getManyToManyRelations() {
        return manyToManyRelations;
    }

    public List<Field> getManyToOneRelations() {
        return manyToOneRelations;
    }

    public String getTableName() {
        return name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Class<T> getTable() {
        return table;
    }

    public Field getFieldByMappedNameInOneToManyRelation(String name) {
        for (Field field : oneToManyRelations) {
            if (field.getAnnotation(OneToMany.class).mappedBy().equals(name)) {
                return field;
            }
        }

        return null;
    }

    public Field getFieldByMappedByNameInChild(String name) {
        for (Field field : manyToOneRelations) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

    public Field getFieldByNameInManyToManyRelation(String name) {
        for (Field field : manyToManyRelations) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

}
