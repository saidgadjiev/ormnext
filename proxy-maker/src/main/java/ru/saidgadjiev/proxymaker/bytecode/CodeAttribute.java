package ru.saidgadjiev.proxymaker.bytecode;

import ru.saidgadjiev.proxymaker.bytecode.common.ByteCodeUtils;
import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeAttribute {

    private static final String TAG = "Code";

    private final ConstantPool constantPool;

    private int maxStackSize;

    private int maxLocalVars;

    private int stackDepth;

    private int codeTagIndex;

    private List<Byte> code = new ArrayList<>();

    public CodeAttribute(ConstantPool constantPool) {
        this(constantPool, 0, 0);
    }

    public CodeAttribute(ConstantPool constantPool, int maxStackSize, int maxLocalVars) {
        this.constantPool = constantPool;
        this.maxStackSize = maxStackSize;
        this.maxLocalVars = maxLocalVars;
        this.codeTagIndex = constantPool.addUtf8Info(TAG);
    }

    public void addAload(int n) {
        if (n < 4) {
            switch (n) {
                case 0:
                    addOpcode(Opcode.ALOAD_0);
                    break;
                case 1:
                    addOpcode(Opcode.ALOAD_1);
                    break;
                case 2:
                    addOpcode(Opcode.ALOAD_2);
                    break;
                case 3:
                    addOpcode(Opcode.ALOAD_3);
                    break;
            }
        } else if (n < 0x100) {
            addOpcode(Opcode.ALOAD);
            add(n);
        } else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.ALOAD);
            addIndex(n);
        }
    }

    public void addNew(String classname) {
        addOpcode(Opcode.NEW);
        addIndex(constantPool.addClassInfo(classname));
    }

    public void addPutStatic(String classname, String fieldName, String desc) {
        add(Opcode.PUT_STATIC.getOpcode());
        int ci = constantPool.addClassInfo(classname);

        addIndex(constantPool.addFieldRefInfo(ci, fieldName, desc));
        growStack(-ByteCodeUtils.dataSize(desc));
    }

    public void addAnewarray(String classname) {
        addOpcode(Opcode.ANEWARRAY);
        addIndex(constantPool.addClassInfo(classname));
    }

    public void addGetstatic(String clasName, String fieldName, String desc) {
        add(Opcode.GET_STATIC.getOpcode());
        int ci = constantPool.addClassInfo(clasName);

        addIndex(constantPool.addFieldRefInfo(ci, fieldName, desc));
        growStack(ByteCodeUtils.dataSize(desc));
    }

    public void addPutField(String className, String name, String descriptor) {
        add(Opcode.PUT_FIELD.getOpcode());

        int classInfoIndex = constantPool.addClassInfo(className);
        int fieldRefInfoIndex = constantPool.addFieldRefInfo(classInfoIndex, name, descriptor);

        addIndex(fieldRefInfoIndex);
        growStack(-1 - ByteCodeUtils.dataSize(descriptor));
    }

    public void addOpcode(Opcode opcode) {
        add(opcode.getOpcode());
        growStack(opcode.getStackDepth());
    }

    public void addAstore(int n) {
        switch (n) {
            case 0:
                addOpcode(Opcode.ASTORE_0);
                break;
            case 1:
                addOpcode(Opcode.ASTORE_1);
                break;
        }
    }

    public void addCheckCast(String className) {
        addOpcode(Opcode.CHECK_CAST);
        addIndex(constantPool.addClassInfo(className));
    }

    public void addLdc(String value) {
        int valueInfoIndex = constantPool.addStringInfo(value);

        addOpcode(Opcode.LDC);
        add(valueInfoIndex);
    }

    public void addGetField(String className, String fieldName, String fieldType) {
        add(Opcode.GET_FIELD.getOpcode());
        int ci = constantPool.addClassInfo(className);
        addIndex(constantPool.addFieldRefInfo(ci, fieldName, fieldType));
        growStack(ByteCodeUtils.dataSize(fieldType) - 1);
    }

    public void addInvokeStatic(String className, String methodName, String descriptor) {
        int classInfo = constantPool.addClassInfo(className);

        add(Opcode.INVOKE_STATIC.getOpcode());
        addIndex(constantPool.addMethodRefInfo(classInfo, methodName, descriptor));
        growStack(ByteCodeUtils.dataSize(descriptor));
    }

    public void addInvokeSpecial(String clazz, String name, String desc) {
        add(Opcode.INVOKE_SPECIAL.getOpcode());
        int index = constantPool.addMethodRefInfo(constantPool.addClassInfo(clazz), name, desc);

        addIndex(index);
        growStack(ByteCodeUtils.dataSize(desc) - 1);
    }

    public void addInvokeInterface(String classname, String methodName,
                                   String desc, int count) {
        add(Opcode.INVOKE_INTERFACE.getOpcode());
        addIndex(constantPool.addInterfaceMethodRefInfo(constantPool.addClassInfo(classname), methodName, desc));
        add(count);
        add(0);
        growStack(ByteCodeUtils.dataSize(desc) - 1);
    }

    private void addIndex(int index) {
        add(index >> 8);
        add(index);
    }

    private void growStack(int diff) {
        stackDepth += diff;
        if (stackDepth > maxStackSize) {
            maxStackSize = stackDepth;
        }
    }

    private void add(int code) {
        this.code.add((byte) code);
    }

    public void addIconst(int n) {
        if (n < 6 && -2 < n) {
            switch (n) {
                case 0:
                    addOpcode(Opcode.ICONST_0);
                    break;
                case 1:
                    addOpcode(Opcode.ICONST_1);
                    break;
                case 2:
                    addOpcode(Opcode.ICONST_2);
                    break;
                case 3:
                    addOpcode(Opcode.ICONST_3);
                    break;
                case 4:
                    addOpcode(Opcode.ICONST_4);
                    break;
                case 5:
                    addOpcode(Opcode.ICONST_5);
                    break;
            }
        } else if (n <= 127 && -128 <= n) {
            addOpcode(Opcode.BI_PUSH);
            add(n);
        }
    }

    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeShort(codeTagIndex);

        outputStream.writeInt(12 + code.size()); //code attribute lenght
        outputStream.writeShort(maxStackSize); //max stack size
        outputStream.writeShort(maxLocalVars); //max_local_var size
        outputStream.writeInt(code.size()); //size of code

        outputStream.write(toByteArray(code)); //machine instructions

        outputStream.writeShort(0); //exceptions size
        outputStream.writeShort(0); //attribute size
    }

    private byte[] toByteArray(List<Byte> list) {
        byte[] array = new byte[list.size()];
        AtomicInteger index = new AtomicInteger();

        list.forEach(aByte -> array[index.getAndIncrement()] = aByte);

        return array;
    }

    public void addInvokeVirtual(String className, String methodName, String desc) {
        add(Opcode.INVOKE_VIRTUAL.getOpcode());
        addIndex(constantPool.addMethodRefInfo(constantPool.addClassInfo(className), methodName, desc));
        growStack(ByteCodeUtils.dataSize(desc) - 1);
    }

    public void addLload(int n) {
        if (n < 4)
            switch (n) {
                case 0:
                    addOpcode(Opcode.LLOAD_0);
                    break;
                case 1:
                    addOpcode(Opcode.LLOAD_1);
                    break;
                case 2:
                    addOpcode(Opcode.LLOAD_2);
                    break;
                case 3:
                    addOpcode(Opcode.LLOAD_3);
                    break;
            }
        else if (n < 0x100) {
            addOpcode(Opcode.LLOAD);
            add(n);
        }
        else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.LLOAD);
            addIndex(n);
        }
    }

    public void addFload(int n) {
        if (n < 4)
            switch (n) {
                case 0:
                    addOpcode(Opcode.FLOAD_0);
                    break;
                case 1:
                    addOpcode(Opcode.FLOAD_1);
                    break;
                case 2:
                    addOpcode(Opcode.FLOAD_2);
                    break;
                case 3:
                    addOpcode(Opcode.FLOAD_3);
                    break;
            }
        else if (n < 0x100) {
            addOpcode(Opcode.FLOAD);
            add(n);
        }
        else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.FLOAD);
            addIndex(n);
        }
    }

    public void addDload(int n) {
        if (n < 4)
            switch (n) {
                case 0:
                    addOpcode(Opcode.DLOAD_0);
                    break;
                case 1:
                    addOpcode(Opcode.DLOAD_1);
                    break;
                case 2:
                    addOpcode(Opcode.DLOAD_2);
                    break;
                case 3:
                    addOpcode(Opcode.DLOAD_3);
                    break;
            }
        else if (n < 0x100) {
            addOpcode(Opcode.DLOAD);           // dload
            add(n);
        }
        else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.DLOAD);
            addIndex(n);
        }
    }

    public void addIload(int n) {
        if (n < 4)
            switch (n) {
                case 0:
                    addOpcode(Opcode.ILOAD_0);
                    break;
                case 1:
                    addOpcode(Opcode.ILOAD_1);
                    break;
                case 2:
                    addOpcode(Opcode.ILOAD_2);
                    break;
                case 3:
                    addOpcode(Opcode.ILOAD_3);
                    break;
            }
        else if (n < 0x100) {
            addOpcode(Opcode.ILOAD);           // iload
            add(n);
        }
        else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.ILOAD);
            addIndex(n);
        }
    }

    public enum Opcode {

        ALOAD(25, 1),

        ALOAD_0(42, 1),

        ALOAD_1(43, 1),

        ALOAD_2(44, 1),

        ALOAD_3(45, 1),

        FLOAD(23, 1),

        FLOAD_0(34, 1),

        FLOAD_1(35, 1),

        FLOAD_2(36, 1),

        FLOAD_3(37, 1),

        LLOAD(22, 2),

        LLOAD_0(30, 2),

        LLOAD_1(31, 2),

        LLOAD_2(32, 2),

        LLOAD_3(33, 2),

        DLOAD(24, 2),

        DLOAD_0(38, 2),

        DLOAD_1(39, 2),

        DLOAD_2(40, 2),

        DLOAD_3(41, 2),

        ILOAD(21, 2),

        ILOAD_0(26, 2),

        ILOAD_1(27, 2),

        ILOAD_2(28, 2),

        ILOAD_3(29, 2),

        ASTORE_0(75, -1),

        ASTORE_1(76, -1),

        ICONST_0(3, 1),

        ICONST_1(4, 1),

        ICONST_2(5, 1),

        ICONST_3(6, 1),

        ICONST_4(7, 1),

        ICONST_5(8, 1),

        AASTORE(83, -3),

        BI_PUSH(16, 1),

        NEW(187, 1),

        ANEWARRAY(189, 0),

        DUP(89, 1),

        INVOKE_SPECIAL(183, 0),

        INVOKE_INTERFACE(185, 0),

        INVOKE_VIRTUAL(182, 0),

        PUT_FIELD(181, null),

        PUT_STATIC(179, 0),

        GET_STATIC(178, 0),

        GET_FIELD(180, 0),

        RETURN(177, 0),

        ARETURN(176, -1),

        CHECK_CAST(192, 0),

        LDC(18, 1),

        POP(87, -1),

        WIDE(196, 0),

        INVOKE_STATIC(184, null),

        LRETURN(173, -2),

        FRETURN(174, -1),

        DRETURN(175, -2),

        IRETURN(172, -1);

        private final int opcode;

        private final Integer stackDepth;

        Opcode(int opcode, Integer stackDepth) {
            this.opcode = opcode;
            this.stackDepth = stackDepth;
        }

        public int getOpcode() {
            return opcode;
        }

        public Integer getStackDepth() {
            return stackDepth;
        }
    }
}
