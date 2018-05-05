package ru.saidgadjiev.proxymaker.bytecode.constantpool;

public class MethodRefInfo extends MemberRefInfo {

    private static final int TAG = 10;

    public MethodRefInfo(int classInfoIndex, int nameAndTypeIndex, int index) {
        super(classInfoIndex, nameAndTypeIndex, index);
    }

    @Override
    public int getTag() {
        return TAG;
    }

}
