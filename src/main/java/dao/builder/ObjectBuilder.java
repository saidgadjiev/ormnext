package dao.builder;

import field.FieldWrapper;
import field.MethodWrapper;
import utils.MethodNameUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by said on 17.06.17.
 */
public class ObjectBuilder {
    private Object object;
    private Map<FieldWrapper, Object> fieldWrapperObjectMap = new HashMap<>();
    private Map<FieldWrapper, Object> oneToOneRelations = new HashMap<>();
    private Map<FieldWrapper, Object> oneToManyRelations = new HashMap<>();

    public ObjectBuilder(Class clazz) throws InstantiationException, IllegalAccessException {
        object = clazz.newInstance();
    }

    public ObjectBuilder buildField(FieldWrapper fieldWrapper, Object value) {
        fieldWrapperObjectMap.put(fieldWrapper, value);

        return this;
    }

    public ObjectBuilder buildOneToOne(FieldWrapper fieldWrapper, Object value) {
        oneToOneRelations.put(fieldWrapper, value);

        return this;
    }


    public ObjectBuilder buildOneToMany(FieldWrapper fieldWrapper, Object value) {
        oneToManyRelations.put(fieldWrapper, value);

        return this;
    }

    public Object build() throws Exception {
        for (Map.Entry<FieldWrapper, Object> entry: fieldWrapperObjectMap.entrySet()) {
            new MethodWrapper(object.getClass().getDeclaredMethod(MethodNameUtils.makeSetterMethodName(entry.getKey()))).invoke(object, entry.getValue());
        }

        for (Map.Entry<FieldWrapper, Object> entry: oneToOneRelations.entrySet()) {
            new MethodWrapper(object.getClass().getDeclaredMethod(MethodNameUtils.makeSetterMethodName(entry.getKey()))).invoke(entry.getValue());
        }

        for (Map.Entry<FieldWrapper, Object> entry: oneToManyRelations.entrySet()) {
            ((List) new MethodWrapper(object.getClass().getDeclaredMethod(MethodNameUtils.makeGetterMethodName(entry.getKey())))).addAll((List) entry.getValue());
        }

        return object;
    }
}
