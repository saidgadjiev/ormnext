package utils;

import field.FieldWrapper;

/**
 * Created by said on 17.06.17.
 */
public class MethodNameUtils {

    public static String makeGetterMethodName(FieldWrapper fieldWrapper) {
        String fieldName = fieldWrapper.getName();
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String makeSetterMethodName(FieldWrapper wrapper) {
        return  "set" + wrapper.getName().substring(0, 1).toUpperCase() + wrapper.getName().substring(1);
    }
}
