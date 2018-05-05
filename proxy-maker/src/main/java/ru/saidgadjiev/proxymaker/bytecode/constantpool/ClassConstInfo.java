package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClassConstInfo extends ConstantInfo {

    private static final int TAG = 7;

    private int classNameIndex;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassConstInfo that = (ClassConstInfo) o;

        return classNameIndex == that.classNameIndex;
    }

    @Override
    public int hashCode() {
        return classNameIndex;
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {   dataOutputStream.writeByte(TAG);
        dataOutputStream.writeShort(classNameIndex);
    }
}
