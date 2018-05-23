package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base class for constant in constant pool such as class name, super class name, utf8 strings, ...
 */
public abstract class ConstantInfo {

    /**
     * Constant index in constant pool.
     */
    private int index;

    /**
     * Base constructor.
     * @param index index of constant from constant pool.
     */
    public ConstantInfo(int index) {
        this.index = index;
    }

    /**
     * Return tag of constant in constant pool.
     *
     * @return constant tag
     */
    public abstract int getTag();

    /**
     * Return index of constant in constant pool.
     *
     * @return constant {@link ConstantInfo#index}
     */
    public int getIndex() {
        return index;
    }

    /**
     * Write constant instruction to {@link DataOutputStream}.
     *
     * @param dataOutputStream dataoutputstream for write constant info in constant pool
     * @throws IOException throws when write {@link DataOutputStream}
     */
    public abstract void write(DataOutputStream dataOutputStream) throws IOException;
}
