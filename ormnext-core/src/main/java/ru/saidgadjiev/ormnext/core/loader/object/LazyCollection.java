package ru.saidgadjiev.ormnext.core.loader.object;

import ru.saidgadjiev.ormnext.core.dao.Session;

/**
 * Created by said on 25.07.2018.
 */
public interface LazyCollection extends Lazy {

    void loadSize(Session session);
}
