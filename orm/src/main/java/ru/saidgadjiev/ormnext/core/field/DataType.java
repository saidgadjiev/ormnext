package ru.saidgadjiev.ormnext.core.field;

public final class DataType {

    public static final int STRING = 0;

    public static final int INTEGER = 1;

    public static final int BOOLEAN = 2;

    public static final int DATE = 3;

    public static final int LONG = 4;

    public static final int FLOAT = 5;

    public static final int DOUBLE = 6;

    public static final int UNKNOWN = 7;

    public static int[] types() {
        return new int[] {STRING, INTEGER, BOOLEAN, DATE, LONG, FLOAT, DOUBLE, UNKNOWN};
    }
}
