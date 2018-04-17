package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.dao.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SessionMangerBuilder {

    private Collection<Class<?>> entityClasses = new ArrayList<>();

    private ConnectionSource connectionSource;

    public SessionMangerBuilder addEntityClasses(Class<?> ... classes) {
        entityClasses.addAll(Arrays.asList(classes));

        return this;
    }

    public SessionMangerBuilder connectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;

        return this;
    }

    public SessionManager build() {
        MetaModel metaModel = new MetaModel();

        metaModel.initialize(entityClasses);

        return new SessionManagerImpl(connectionSource, metaModel);
    }


}
