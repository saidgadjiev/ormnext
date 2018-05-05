package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ConstantInfo {

    private int index;

    public ConstantInfo(int index) {
        this.index = index;
    }

    public abstract int getTag();

    public int getIndex() {
        return index;
    }

    public abstract void write(DataOutputStream dataOutputStream) throws IOException;
}
