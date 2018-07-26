package ru.saidgadjiev.ormnext.core.loader.object;

import ru.saidgadjiev.ormnext.core.dao.Session;

/**
 * Created by said on 25.07.2018.
 */
public interface Lazy {

    void attach(Session session);
}
