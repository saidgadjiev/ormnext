package ru.said.miami.orm.core.query.core.object_builder;

import ru.said.miami.orm.core.query.core.IMiamiData;

/**
 * Created by said on 14.10.17.
 */
public abstract class ObjectPartBuilder {

    private ObjectPartBuilder next;

    public ObjectPartBuilder linkWith(ObjectPartBuilder next) {
        this.next = next;

        return next;
    }

    public abstract boolean check(IMiamiData data, Object object) throws Exception;

    protected boolean checkNext(IMiamiData data, Object object) throws Exception {
        if (next == null) {
            return true;
        }

        return next.check(data, object);
    }
}
