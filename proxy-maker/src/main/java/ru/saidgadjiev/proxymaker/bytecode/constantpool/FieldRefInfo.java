package ru.saidgadjiev.proxymaker.bytecode.constantpool;

/**
 * This class represent constant for field.
 */
public class FieldRefInfo extends MemberRefInfo {

    /**
     * Field ref info constant start tag.
     */
    private static final int TAG = 9;

    /**
     * Constructor for field ref constant.
     *
     * @param classInfoIndex target class index
     * @param nameAndTypeIndex target name and type index
     * @param index index of this constant in constant pool items
     */
    public FieldRefInfo(int classInfoIndex, int nameAndTypeIndex, int index) {
        super(classInfoIndex, nameAndTypeIndex, index);
    }

    @Override
    public int getTag() {
        return TAG;
    }
}
