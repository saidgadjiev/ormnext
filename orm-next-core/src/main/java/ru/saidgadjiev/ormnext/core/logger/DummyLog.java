package ru.saidgadjiev.ormnext.core.logger;

/**
 * Dummy log.
 */
public class DummyLog implements Log {

    @Override
    public void error(String message, Throwable t, Object ... args) {

    }

    @Override
    public void info(String message, Object ... args) {

    }

    @Override
    public void debug(String message, Object ... args) {

    }

    @Override
    public void debug(String message, Throwable t, Object ... args) {

    }

    @Override
    public void error(String message, Object ... args) {

    }

    @Override
    public void info(String message, Throwable t, Object ... args) {

    }

    @Override
    public void warn(String message, Object ... args) {

    }

    @Override
    public void warn(String message, Throwable t, Object ... args) {

    }
}
