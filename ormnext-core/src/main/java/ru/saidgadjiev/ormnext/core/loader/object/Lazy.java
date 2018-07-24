package ru.saidgadjiev.ormnext.core.loader.object;

import ru.saidgadjiev.ormnext.core.dao.Session;

import java.sql.SQLException;

/**
 * Created by said on 25.07.2018.
 */
public interface Lazy {

    void load(Session session) throws SQLException;
}
