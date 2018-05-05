package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import java.io.DataOutputStream;
import java.io.IOException;

public class Utf8Info extends ConstantInfo {

    private static final int TAG = 1;

    private final String utf8;

    public Utf8Info(String utf8, int index) {
        super(index);
        this.utf8 = utf8;
    }

    @Override
    public int getTag() {
        return TAG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Utf8Info utf8Info = (Utf8Info) o;

        return utf8.equals(utf8Info.utf8);
    }

    @Override
    public int hashCode() {
        return utf8.hashCode();
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG);
        dataOutputStream.writeUTF(utf8);
    }
}
