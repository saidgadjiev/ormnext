package ru.saidgadjiev.ormnext.core;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.loader.object.Lazy;

/**
 * OrmNext utils.
 *
 * @author Said Gadjiev
 */
public final class OrmNext {

    /**
     * Can't be instantiated.
     */
    private OrmNext() { }

    /**
     * Attach new session to lazy reference.
     *
     * @param session target session
     * @param lazy lazy reference object
     */
    public static void attach(Session session, Object lazy) {
        if (lazy instanceof Lazy) {
            ((Lazy) lazy).attach(session);
        }
    }
}
