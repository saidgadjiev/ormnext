package ru.saidgadjiev.ormnext.core.logger;

import java.lang.reflect.Constructor;

/**
 * Logger factory. Create appropriate logger by log class property {@link #LOG_CLASS_PROPERTY}.
 *
 * @author Said Gadjiev
 */
public final class LoggerFactory {

    /**
     * Property for log class. It must implement {@link Log}.
     */
    public static final String LOG_CLASS_PROPERTY = "ormnext.logger";

    /**
     * Log enable or disable property. Default disable.
     */
    public static final String LOG_ENABLED_PROPERTY = "ormnext.log.enabled";

    /**
     * Can't be instantiated.
     */
    private LoggerFactory() { }

    /**
     * Create appropriate logger for {@code clazz}.
     * @param clazz target class
     * @return logger
     */
    public static Log getLogger(Class<?> clazz) {
        Boolean enable = Boolean.valueOf(System.getProperty(LOG_ENABLED_PROPERTY));

        if (enable) {
            String loggerClassName = System.getProperty(LOG_CLASS_PROPERTY);

            if (loggerClassName != null) {
                try {
                    return loadLogger(clazz, loggerClassName);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            return new Log4j(clazz);
        } else {
            return new DummyLog();
        }
    }

    /**
     * Load and create a new logger class instance which implement {@link Log} and has constructor
     * with one argument logged class {@code clazz}.
     * @param clazz target logged class
     * @param loggerClassName target logger class name
     * @return logger
     * @throws Exception any exceptions
     */
    private static Log loadLogger(Class<?> clazz, String loggerClassName) throws Exception {
        Class<?> logClass = Class.forName(loggerClassName);
        @SuppressWarnings("Unchecked")
        Constructor<Log> logConstructor = (Constructor<Log>) logClass.getConstructor(String.class);

        return logConstructor.newInstance(clazz);
    }
}
