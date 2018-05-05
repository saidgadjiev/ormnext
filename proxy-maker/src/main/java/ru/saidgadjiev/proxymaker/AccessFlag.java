package ru.saidgadjiev.proxymaker;

public class AccessFlag {

    private AccessFlag() {}

    public static final int PUBLIC = 0x0001;

    public static final int SUPER = 0x0020;

    public static final int PRIVATE = 0x0002;

    public static final int STATIC    = 0x0008;
}
