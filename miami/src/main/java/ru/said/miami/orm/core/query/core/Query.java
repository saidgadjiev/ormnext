package ru.said.miami.orm.core.query.core;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Общий интерфейс для всех типов запросов
 */
public interface Query<R> {

    /**
     * Выполняет сгенерированный запрос и возвращает результат выполнения
     * @return результат выполнения запроса
     * @throws SQLException если произошла ошибка выполнения запроса
     * @param connection
     */
    R execute(Connection connection) throws SQLException;
}
