package ru.saidgadjiev.orm.next.core.logger;

import org.junit.Assert;
import org.junit.Test;

public class LoggerFactoryTest {

    @Test
    public void getLogger() throws Exception {
        System.setProperty(LoggerFactory.LOG_CLASS_PROPERTY, "ru.saidgadjiev.orm.next.core.logger.TestLog");
        Log logTest = LoggerFactory.getLogger(Test.class);

        Assert.assertTrue(logTest instanceof TestLog);
        System.setProperty(LoggerFactory.LOG_CLASS_PROPERTY, "ru.saidgadjiev.orm.next.core.logger.Log4j");
        Log log4j = LoggerFactory.getLogger(Test.class);

        Assert.assertTrue(log4j instanceof Log4j);
    }
}