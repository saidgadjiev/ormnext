package ru.said.orm.next.core.stament_executor;

import java.sql.SQLException;
import java.util.List;

public interface GenericResults<T> {

    List<T> getResults() throws SQLException;

    T getFirstResult() throws SQLException;
}
