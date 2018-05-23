package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base constant class for constants with name and type and class index.
 */
public abstract class MemberRefInfo extends ConstantInfo {

    /**
     * Class info index in constant pool.
     */
    private int classInfoIndex;

    /**
     * Name and type index in constant pool.
     */
    private int nameAndTypeIndex;

    /**
     * Base constructor.
     *
     * @param classInfoIndex target class index
     * @param nameAndTypeIndex target name and type index
     * @param index index of this constant in constant pool items
     */
    public MemberRefInfo(int classInfoIndex, int nameAndTypeIndex, int index) {
        super(index);
        this.classInfoIndex = classInfoIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MemberRefInfo that = (MemberRefInfo) o;

        if (classInfoIndex != that.classInfoIndex) {
            return false;
        }

        return nameAndTypeIndex == that.nameAndTypeIndex;
    }

    @Override
    @SuppressWarnings("magicnumber")
    public int hashCode() {
        return (classInfoIndex << 16) ^ nameAndTypeIndex;
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(getTag());
        dataOutputStream.writeShort(classInfoIndex);
        dataOutputStream.writeShort(nameAndTypeIndex);
    }
}
