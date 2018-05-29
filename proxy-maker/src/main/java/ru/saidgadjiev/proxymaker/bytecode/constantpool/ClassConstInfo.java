package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class represent class constant in constant pool.
 */
public class ClassConstInfo extends ConstantInfo {

    /**
     * Bytecode tag.
     */
    private static final int TAG = 7;

    /**
     * Class name index in constant pool.
     */
    private int classNameIndex;

    /**
     * Constructor for create new instance.
     *
     * @param classNameIndex classname index
     * @param index index of constant from constant pool
     */
    public ClassConstInfo(int classNameIndex, int index) {
        super(index);
        this.classNameIndex = classNameIndex;
    }

    @Override
    public int getTag() {
        return TAG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClassConstInfo that = (ClassConstInfo) o;

        return classNameIndex == that.classNameIndex;
    }

    @Override
    public int hashCode() {
        return classNameIndex;
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG);
        dataOutputStream.writeShort(classNameIndex);
    }
}
