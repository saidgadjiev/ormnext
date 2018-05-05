package ru.saidgadjiev.proxymaker.bytecode.constantpool;

public class FieldRefInfo extends MemberRefInfo {

    private static final int TAG = 9;

    public FieldRefInfo(int classInfoIndex, int nameAndTypeIndex, int index) {
        super(classInfoIndex, nameAndTypeIndex, index);
    }

    @Override
    public int getTag() {
        return TAG;
    }
}
