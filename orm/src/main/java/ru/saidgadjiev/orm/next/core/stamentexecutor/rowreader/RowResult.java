package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

public class RowResult<T> {

    private final T result;

    private final boolean isNew;

    public RowResult(T result, boolean isNew) {
        this.result = result;
        this.isNew = isNew;
    }

    public T getResult() {
        return result;
    }

    public boolean isNew() {
        return isNew;
    }
}
