package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class MemberRefInfo extends ConstantInfo {

    private int classInfoIndex;

    private int nameAndTypeIndex;

    public MemberRefInfo(int classInfoIndex, int nameAndTypeIndex, int index) {
        super(index);
        this.classInfoIndex = classInfoIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberRefInfo that = (MemberRefInfo) o;

        if (classInfoIndex != that.classInfoIndex) return false;
        return nameAndTypeIndex == that.nameAndTypeIndex;
    }

    @Override
    public int hashCode() {
        return (classInfoIndex << 16) ^ nameAndTypeIndex;
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(getTag());
        dataOutputStream.writeShort(classInfoIndex);
        dataOutputStream.writeShort(nameAndTypeIndex);
    }
}
