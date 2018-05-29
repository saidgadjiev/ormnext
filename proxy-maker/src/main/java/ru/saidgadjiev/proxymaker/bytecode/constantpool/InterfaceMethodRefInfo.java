package ru.saidgadjiev.proxymaker.bytecode.constantpool;

/**
 * This class represent constant for interface method.
 */
public class InterfaceMethodRefInfo extends MemberRefInfo {

    /**
     * Interface method ref info constant start tag.
     */
    private static final int TAG = 11;

    /**
     * Constructor for interface method ref constant.
     *
     * @param classInfoIndex target class index
     * @param nameAndTypeIndex target name and type index
     * @param index index of this constant in constant pool items
     */
    public InterfaceMethodRefInfo(int classInfoIndex, int nameAndTypeIndex, int index) {
        super(classInfoIndex, nameAndTypeIndex, index);
    }

    @Override
    public int getTag() {
        return TAG;
    }

}
