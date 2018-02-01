package ru.said.orm.next.core.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class Log4j implements Log {

    private Logger logger;

    @Override
    public void debug(Object message) {
        logger.debug(message);
    }

    public void debug(Object message, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, t);
        }
    }

    @Override
    public void error(Object message) {
        if (logger.isEnabledFor(Priority.ERROR)) {
            logger.error(message);
        }
    }

    public void error(Object message, Throwable t) {
        if (logger.isEnabledFor(Priority.ERROR)) {
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
        logger.info(message, t);
    }

    public void warn(Object message) {
        logger.warn(message);
    }

    public void warn(Object message, Throwable t) {
        logger.warn(message, t);
    }
}
