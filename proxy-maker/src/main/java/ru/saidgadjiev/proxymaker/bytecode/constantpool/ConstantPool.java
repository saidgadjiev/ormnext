package ru.saidgadjiev.proxymaker.bytecode.constantpool;

import ru.saidgadjiev.proxymaker.bytecode.ByteCodeUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represent constant pool instruction in bytecode.
 */
public class ConstantPool {

    /**
     * Target class name index.
     */
    private int thisClassIndex;

    /**
     * Super class name index.
     */
    private int superClassIndex;

    /**
     * Constant count.
     */
    private int itemsCount = 1;

    /**
     * Constants.
     */
    private Map<ConstantInfo, Integer> constantInfoIndexMap = new LinkedHashMap<>();

    /**
     * Constructor create new instance with request target class name and super class name.
     *
     * @param className target class name
     * @param superClassName super class name
     */
    public ConstantPool(String className, String superClassName) {
        this.thisClassIndex = addClassInfo(className);
        this.superClassIndex = addClassInfo(superClassName);
    }

    /**
     * Add class info to constant pool.
     *
     * @param className target class
     * @return index of class in constant pool
     */
    public int addClassInfo(String className) {
        int classNameIndex = addUtf8Info(ByteCodeUtils.toJvmName(className));

        return addConstantInfo(new ClassConstInfo(classNameIndex, itemsCount));
    }

    /**
     * Add string info to constant pool.
     *
     * @param str target string
     * @return index of str in constant pool
     */
    public int addStringInfo(String str) {
        int strIndex = addUtf8Info(str);

        return addConstantInfo(new StringInfo(strIndex, itemsCount));
    }

    /**
     * Add integer value info to constant pool.
     *
     * @param value target value
     * @return index of value in constant pool
     */
    public int addIntegerInfo(int value) {
        return addConstantInfo(new IntegerInfo(value, itemsCount));
    }

    /**
     * Add method ref info to constant pool.
     *
     * @param classInfoIndex class info index in constant pool
     * @param methodName method name
     * @param descriptor method descriptor
     * @return method ref info index in constant pool
     */
    public int addMethodRefInfo(int classInfoIndex, String methodName, String descriptor) {
        int nameAndTypeInfo = addNameAndTypeInfo(methodName, descriptor);

        return addConstantInfo(new MethodRefInfo(classInfoIndex, nameAndTypeInfo, itemsCount));
    }

    /**
     * Add interface method ref info to constant pool.
     *
     * @param classInfoIndex class info index in constant pool
     * @param methodName method name
     * @param descriptor method descriptor
     * @return method ref info index in constant pool
     */
    public int addInterfaceMethodRefInfo(int classInfoIndex, String methodName, String descriptor) {
        int nameAndTypeInfo = addNameAndTypeInfo(methodName, descriptor);

        return addConstantInfo(new InterfaceMethodRefInfo(classInfoIndex, nameAndTypeInfo, itemsCount));
    }

    /**
     * Add utf8 string info to constant pool.
     *
     * @param utf8 target utf8
     * @return method ref info index in constant pool
     */
    public int addUtf8Info(String utf8) {
        return addConstantInfo(new Utf8Info(utf8, itemsCount));
    }

    /**
     * Add field ref info to constant pool.
     *
     * @param classInfoIndex class info index in constant pool
     * @param fieldName field name
     * @param descriptor field descriptor
     * @return field ref info index in constant pool
     */
    public int addFieldRefInfo(int classInfoIndex, String fieldName, String descriptor) {
        int nameAndTypeInfo = addNameAndTypeInfo(fieldName, descriptor);

        return addConstantInfo(new FieldRefInfo(classInfoIndex, nameAndTypeInfo, itemsCount));
    }

    /**
     * Add name and descriptor info to constant pool.
     *
     * @param name name
     * @param descriptor descriptor
     * @return name and descriptor info index in constant pool
     */
    private int addNameAndTypeInfo(String name, String descriptor) {
        return addConstantInfo(new NameAndTypeInfo(addUtf8Info(name), addUtf8Info(descriptor), itemsCount));
    }

    /**
     * Add constant to constant pool.
     *
     * @param constantInfo constant {@link ConstantInfo}
     * @return constant index in constant pool
     */
    private int addConstantInfo(ConstantInfo constantInfo) {
        if (constantInfoIndexMap.containsKey(constantInfo)) {
            return constantInfoIndexMap.get(constantInfo);
        }

        constantInfoIndexMap.put(constantInfo, constantInfo.getIndex());

        return itemsCount++;
    }

    /**
     * Return this class index in constant pool.
     *
     * @return this class index
     */
    public int getThisClassIndex() {
        return thisClassIndex;
    }

    /**
     * Return super class index in constant pool.
     *
     * @return super class index
     */
    public int getSuperClassIndex() {
        return superClassIndex;
    }

    /**
     * Write constant pool to {@link DataOutputStream}.
     *
     * @param outputStream target outputStream
     * @throws IOException thrown by {@link DataOutputStream}
     */
    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeShort(itemsCount);

        for (ConstantInfo info: constantInfoIndexMap.keySet()) {
            info.write(outputStream);
        }
    }

}
