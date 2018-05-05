package ru.saidgadjiev.proxymaker.bytecode;

import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;

public class FieldInfo {

    private int accessFlags;

    private int nameIndex;

    private int descriptorIndex;

    public FieldInfo(ConstantPool constantPool, String name, String descriptor) {
        this.nameIndex = constantPool.addUtf8Info(name);
        this.descriptorIndex = constantPool.addUtf8Info(descriptor);
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeShort(accessFlags);
        outputStream.writeShort(nameIndex);
        outputStream.writeShort(descriptorIndex);
        outputStream.writeShort(0);
    }
}
