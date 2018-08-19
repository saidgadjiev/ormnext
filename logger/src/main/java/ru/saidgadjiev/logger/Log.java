package ru.saidgadjiev.logger;

/**
 * Log interface. Custom logger class should implement this interface
 * and has constructor with one argument logged class.
 *
 * @author Said Gadjiev
 */
public interface Log {

    /**
     * Log error.
     *
     * @param message message
     * @param t       clause
     * @param args    message args
     */
    void error(String message, Throwable t, Object... args);

    /**
     * Log info.
     *
     * @param message message
     * @param args    message args
     */
    void info(String message, Object... args);

    /**
     * Log debug.
     *
     * @param message message
     * @param args    message args
     */
    void debug(String message, Object... args);

    /**
     * Log debug.
     *
     * @param message message
     * @param t       clause
     * @param args    message args
     */
    void debug(String message, Throwable t, Object... args);

    /**
     * Log error.
     *
     * @param message message
     * @param args    message args
     */
    void error(String message, Object... args);

    /**
     * Log info.
     *
     * @param message message
     * @param t       clause
     * @param args    message args
     */
    void info(String message, Throwable t, Object... args);

    /**
     * Log warn.
     *
     * @param message message
     * @param args    message args
     */
    void warn(String message, Object... args);

    /**
     * Log warn.
     *
     * @param message message
     * @param t       clause
     * @param args    message args
     */
    void warn(String message, Throwable t, Object... args);
}
