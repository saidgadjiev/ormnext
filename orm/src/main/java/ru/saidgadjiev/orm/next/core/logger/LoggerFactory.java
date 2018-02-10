package ru.saidgadjiev.orm.next.core.logger;

import java.lang.reflect.Constructor;

public class LoggerFactory {

    public static final String LOG_CLASS_PROPERTY = "orm.next.logger";

    public static final String LOG_ENABLED_PROPERTY = "orm.next.log.enabled";

    private LoggerFactory() {

    }

    public static Log getLogger(Class<?> clazz) {
        String loggerClassName = System.getProperty(LOG_CLASS_PROPERTY);

        if (loggerClassName != null) {
            try {
                return loadLogger(clazz.getName(), loggerClassName);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
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
