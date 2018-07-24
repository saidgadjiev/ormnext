package ru.saidgadjiev.ormnext.core;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.loader.object.Lazy;
import ru.saidgadjiev.ormnext.core.loader.object.LazyCollection;

import java.sql.SQLException;

/**
 * Created by said on 25.07.2018.
 */
public final class OrmNext {

    private OrmNext() { }

    public static void load(Session session, Object lazy) throws SQLException {
        if (lazy instanceof Lazy) {
            ((Lazy) lazy).load(session);
        }
    }

    public static void loadSize(Session session, Object lazy) {
        if (lazy instanceof LazyCollection) {
            ((LazyCollection) lazy).loadSize(session);
        }
    }
}
