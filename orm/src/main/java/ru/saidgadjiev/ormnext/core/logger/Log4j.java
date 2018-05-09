package ru.saidgadjiev.ormnext.core.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4j implements Log {

    private Logger logger;

    public Log4j(String className) {
        logger = Logger.getLogger(className);
    }

    @Override
    public void debug(String message, Object ... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(message, args));
        }
    }

    @Override
    public void debug(String message, Throwable t, Object ... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(message, args), t);
        }
    }

    @Override
    public void error(String message, Object ... args) {
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error(String.format(message, args));
        }
    }

    @Override
    public void error(String message, Throwable t, Object ... args) {
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error(String.format(message, args), t);
        }
    }

    @Override
    public void info(String message, Object ... args) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format(message, args));
        }
    }

    @Override
    public void info(String message, Throwable t, Object ... args) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format(message, args), t);
        }
    }

    @Override
    public void warn(String message, Object ... args) {
        if (logger.isEnabledFor(Level.WARN)) {
            logger.warn(String.format(message, args));
        }
    }

    @Override
    public void warn(String message, Throwable t, Object ... args) {
        if (logger.isEnabledFor(Level.WARN)) {
            logger.warn(String.format(message, args), t);
        }
    }
}
