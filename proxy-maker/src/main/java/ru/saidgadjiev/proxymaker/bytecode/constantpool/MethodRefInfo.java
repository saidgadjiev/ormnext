package ru.saidgadjiev.proxymaker.bytecode.constantpool;

/**
 * This class present method constant in constant pool.
 */
public class MethodRefInfo extends MemberRefInfo {

    /**
     * Method constant tag.
     */
    private static final int TAG = 10;

    /**
     * Create method info.
     * @param classInfoIndex method class index
     * @param nameAndTypeIndex method name and type
     * @param index method constant index in constant pool
     */
    public MethodRefInfo(int classInfoIndex, int nameAndTypeIndex, int index) {
        super(classInfoIndex, nameAndTypeIndex, index);
    }

    @Override
    public int getTag() {
        return TAG;
    }

}
