package ru.saidgadjiev.ormnext.core.logger;

public interface Log {

    void error(String message, Throwable t, Object ... args);

    void info(String message, Object ... args);

    void debug(String message, Object ... args);

    void debug(String message, Throwable t, Object ... args);

    void error(String message, Object ... args);

    void info(String message, Throwable t, Object ... args);

    void warn(String message, Object ... args);

    void warn(String message, Throwable t, Object ... args);
}
