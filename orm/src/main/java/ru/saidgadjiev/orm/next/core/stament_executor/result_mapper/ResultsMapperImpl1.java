package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import ru.saidgadjiev.orm.next.core.criteria.impl.Criteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.field.FetchType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignCollectionFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignColumnTypeFactory;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignColumnype;
import ru.saidgadjiev.orm.next.core.field.field_type.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stament_executor.GenericResults;
import ru.saidgadjiev.orm.next.core.stament_executor.object.collection.LazyList;
import ru.saidgadjiev.orm.next.core.stament_executor.object.collection.LazySet;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;
import ru.saidgadjiev.orm.next.core.table.persister.instatiator.Instantiator;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

public class ResultsMapperImpl1<T> implements ResultsMapper<T> {

    private Instantiator instantiator;

    public ResultsMapperImpl1(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {
       T object = (T) instantiator.instantiate();

       return object;
    }
}
