package ru.said.orm.next.core.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4j implements Log {

    private Logger logger;

    @Override
    public void debug(Object message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

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

    public void info(Object message, Throwable t) {
        if (logger.isInfoEnabled()) {
            logger.info(message, t);
        }
    }

    public void warn(Object message) {
        if (logger.isEnabledFor(Level.WARN)) {
            logger.warn(message);
        }
    }

    public void warn(Object message, Throwable t) {
        if (logger.isEnabledFor(Level.WARN)) {
            logger.warn(message, t);
        }
    }
}
