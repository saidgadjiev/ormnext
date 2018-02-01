package ru.said.orm.next.core.logger;

public interface Log {

    void info(Object message);

    void debug(Object message);

    void error(Object message);
}
