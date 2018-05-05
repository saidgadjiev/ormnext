package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import ru.saidgadjiev.proxymaker.bytecode.common.ByteCodeUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class ConstantPool {

    private int thisClassIndex;

    private int superClassIndex;

    private int itemsCount = 1;

    private Map<ConstantInfo, Integer> constantInfoIndexMap = new LinkedHashMap<>();

    public ConstantPool(String className, String superClassName) {
        this.thisClassIndex = addClassInfo(className);
        this.superClassIndex = addClassInfo(superClassName);
    }

    public int addClassInfo(String className) {
        int classNameIndex = addUtf8Info(ByteCodeUtils.toJvmName(className));

        return addConstantInfo(new ClassConstInfo(classNameIndex, itemsCount));
    }

    public int addStringInfo(String str) {
        int strIndex = addUtf8Info(str);

        return addConstantInfo(new StringInfo(strIndex, itemsCount));
    }

    public int addMethodRefInfo(int classInfoIndex, String methodName, String descriptor) {
        int nameAndTypeInfo = addNameAndTypeInfo(methodName, descriptor);

        return addConstantInfo(new MethodRefInfo(classInfoIndex, nameAndTypeInfo, itemsCount));
    }

    public int addInterfaceMethodRefInfo(int classInfoIndex, String methodName, String descriptor) {
        int nameAndTypeInfo = addNameAndTypeInfo(methodName, descriptor);

        return addConstantInfo(new InterfaceMethodRefInfo(classInfoIndex, nameAndTypeInfo, itemsCount));
    }

    public int addUtf8Info(String utf8) {
        return addConstantInfo(new Utf8Info(utf8, itemsCount));
    }

    public int addFieldRefInfo(int classInfo, String name, String descriptor) {
        int nameAndTypeInfo = addNameAndTypeInfo(name, descriptor);

        return addConstantInfo(new FieldRefInfo(classInfo, nameAndTypeInfo, itemsCount));
    }

    private int addNameAndTypeInfo(String name, String descriptor) {
        return addConstantInfo(new NameAndTypeInfo(addUtf8Info(name), addUtf8Info(descriptor), itemsCount));
    }

    private int addConstantInfo(ConstantInfo constantInfo) {
        if (constantInfoIndexMap.containsKey(constantInfo)) {
            return constantInfoIndexMap.get(constantInfo);
        }

        constantInfoIndexMap.put(constantInfo, constantInfo.getIndex());

        return itemsCount++;
    }

    public int getThisClassIndex() {
        return thisClassIndex;
    }

    public int getSuperClassIndex() {
        return superClassIndex;
    }

    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeShort(itemsCount);

        for (ConstantInfo info: constantInfoIndexMap.keySet()) {
            info.write(outputStream);
        }
    }

    private class CachedInfo {

        private int classInfoIndex;

        private int classNameIndex;

        public CachedInfo(int classInfoIndex, int classNameIndex) {
            this.classInfoIndex = classInfoIndex;
            this.classNameIndex = classNameIndex;
        }
    }

}
