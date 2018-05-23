package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class represent name and type constant.
 */
public class NameAndTypeInfo extends ConstantInfo {

    /**
     * Name and type tag.
     */
    private static final int TAG = 12;

    /**
     * Name index in constant pool.
     */
    private final int nameIndex;

    /**
     * Type index in constant pool.
     */
    private final int typeIndex;

    /**
     * Create new name and type constant info in {@link ConstantPool}.
     * @param nameIndex name index
     * @param typeIndex type index
     * @param index index in constant pool items
     */
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NameAndTypeInfo that = (NameAndTypeInfo) o;

        if (nameIndex != that.nameIndex) {
            return false;
        }

        return typeIndex == that.typeIndex;
    }

    @Override
    @SuppressWarnings("magicnumber")
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
