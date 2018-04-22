package ru.saidgadjiev.orm.next.core.dao;

import java.sql.SQLException;
import java.util.List;

public interface ExpandedDao extends BaseDao {

    <T> List<T> list(CriteriaQuery<T> criteriaQuery) throws SQLException;
}
