package ru.saidgadjiev.proxymaker.bytecode;

import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for generate code instruction for bytecode.
 */
public class CodeAttribute {

    /**
     * Instruction tag.
     */
    private static final String TAG = "Code";

    /**
     * Represent constant pool from bytecode.
     */
    private final ConstantPool constantPool;

    /**
     * Code instruction stack size.
     */
    private int maxStackSize;

    /**
     * Code instruction local vars.
     */
    private int maxLocalVars;

    /**
     * Code stack depth. If stackDepth > maxStackSize maxStackSize = stackDepth.
     */
    private int stackDepth;

    /**
     * Code instruction tag index in constant pool.
     */
    private int codeTagIndex;

    /**
     * Code instruction in byte array.
     */
    private List<Byte> code = new ArrayList<>();

    /**
     * Constructor init with maxStackSize 0 maxLocalVars 0.
     *
     * @param constantPool constant pool instruction
     */
    public CodeAttribute(ConstantPool constantPool) {
        this(constantPool, 0, 0);
    }

    /**
     * Constructor init code instruction with an empty byte array.
     *
     * @param constantPool constant pool instruction
     * @param maxStackSize code stack size
     * @param maxLocalVars code args size
     */
    public CodeAttribute(ConstantPool constantPool, int maxStackSize, int maxLocalVars) {
        this.constantPool = constantPool;
        this.maxStackSize = maxStackSize;
        this.maxLocalVars = maxLocalVars;
        this.codeTagIndex = constantPool.addUtf8Info(TAG);
    }

    /**
     * Appends {@link Opcode#ALOAD} instruction.
     *
     * @param n an index into the local variable array.
     */
    @SuppressWarnings("checkstyle:magicnumber")
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
                default:
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

    /**
     * Appends {@link Opcode#NEW} instruction.
     *
     * @param className the fully-qualified className of the created instance
     */
    public void addNew(String className) {
        addOpcode(Opcode.NEW);
        addIndex(constantPool.addClassInfo(className));
    }

    /**
     * Appends {@link Opcode#PUT_STATIC} instruction.
     *
     * @param className the fully-qualified className of the target class
     * @param fieldName the field name
     * @param desc      field descriptor
     */
    public void addPutStatic(String className, String fieldName, String desc) {
        add(Opcode.PUT_STATIC.getOpcode());
        int ci = constantPool.addClassInfo(className);

        addIndex(constantPool.addFieldRefInfo(ci, fieldName, desc));
        growStack(-ByteCodeUtils.dataSize(desc));
    }

    /**
     * Appends {@link Opcode#ANEWARRAY} instruction.
     *
     * @param className the fully-qualified className of the created array instance
     */
    public void addAnewarray(String className) {
        addOpcode(Opcode.ANEWARRAY);
        addIndex(constantPool.addClassInfo(className));
    }

    /**
     * Appends {@link Opcode#GET_STATIC} instruction.
     *
     * @param className the fully-qualified className of the target class
     * @param fieldName the field name
     * @param desc      field descriptor
     */
    public void addGetstatic(String className, String fieldName, String desc) {
        add(Opcode.GET_STATIC.getOpcode());
        int ci = constantPool.addClassInfo(className);

        addIndex(constantPool.addFieldRefInfo(ci, fieldName, desc));
        growStack(ByteCodeUtils.dataSize(desc));
    }

    /**
     * Appends {@link Opcode#PUT_FIELD} instruction.
     *
     * @param className the fully-qualified className of the target class
     * @param fieldName the field name
     * @param desc      the field descriptor
     */
    public void addPutField(String className, String fieldName, String desc) {
        add(Opcode.PUT_FIELD.getOpcode());

        int classInfoIndex = constantPool.addClassInfo(className);
        int fieldRefInfoIndex = constantPool.addFieldRefInfo(classInfoIndex, fieldName, desc);

        addIndex(fieldRefInfoIndex);
        growStack(-1 - ByteCodeUtils.dataSize(desc));
    }

    /**
     * Appends {@link Opcode} instruction.
     *
     * @param opcode the target opcode
     */
    public void addOpcode(Opcode opcode) {
        add(opcode.getOpcode());
        growStack(opcode.getStackDepth());
    }

