package field;

import table.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by said on 17.06.17.
 */
public class FieldWrapper {

    private Map<Class, Object> annotationsMap = new HashMap<>();

    private Map<Object, Object> values = new HashMap<>();

    private String name;

    private Annotation[] annotations;

    private Field field;

    private Class type;

    public FieldWrapper(Field field) {
        this.field = field;
    }

    public String getName() {
        if (name == null) {
            name = field.getName();
        }

        return name;
    }

    public Object getValue(Object object) throws IllegalAccessException {
        if (values.containsKey(object)) {
            values.put(object, field.get(object));
        }

        return values.get(object);
    }

    public Field getField() {
        return field;
    }

    public Annotation[] getAnnotations() {
        if (annotations == null) {
            annotations = field.getAnnotations();
        }

        return annotations;
    }

    public Class getType() {
        if (type == null) {
            type = field.getType();
        }

        return type;
    }


    public Object getAnnotation(Class annotationClass) {
        if (annotationsMap.containsKey(annotationClass)) {
            annotationsMap.put(annotationClass, field.getDeclaredAnnotation(annotationClass));
        }

        return annotationsMap.get(annotationClass);
    }

    public boolean isAnnotationPresent(Class annotaitonClass) {
        return field.isAnnotationPresent(annotaitonClass);
    }
}
