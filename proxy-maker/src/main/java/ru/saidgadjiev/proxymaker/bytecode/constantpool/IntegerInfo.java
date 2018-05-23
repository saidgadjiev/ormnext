package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class represent integer in constant pool.
 */
public class IntegerInfo extends ConstantInfo {

    /**
     * Integer constant tag.
     */
    private static final int TAG = 3;

    /**
     * Target value.
     */
    private int value;

    /**
     * Constructor create a new instance with {@code value}.
     * @param value target value
     * @param index index in constant pool
     */
    public IntegerInfo(int value, int index) {
        super(index);
        this.value = value;
    }

    @Override
    public int getTag() {
        return TAG;
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG);
        dataOutputStream.writeInt(value);
    }
}