    /**
     * Appends {@link Opcode#CHECK_CAST}.
     *
     * @param className the fully-qualified className for checkcast
     */
    public void addCheckCast(String className) {
        addOpcode(Opcode.CHECK_CAST);
        addIndex(constantPool.addClassInfo(className));
    }

    /**
     * Appends {@link Opcode#LDC}.
     *
     * @param value the pushed by {@link Opcode#LDC} value
     */
    public void addLdc(String value) {
        int valueInfoIndex = constantPool.addStringInfo(value);

        addLdc(valueInfoIndex);
    }

    /**
     * Appends {@link Opcode#LDC} or {@link Opcode#LDC_W}.
     *
     * @param n index into the constant pool
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public void addLdc(int n) {
        if (n > 0xFF) {
            addOpcode(Opcode.LDC_W);
            addIndex(n);
        } else {
            addOpcode(Opcode.LDC);
            add(n);
        }
    }

    /**
     * Appends {@link Opcode#GET_FIELD}.
     *
     * @param className the fully-qualified class name
     * @param fieldName the field name
     * @param desc      the descriptor of the field type
     */
    public void addGetField(String className, String fieldName, String desc) {
        add(Opcode.GET_FIELD.getOpcode());
        int ci = constantPool.addClassInfo(className);
        addIndex(constantPool.addFieldRefInfo(ci, fieldName, desc));
        growStack(ByteCodeUtils.dataSize(desc) - 1);
    }

    /**
     * Appends {@link Opcode#INVOKE_STATIC}.
     *
     * @param className  the fully-qualified class name
     * @param methodName the static method name
     * @param desc       the descriptor of the method
     */
    public void addInvokeStatic(String className, String methodName, String desc) {
        int classInfo = constantPool.addClassInfo(className);

        add(Opcode.INVOKE_STATIC.getOpcode());
        addIndex(constantPool.addMethodRefInfo(classInfo, methodName, desc));
        growStack(ByteCodeUtils.dataSize(desc));
    }

    /**
     * Appends {@link Opcode#INVOKE_SPECIAL}.
     *
     * @param className  the fully-qualified class name
     * @param methodName the method name
     * @param desc       the descriptor of the method
     */
    public void addInvokeSpecial(String className, String methodName, String desc) {
        add(Opcode.INVOKE_SPECIAL.getOpcode());
        int index = constantPool.addMethodRefInfo(constantPool.addClassInfo(className), methodName, desc);

        addIndex(index);
        growStack(ByteCodeUtils.dataSize(desc) - 1);
    }

    /**
     * Appends {@link Opcode#INVOKE_INTERFACE}.
     *
     * @param className  the fully-qualified class name
     * @param methodName the interface method name
     * @param desc       the descriptor of the method
     * @param count      the count operand of the instruction
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public void addInvokeInterface(String className, String methodName,
                                   String desc, int count) {
        add(Opcode.INVOKE_INTERFACE.getOpcode());
        addIndex(constantPool.addInterfaceMethodRefInfo(constantPool.addClassInfo(className), methodName, desc));
        add(count);
        add(0);
        growStack(ByteCodeUtils.dataSize(desc) - 1);
    }

    /**
     * Appends a 16bit value to the end of the bytecode sequence.
     * It never changes the current stack depth.
     *
     * @param value target value
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void addIndex(int value) {
        add(value >> 8);
        add(value);
    }

    /**
     * Sets the current stack depth.
     * It also updates {@code maxStackSize} if the current stack depth
     * is the deepest so far.
     *
     * @param diff new value.
     */
    private void growStack(int diff) {
        stackDepth += diff;
        if (stackDepth > maxStackSize) {
            maxStackSize = stackDepth;
        }
    }

    /**
     * Appends an 8bit value to the end of the bytecode sequence.
     *
     * @param code target opcode
     */
    private void add(int code) {
        this.code.add((byte) code);
    }

