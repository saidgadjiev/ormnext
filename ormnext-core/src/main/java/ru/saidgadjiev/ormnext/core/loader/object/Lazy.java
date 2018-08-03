package ru.saidgadjiev.ormnext.core.loader.object;

import ru.saidgadjiev.ormnext.core.dao.Session;

/**
 * Lazy interface.
 *
 * @author Said Gadjiev
 */
public interface Lazy {

    /**
     * Attach session to lazy object.
     *
     * @param session target session
     */
    void attach(Session session);

    /**
     * Return true if lazy initialized.
     *
     * @return true if lazy initialized
     */
    boolean isInitialized();

    /**
     * Set initialized false for lazy.
     */
    void setNonInitialized();
}
