package ru.saidgadjiev.orm.next.core.logger;

import java.lang.reflect.Constructor;

public class LoggerFactory {

    public static final String LOG_CLASS_PROPERTY = "orm.next.logger";

    private LoggerFactory() {

    }

    public static Log getLogger(Class<?> clazz) throws Exception {
        String loggerClassName = System.getProperty(LOG_CLASS_PROPERTY);

        if (loggerClassName != null) {
            return loadLogger(clazz.getName(), loggerClassName);
        }

        return new Log4j(clazz.getName());
    }

    private static Log loadLogger(String clazzName, String loggerClassName) throws Exception {
        Class<?> logClass = Class.forName(loggerClassName);
        @SuppressWarnings("Unchecked")
        Constructor<Log> logConstructor = (Constructor<Log>) logClass.getConstructor(String.class);

        return logConstructor.newInstance(clazzName);
    }
}
