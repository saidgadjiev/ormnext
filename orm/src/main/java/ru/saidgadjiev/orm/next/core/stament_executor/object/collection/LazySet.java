package ru.saidgadjiev.orm.next.core.stament_executor.object.collection;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by said on 09.03.2018.
 */
public class LazySet<T> extends AbstractLazyCollection<T> implements Set<T> {

    public LazySet(Supplier<List<T>> fetcher, Set<T> set) {
        super(fetcher, set);
    }
}
