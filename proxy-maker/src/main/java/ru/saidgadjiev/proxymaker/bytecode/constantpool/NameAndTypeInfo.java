package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

public class NameAndTypeInfo extends ConstantInfo {

    private static final int TAG = 12;

    private final int nameIndex;

    private final int typeIndex;

    public NameAndTypeInfo(int nameIndex, int typeIndex, int index) {
        super(index);
        this.nameIndex = nameIndex;
        this.typeIndex = typeIndex;
    }

    @Override
    public int getTag() {
        return TAG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameAndTypeInfo that = (NameAndTypeInfo) o;

        if (nameIndex != that.nameIndex) return false;
        return typeIndex == that.typeIndex;
    }

    @Override
    public int hashCode() {
        return (nameIndex << 16) ^ typeIndex;
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG);
        dataOutputStream.writeShort(nameIndex);
        dataOutputStream.writeShort(typeIndex);
    }
}