    /**
     * Appends iconst_[0, 1, 2, 3, 4] or {@link Opcode#BI_PUSH}.
     *
     * @param n the pushed integer constant
     */
    @SuppressWarnings("checkstyle:magicnumber")
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
                default:
                    break;
            }
        } else if (n <= 127 && -128 <= n) {
            addOpcode(Opcode.BI_PUSH);
            add(n);
        } else if (n <= 32767 && -32768 <= n) {
            addOpcode(Opcode.SI_PUSH);
            add(n >> 8);
            add(n);
        } else {
            addLdc(constantPool.addIntegerInfo(n));
        }
    }

    /**
     * Appends {@link Opcode#INVOKE_INTERFACE}.
     *
     * @param className  the fully-qualified class name
     * @param methodName the interface method name
     * @param desc       the descriptor of the method
     */
    public void addInvokeVirtual(String className, String methodName, String desc) {
        add(Opcode.INVOKE_VIRTUAL.getOpcode());
        addIndex(constantPool.addMethodRefInfo(constantPool.addClassInfo(className), methodName, desc));
        growStack(ByteCodeUtils.dataSize(desc) - 1);
    }

    /**
     * Appends {@link Opcode#LLOAD}.
     *
     * @param n an index into the local variable array
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public void addLload(int n) {
        if (n < 4) {
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
                default:
                    break;
            }
        } else if (n < 0x100) {
            addOpcode(Opcode.LLOAD);
            add(n);
        } else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.LLOAD);
            addIndex(n);
        }
    }

    /**
     * Appends {@link Opcode#FLOAD}.
     *
     * @param n an index into the local variable array
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public void addFload(int n) {
        if (n < 4) {
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
                default:
                    break;
            }
        } else if (n < 0x100) {
            addOpcode(Opcode.FLOAD);
            add(n);
        } else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.FLOAD);
            addIndex(n);
        }
    }

    /**
     * Appends {@link Opcode#DLOAD}.
     *
     * @param n an index into the local variable array
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public void addDload(int n) {
        if (n < 4) {
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
                default:
                    break;
            }
        } else if (n < 0x100) {
            addOpcode(Opcode.DLOAD);
            add(n);
        } else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.DLOAD);
            addIndex(n);
        }
    }

    /**
     * Appends {@link Opcode#ILOAD}.
     *
     * @param n an index into the local variable array
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public void addIload(int n) {
        if (n < 4) {
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
                default:
                    break;
            }
        } else if (n < 0x100) {
            addOpcode(Opcode.ILOAD);
            add(n);
        } else {
            addOpcode(Opcode.WIDE);
            addOpcode(Opcode.ILOAD);
            addIndex(n);
        }
    }

    /**
     * Write code byte array to {@link DataOutputStream}.
     *
     * @param outputStream target output stream
     * @throws IOException exception would be throw when write to {@link DataOutputStream}
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeShort(codeTagIndex);

        outputStream.writeInt(12 + code.size()); //code attribute lenght
        outputStream.writeShort(maxStackSize); //max stack size
        outputStream.writeShort(maxLocalVars); //max_local_var size
        outputStream.writeInt(code.size()); //size of code

        outputStream.write(ByteCodeUtils.toByteArray(code)); //machine instructions

        outputStream.writeShort(0); //exceptions size
        outputStream.writeShort(0); //attribute size
    }

    /**
     * This enum represent Opcodes.
     */

    public enum Opcode {

        /** ALOAD instruction.*/
        ALOAD(25, 1),

        /** ALOAD instruction for value = 0.*/
        ALOAD_0(42, 1),

        /** ALOAD instruction for value = 1.*/
        ALOAD_1(43, 1),

        /** ALOAD instruction for value = 2.*/
        ALOAD_2(44, 1),

        /** ALOAD instruction for value = 3.*/
        ALOAD_3(45, 1),

        /** FLOAD instruction.*/
        FLOAD(23, 1),

        /** FLOAD instruction for value = 0.*/
        FLOAD_0(34, 1),

        /** FLOAD instruction for value = 1.*/
        FLOAD_1(35, 1),

        /** FLOAD instruction for value = 2.*/
        FLOAD_2(36, 1),

        /** FLOAD instruction for value = 3.*/
        FLOAD_3(37, 1),

        /** LLOAD instruction.*/
        LLOAD(22, 2),

        /** LLOAD instruction for value = 0.*/
        LLOAD_0(30, 2),

        /** LLOAD instruction for value = 1.*/
        LLOAD_1(31, 2),

        /** LLOAD instruction for value = 2.*/
        LLOAD_2(32, 2),

        /** LLOAD instruction for value = 3.*/
        LLOAD_3(33, 2),

        /** DLOAD instruction.*/
        DLOAD(24, 2),

        /** DLOAD instruction for value = 0.*/
        DLOAD_0(38, 2),

        /** DLOAD instruction for value = 1.*/
        DLOAD_1(39, 2),

        /** DLOAD instruction for value = 2.*/
        DLOAD_2(40, 2),

        /** DLOAD instruction for value = 3.*/
        DLOAD_3(41, 2),

        /** ILOAD instruction.*/
        ILOAD(21, 2),

        /** ILOAD instruction for value = 0.*/
        ILOAD_0(26, 2),

        /** ILOAD instruction for value = 1.*/
        ILOAD_1(27, 2),

        /** ILOAD instruction for value = 2.*/
        ILOAD_2(28, 2),

        /** ILOAD instruction for value = 3.*/
        ILOAD_3(29, 2),

        /** ILOAD instruction for value = 0.*/
        ICONST_0(3, 1),

        /** ILOAD instruction for value = 1.*/
        ICONST_1(4, 1),

        /** ILOAD instruction for value = 2.*/
        ICONST_2(5, 1),

        /** ILOAD instruction for value = 3.*/
        ICONST_3(6, 1),

        /** ILOAD instruction for value = 4.*/
        ICONST_4(7, 1),

        /** ILOAD instruction for value = 5.*/
        ICONST_5(8, 1),

        /** AASTORE instruction.*/
        AASTORE(83, -3),

        /** BI_PUSH instruction.*/
        BI_PUSH(16, 1),

        /** SI_PUSH instruction.*/
        SI_PUSH(17, 1),

        /** NEW instruction.*/
        NEW(187, 1),

        /** ANEWARRAY instruction.*/
        ANEWARRAY(189, 0),

        /** DUP instruction.*/
        DUP(89, 1),

        /** INVOKE_SPECIAL instruction.*/
        INVOKE_SPECIAL(183, 0),

        /** INVOKE_INTERFACE instruction.*/
        INVOKE_INTERFACE(185, 0),

        /** INVOKE_VIRTUAL instruction.*/
        INVOKE_VIRTUAL(182, 0),

        /** PUT_FIELD instruction.*/
        PUT_FIELD(181, null),

        /** PUT_STATIC instruction.*/
        PUT_STATIC(179, 0),

        /** GET_STATIC instruction.*/
        GET_STATIC(178, 0),

        /** GET_FIELD instruction.*/
        GET_FIELD(180, 0),

        /** RETURN instruction.*/
        RETURN(177, 0),

        /** ARETURN instruction.*/
        ARETURN(176, -1),

        /** CHECK_CAST instruction.*/
        CHECK_CAST(192, 0),

        /** LDC instruction.*/
        LDC(18, 1),

        /** LDC_W instruction.*/
        LDC_W(19, 1),

        /** POP instruction.*/
        POP(87, -1),

        /** WIDE instruction.*/
        WIDE(196, 0),

        /** INVOKE_STATIC instruction.*/
        INVOKE_STATIC(184, null),

        /** LRETURN instruction.*/
        LRETURN(173, -2),

        /** FRETURN instruction.*/
        FRETURN(174, -1),

        /** DRETURN instruction.*/
        DRETURN(175, -2),

        /** IRETURN instruction.*/
        IRETURN(172, -1);

        /**
         * Opcode instruction code.
         */
        private final int opcode;

        /**
         * how many values are pushed on the operand stack.
         */
        private final Integer stackDepth;

        /**
         * Constructor.
         * @param opcode bytecode opcode
         * @param stackDepth represent how many values are pushed on the operand stack
         */
        Opcode(int opcode, Integer stackDepth) {
            this.opcode = opcode;
            this.stackDepth = stackDepth;
        }

        /**
         * Return associated opcode.
         * @return associated opcode.
         */
        public int getOpcode() {
            return opcode;
        }

        /**
         * Return count off values are pushed on the operand stack.
         * @return operand stack
         */
        public Integer getStackDepth() {
            return stackDepth;
        }
    }
}
