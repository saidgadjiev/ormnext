package ru.saidgadjiev.proxymaker.bytecode;

import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class represent method info in bytecode.
 */
public class MethodInfo {

    /**
     * Init.
     */
    public static final String NAME_INIT = "<init>";

    /**
     * Clinit.
     */
    public static final String NAME_CLINIT = "<clinit>";

    /**
     * Access flags.
     */
    private int accessFlags;

    /**
     * Method name index in constant pool.
     */
    private int nameIndex;

    /**
     * Method descriptor index in constant pool.
     */
    private int descriptorIndex;

    /**
     * Method code {@link CodeAttribute}.
     */
    private CodeAttribute codeAttribute;

    /**
     * Create new instance which represent method info in constant pool.
     * @param constantPool target constant pool
     * @param name method name
     * @param descriptor method descriptor
     */
    public MethodInfo(ConstantPool constantPool, String name, String descriptor) {
        this.nameIndex = constantPool.addUtf8Info(name);
        this.descriptorIndex = constantPool.addUtf8Info(descriptor);
    }

    /**
     * Provide method access flags {@link ru.saidgadjiev.proxymaker.AccessFlag}.
     * @param accessFlags target access flags.
     */
    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    /**
     * Method code.
     * @param codeAttribute target method code.
     */
    public void setCodeAttribute(CodeAttribute codeAttribute) {
        this.codeAttribute = codeAttribute;
    }

    /**
     * Write method info to {@link DataOutputStream}.
     * @param outputStream target outputstream
     * @throws IOException throws in {@link DataOutputStream}
     */
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
