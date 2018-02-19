package ru.saidgadjiev.orm.next.core.stament_executor.object.operation;

import ru.saidgadjiev.orm.next.core.table.TableInfo;

/**
 * Created by said on 10.02.2018.
 */
public interface IObjectOperation<R> {

    <O> R execute(TableInfo<O> tableInfo, O object) throws Exception;
}
