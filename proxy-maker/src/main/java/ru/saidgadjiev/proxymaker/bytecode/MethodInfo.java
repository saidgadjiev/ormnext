package ru.saidgadjiev.proxymaker.bytecode;

import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;

public class MethodInfo {

    public static final String NAME_INIT = "<init>";

    public static final String NAME_CLINIT = "<clinit>";

    private int accessFlags;

    private int nameIndex;

    private int descriptorIndex;

    private CodeAttribute codeAttribute;

    public MethodInfo(ConstantPool constantPool, String name, String descriptor) {
        this.nameIndex = constantPool.addUtf8Info(name);
        this.descriptorIndex = constantPool.addUtf8Info(descriptor);
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    public void setCodeAttribute(CodeAttribute codeAttribute) {
        this.codeAttribute = codeAttribute;
    }

    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeShort(accessFlags);
        outputStream.writeShort(nameIndex);
        outputStream.writeShort(descriptorIndex);

        if (codeAttribute == null) {
            outputStream.writeShort(0);
        } else {
            outputStream.writeShort(1);
            codeAttribute.write(outputStream);
        }
    }
}
