package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliases;

public class EntityInitializer {

    private Class<?> entityClass;

    private EntityAliases entityAliases;

    public EntityInitializer(EntityAliases entityAliases, Class<?> entityClass) {
        this.entityAliases = entityAliases;
        this.entityClass = entityClass;
    }

    public void startRead(DatabaseResults results) {

    }

    public void finishRead() {

    }
}
