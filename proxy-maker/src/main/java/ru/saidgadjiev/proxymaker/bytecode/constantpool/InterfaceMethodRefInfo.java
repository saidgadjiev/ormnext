package ru.saidgadjiev.proxymaker.bytecode.constantpool;

public class InterfaceMethodRefInfo extends MemberRefInfo {

    private static final int TAG = 11;

    public InterfaceMethodRefInfo(int classInfoIndex, int nameAndTypeIndex, int index) {
        super(classInfoIndex, nameAndTypeIndex, index);
    }

    @Override
    public int getTag() {
        return TAG;
    }

}
