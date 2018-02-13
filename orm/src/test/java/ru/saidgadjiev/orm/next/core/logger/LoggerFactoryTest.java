package ru.saidgadjiev.orm.next.core.logger;

import org.junit.Assert;
import org.junit.Test;

public class LoggerFactoryTest {

    @Test
    public void getLogger() throws Exception {
        Log dummyLog = LoggerFactory.getLogger(Test.class);

        Assert.assertTrue(dummyLog instanceof DummyLog);
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        Log log4j = LoggerFactory.getLogger(Test.class);

        Assert.assertTrue(log4j instanceof Log4j);
        System.setProperty(LoggerFactory.LOG_CLASS_PROPERTY, "ru.saidgadjiev.orm.next.core.logger.TestLog");
        Log logTest = LoggerFactory.getLogger(Test.class);

        Assert.assertTrue(logTest instanceof TestLog);
    }
}