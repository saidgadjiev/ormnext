package ru.saidgadjiev.ormnext.core.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4j implements Log {

    private Logger logger;

    public Log4j(String className) {
        logger = Logger.getLogger(className);
    }

    @Override
    public void debug(Object message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    @Override
    public void debug(Object message, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, t);
        }
    }

    @Override
    public void error(Object message) {
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error(message);
        }
    }

    @Override
    public void error(Object message, Throwable t) {
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error(message, t);
        }
    }

    @Override
    public void info(Object message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    @Override
    public void info(Object message, Throwable t) {
        if (logger.isInfoEnabled()) {
            logger.info(message, t);
        }
    }

    @Override
    public void warn(Object message) {
        if (logger.isEnabledFor(Level.WARN)) {
            logger.warn(message);
        }
    }

    @Override
    public void warn(Object message, Throwable t) {
        if (logger.isEnabledFor(Level.WARN)) {
            logger.warn(message, t);
        }
    }
}
