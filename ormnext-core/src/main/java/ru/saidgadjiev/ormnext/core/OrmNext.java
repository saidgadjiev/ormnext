package ru.saidgadjiev.ormnext.core;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.loader.object.Lazy;

/**
 * Created by said on 25.07.2018.
 */
public final class OrmNext {

    private OrmNext() { }

    public static void attach(Session session, Object lazy) {
        if (lazy instanceof Lazy) {
            ((Lazy) lazy).attach(session);
        }
    }
}
