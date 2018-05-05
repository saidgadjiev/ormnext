package ru.saidgadjiev.ormnext.core.utils;

/**
 * Created by said on 03.02.2018.
 */
public class ExceptionUtils {

    private ExceptionUtils() {}

    public static String message(Exception ex, Object ... args) {
        return String.format(ex.getMessage(), args);
    }

    public enum Exception {
        WRONG_TYPE("Field %s type maybe should be in [%s]"),
        PRIMARY_KEY_MISS("Type %s doesn't has primary key"),
        WRONG_GENERATED_TYPE("For generated value use next types: [%s]");

        private String message;

        Exception(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
