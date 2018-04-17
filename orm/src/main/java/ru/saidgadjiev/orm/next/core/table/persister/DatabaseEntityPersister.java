package ru.saidgadjiev.orm.next.core.table.persister;

import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

public interface DatabaseEntityPersister {

    void initialize();

    DatabaseEntityMetadata<?> getMetadata();
}
