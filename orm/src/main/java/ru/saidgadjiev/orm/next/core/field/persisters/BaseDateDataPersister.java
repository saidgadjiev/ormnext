package ru.saidgadjiev.orm.next.core.field.persisters;

import java.util.Date;

/**
 * Created by said on 03.02.2018.
 */
public abstract class BaseDateDataPersister extends BaseDataPersister {

    public BaseDateDataPersister() {
        super(new Class[]{Date.class});
    }

    @Override
    public Class<?>[] getAssociatedClasses() {
        return classes;
    }

}
