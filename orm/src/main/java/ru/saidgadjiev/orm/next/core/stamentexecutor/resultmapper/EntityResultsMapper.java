package ru.saidgadjiev.orm.next.core.stamentexecutor.resultmapper;

import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.lang.reflect.Constructor;

public class EntityResultsMapper implements ResultsMapper {

    private DatabaseEntityMetadata entityMetadata;

    public EntityResultsMapper(DatabaseEntityMetadata entityMetadata) {
        this.entityMetadata = entityMetadata;
    }

    @Override
    public Object mapResults(DatabaseResults results) throws Exception {
        Object instance = newObject(entityMetadata.getConstructor());


        return null;
    }

    private void readRow(Object instance) {

    }

    private Object newObject(Constructor<?> constructor) throws Exception {
        Object object;

        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
            object = constructor.newInstance();
            constructor.setAccessible(false);
        } else {
            object = constructor.newInstance();
        }

        return object;
    }
}
