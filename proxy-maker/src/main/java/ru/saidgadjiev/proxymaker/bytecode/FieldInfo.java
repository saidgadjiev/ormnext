package ru.saidgadjiev.proxymaker.bytecode;

import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class represent field in bytecode.
 */
public class FieldInfo {

    /**
     * Access flag {@link ru.saidgadjiev.proxymaker.AccessFlag}.
     */
    private int accessFlags;

    /**
     * Field name index constant in constant pool.
     */
    private int nameIndex;

    /**
     * Field descriptor index in constant pool.
     */
    private int descriptorIndex;

    /**
     * Create new instance which represent field info in bytecode.
     * @param constantPool current constant pool {@link ConstantPool}
     * @param name field name
     * @param descriptor field descriptor
     */
    public FieldInfo(ConstantPool constantPool, String name, String descriptor) {
        this.nameIndex = constantPool.addUtf8Info(name);
        this.descriptorIndex = constantPool.addUtf8Info(descriptor);
    }

    /**
     * Set field access flags {@link ru.saidgadjiev.proxymaker.AccessFlag}.
     * @param accessFlags target access flags
     */
    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    /**
     * Write field info to {@link DataOutputStream}.
     * @param outputStream target outputstream
     * @throws IOException throws in {@link DataOutputStream}
     */
    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeShort(accessFlags);
        outputStream.writeShort(nameIndex);
        outputStream.writeShort(descriptorIndex);
        outputStream.writeShort(0);
    }
}
