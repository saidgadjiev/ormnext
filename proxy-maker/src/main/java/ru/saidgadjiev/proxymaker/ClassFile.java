package ru.saidgadjiev.proxymaker;

import ru.saidgadjiev.proxymaker.bytecode.FieldInfo;
import ru.saidgadjiev.proxymaker.bytecode.MethodInfo;
import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassFile {

    private static final int CAFEBABE = 0xCAFEBABE; //magic

    private static final int MINOR_JAVA_VERSION = 0; //JDK 1.3 or later

    private static final int MAJOR_JAVA_VERSION = 52; //JDK 1.8

    private final String superClassName;

    private final String className;

    private int accessFlags = AccessFlag.SUPER;

    private ConstantPool constantPool;

    private List<Integer> interfaces = new ArrayList<>();

    private List<FieldInfo> fieldInfos = new ArrayList<>();

    private List<MethodInfo> methodInfos = new ArrayList<>();

    public ClassFile(String className, String superClassName) {
        this.className = className;
        this.superClassName = superClassName;
        this.constantPool = new ConstantPool(className, superClassName);
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags |= accessFlags;
    }

    public void setInterfaces(Collection<String> interfaceNames) {
        interfaces.clear();
        for (String interfaceName: interfaceNames) {
            interfaces.add(constantPool.addClassInfo(interfaceName));
        }
    }

    public void addFieldInfo(FieldInfo fieldInfo) {
        fieldInfos.add(fieldInfo);
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }


    public void addMethodInfo(MethodInfo methodInfo) {
        methodInfos.add(methodInfo);
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public String getClassName() {
        return className;
    }

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
