package ru.saidgadjiev.orm.next.core.stament_executor.object.operation;

/**
 * Created by said on 10.02.2018.
 */
public interface IObjectOperation<R, O> {

    R execute(O object) throws Exception;
}
