package ru.saidgadjiev.proxymaker;

/**
 * A support class providing static methods and constants
 * for access modifiers such as public, private, ...
 */
public final class AccessFlag {

    /**
     * Utils class can't be instantiate.
     */
    private AccessFlag() { }

    /**
     * public access modifier.
     */
    public static final int PUBLIC = 0x0001;

    /**
     * super access modifier.
     */
    public static final int SUPER = 0x0020;

    /**
     * private access modifier.
     */
    public static final int PRIVATE = 0x0002;

    /**
     * static access modifier.
     */
    public static final int STATIC = 0x0008;
}
