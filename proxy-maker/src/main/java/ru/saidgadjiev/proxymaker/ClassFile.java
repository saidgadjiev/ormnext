package ru.saidgadjiev.proxymaker;

import ru.saidgadjiev.proxymaker.bytecode.FieldInfo;
import ru.saidgadjiev.proxymaker.bytecode.MethodInfo;
import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represent all class bytecode.
 */
public class ClassFile {

    /**
     * COFEBABE magic.
     */
    private static final int CAFEBABE = 0xCAFEBABE; //magic

    /**
     * JDK minor.
     */
    private static final int MINOR_JAVA_VERSION = 0; //JDK 1.3 or later

    /**
     * JDK major.
     */
    private static final int MAJOR_JAVA_VERSION = 52; //JDK 1.8

    /**
     * Fully qualified target proxy class name.
     */
    private final String className;

    /**
     * Access flags.
     */
    private int accessFlags = AccessFlag.SUPER;

    /**
     * Current bytecode constant pool.
     */
    private ConstantPool constantPool;

    /**
     * Target proxy class interfaces.
     */
    private List<Integer> interfaces = new ArrayList<>();

    /**
     * Target proxy class fields.
     */
    private List<FieldInfo> fieldInfos = new ArrayList<>();

    /**
     * Target proxy class methos.
     */
    private List<MethodInfo> methodInfos = new ArrayList<>();

    /**
     * Create new instance with requested {@code className} and {@code superClassName}.
     * @param className target proxy class name
     * @param superClassName target super class name
     */
    public ClassFile(String className, String superClassName) {
        this.className = className;
        this.constantPool = new ConstantPool(className, superClassName);
    }

    /**
     * Provide class access flags.
     * @param accessFlags target access flags
     */
    public void setAccessFlags(int accessFlags) {
        this.accessFlags |= accessFlags;
    }

    /**
     * Provide proxy class interfaces.
     * @param interfaceNames fully qualified interface class names.
     */
    public void setInterfaces(Collection<String> interfaceNames) {
        interfaces.clear();
        for (String interfaceName: interfaceNames) {
            interfaces.add(constantPool.addClassInfo(interfaceName));
        }
    }

    /**
     * Field info in bytecode this proxy class.
     * @param fieldInfo target field info
     */
    public void addFieldInfo(FieldInfo fieldInfo) {
        fieldInfos.add(fieldInfo);
    }

    /**
     * Current bytecode constant pool.
     * @return current constant pool
     */
    public ConstantPool getConstantPool() {
        return constantPool;
    }

    /**
     * Method info in bytecode this proxy class.
     * @param methodInfo target method info
     */
    public void addMethodInfo(MethodInfo methodInfo) {
        methodInfos.add(methodInfo);
    }

    /**
     * This proxy class fully qualified name.
     * @return proxy class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Wrtie class bytecode to {@link DataOutputStream}.
     * @param outputStream target outputstream
     * @throws IOException throws in {@link DataOutputStream}
     */
    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(CAFEBABE);
        outputStream.writeShort(MINOR_JAVA_VERSION);
        outputStream.writeShort(MAJOR_JAVA_VERSION);
        constantPool.write(outputStream);
        outputStream.writeShort(accessFlags);
        outputStream.writeShort(constantPool.getThisClassIndex());
        outputStream.writeShort(constantPool.getSuperClassIndex());
        outputStream.writeShort(interfaces.size());

        for (int interfaceIndex: interfaces) {
            outputStream.writeShort(interfaceIndex);
        }
        outputStream.writeShort(fieldInfos.size());

        for (FieldInfo fieldInfo: fieldInfos) {
            fieldInfo.write(outputStream);
        }

        outputStream.writeShort(methodInfos.size());

        for (MethodInfo methodInfo: methodInfos) {
            methodInfo.write(outputStream);
        }
        outputStream.writeShort(0);
    }

}
