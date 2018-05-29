package ru.saidgadjiev.proxymaker;

import ru.saidgadjiev.proxymaker.bytecode.ByteCodeUtils;
import ru.saidgadjiev.proxymaker.bytecode.CodeAttribute;
import ru.saidgadjiev.proxymaker.bytecode.FieldInfo;
import ru.saidgadjiev.proxymaker.bytecode.MethodInfo;
import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.saidgadjiev.proxymaker.ProxyFactoryHelper.*;

/**
 * Class for create dynamic proxy classes.
 */
public class ProxyMaker {

    /**
     * Object class {@link Class}.
     */
    private static final Class<?> OBJECT_CLASS = Object.class;

    /**
     * Proxy method invocation handler field name in dynamic proxy class.
     */
    private static final String HANDLER_FIELD_NAME = "handler";

    /**
     * Proxy invocation handler type for bytecode.
     */
    private static final String HANDLER_TYPE = 'L' + MethodHandler.class.getName().replace('.', '/') + ';';

    /**
     * Invocation handler setter method name {@link Proxy#setHandler(MethodHandler)}.
     */
    private static final String HANDLER_SETTER_METHOD_NAME = "setHandler";

    /**
     * Invocation handler setter method type.
     */
    private static final String HANDLER_SETTER_METHOD_TYPE = "(" + HANDLER_TYPE + ")V";

    /**
     * Proxied methods list holder field name.
     */
    private static final String HOLDER_FIELD_NAME = "methods";

    /**
     * Proxied methods list holder field type. It is {@link List}.
     */
    private static final String HOLDER_FIELD_TYPE = "L" + List.class.getName().replace('.', '/') + ";";

    /**
     * Proxy invocation handler method name.
     *
     * @see MethodHandler#invoke
     */
    private static final String PROXY_INTERFACE_INVOKE_METHOD_NAME = "invoke";

    /**
     * Proxy invocation handler method description.
     */
    private static final String PROXY_INTERFACE_INVOKE_METHOD_DESC =
            "(Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";

    /**
     * Invocation handler method definition count {@link MethodHandler#invoke(Method, Object[])}.
     */
    private static final int PROXY_INTERFACE_INVOKE_METHOD_DEFINITION_COUTN = 3;

    /**
     * Super class.
     */
    private Class<?> superClass;

    /**
     * Fully qualified super class name.
     */
    private String superClassName;

    /**
     * Fully qualified dynamic proxy class name.
     */
    private String className;

    /**
     * Proxied methods list.
     */
    private List<Method> methods = new ArrayList<>();

    /**
     * Cached dynamic proxy classes.
     */
    private static final WeakHashMap<String, Class<?>> PROXY_CACHE = new WeakHashMap<>();

    /**
     * Unique integer index generator. Used for make dynamic proxy class name unique.
     */
    private UIDGenerator uidGenerator = new UIDGenerator() {
        private AtomicInteger uid = new AtomicInteger();

        @Override
        public int nextUID() {
            return uid.getAndIncrement();
        }
    };

    /**
     * Ignore override methods.
     */
    private static final Map<String, Class<?>> OVERRIDE_IGNORE_METHODS = new HashMap<String, Class<?>>() {{
        put("getClass", Object.class);
        put("notify", Object.class);
        put("notifyAll", Object.class);
        put("wait", Object.class);
        put("finalize", Object.class);
        put("clone", Object.class);
    }};

    /**
     * Provide dynamic proxy super class. If not provided it will be {@code OBJECT_CLASS}
     *
     * @param superClass target super class
     * @return current instance.
     */
    public ProxyMaker superClass(Class<?> superClass) {
        this.superClass = superClass;

        return this;
    }

    /**
     * Resolve super class and dynamic proxy class names.
     * If super class is null super class will be {@code OBJECT_CLASS}.
     * Class name make with concatenate {@code superClassName} and {@code uidGenerator} nextUID.
     */
    private void resolveSuperClassAndClassName() {
        if (superClass == null) {
            superClass = OBJECT_CLASS;
            superClassName = OBJECT_CLASS.getName();
        } else {
            superClassName = superClass.getName();
        }

        className = superClassName + uidGenerator.nextUID();
        if (className.startsWith("java.")) {
            className = "ru.proxy.maker.tmp." + className;
        }
    }

    /**
     * Resolve dynamic proxy override methods.
     * Ignore methods @{code OVERRIDE_IGNORE_METHODS}
     */
    private void resolveMethods() {
        Map<String, Method> methods = new LinkedHashMap<>();
        Set<Class<?>> visitedClasses = new HashSet<>();

        getMethods(methods, superClass, visitedClasses);

        this.methods = new ArrayList<>(methods.values());
    }

    /**
     * Recursively resolve methods by target class.
     *
     * @param methods        map for save resolved methods
     * @param targetClass    target class {@link Class}
     * @param visitedClasses visited classes. This both speeds up scanning by avoiding duplicate interfaces and
     *                       is needed to ensure that superinterfaces are always scanned before subinterfaces.
     */
    private void getMethods(Map<String, Method> methods, Class<?> targetClass, Set<Class<?>> visitedClasses) {
        if (!visitedClasses.add(targetClass)) {
            return;
        }

        Class<?>[] interfaces = targetClass.getInterfaces();

        for (Class<?> interfaceClass : interfaces) {
            getMethods(methods, interfaceClass, visitedClasses);
        }
        Class<?> parentClass = targetClass.getSuperclass();

        if (parentClass != null) {
            getMethods(methods, parentClass, visitedClasses);
        }
        for (Method method : targetClass.getDeclaredMethods()) {
            if (!Modifier.isPrivate(method.getModifiers())
                    && !(OVERRIDE_IGNORE_METHODS.containsKey(method.getName())
                    && OVERRIDE_IGNORE_METHODS.get(method.getName()).equals(method.getDeclaringClass()))) {
                String key = method.getName() + ":" + ByteCodeUtils.makeDescriptor(method);

                methods.put(key, method);
            }
        }
    }

    /**
     * Add default constructor to class file {@link ClassFile}.
     *
     * @param classFile target class file
     */
    private void addDefaultConstructor(ClassFile classFile) {
        MethodInfo methodInfo = new MethodInfo(classFile.getConstantPool(), MethodInfo.NAME_INIT, "()V");

        methodInfo.setAccessFlags(AccessFlag.PUBLIC);
        CodeAttribute codeAttribute = new CodeAttribute(classFile.getConstantPool(), 0, 1);

        codeAttribute.addAload(0);
        codeAttribute.addInvokeSpecial(superClass.getName(), MethodInfo.NAME_INIT, "()V");
        codeAttribute.addOpcode(CodeAttribute.Opcode.RETURN);

        methodInfo.setCodeAttribute(codeAttribute);
        classFile.addMethodInfo(methodInfo);
    }

    /**
     * Create new dynamic proxy class instance.
     *
     * @param handler method handler for the proxy class
     * @return new dynamic proxy class intance
     * @throws InvocationTargetException throws in {@link Constructor#newInstance(Object...)}
     * @throws IllegalAccessException    throws in {@link Constructor#newInstance(Object...)}
     * @throws NoSuchMethodException     throws in {@link Class#getConstructor(Class[])}}
     * @throws InstantiationException    throws in {@link Constructor#newInstance(Object...)}
     */
    public Object make(MethodHandler handler) throws
            InvocationTargetException,
            IllegalAccessException,
            NoSuchMethodException,
            InstantiationException {
        String key = getKey();

        if (PROXY_CACHE.containsKey(key)) {
            createInstance(PROXY_CACHE.get(key), handler);
        }

        resolveSuperClassAndClassName();
        resolveMethods();
        ClassFile classFile = new ClassFile(className, superClassName);

        classFile.setAccessFlags(AccessFlag.PUBLIC);
        addDefaultConstructor(classFile);
        classFile.setInterfaces(Collections.singleton(Proxy.class.getName()));
        addDefaultClassFields(classFile);
        addClassInitializer(classFile);
        addHandlerSetter(classFile);
        overrideMethods(classFile);

        Class<?> proxyClass = ProxyFactoryHelper.toClass(classFile);

        PROXY_CACHE.put(getKey(), proxyClass);

        return createInstance(proxyClass, handler);
    }

    /**
     * Create new instance of dynamic proxy class {@code proxyClass} by default constructor.
     *
     * @param proxyClass target class
     * @param handler    dynamic proxy invocation handler
     * @return new dynamic proxy class instance
     * @throws NoSuchMethodException     throws in {@link Class#getConstructor(Class[])}
     * @throws IllegalAccessException    throws in {@link Constructor#newInstance(Object...)
     * @throws InvocationTargetException throws in {@link Constructor#newInstance(Object...)
     * @throws InstantiationException    throws in {@link Constructor#newInstance(Object...)
     */
    private Object createInstance(Class<?> proxyClass, MethodHandler handler) throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        Constructor<?> constructor = proxyClass.getConstructor();
        Object proxyInstance = constructor.newInstance();

        ((Proxy) proxyInstance).setHandler(handler);

        return proxyInstance;
    }

    /**
     * Return key for cached proxy class.
     *
     * @return this dynamic proxy class cached ky
     */
    private String getKey() {
        return superClass.getName();
    }

    /**
     * Add default dynamic proxy class fields.
     * Add field for holder proxied methods, field for invocation handler {@link MethodHandler}.
     *
     * @param classFile target class file
     */
    private void addDefaultClassFields(ClassFile classFile) {
        FieldInfo handlerField = new FieldInfo(classFile.getConstantPool(), HANDLER_FIELD_NAME, HANDLER_TYPE);

        handlerField.setAccessFlags(AccessFlag.PRIVATE);
        classFile.addFieldInfo(handlerField);

        FieldInfo holderField = new FieldInfo(classFile.getConstantPool(), HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);

        holderField.setAccessFlags(AccessFlag.PRIVATE | AccessFlag.STATIC);
        classFile.addFieldInfo(holderField);
    }

    /**
     * Add invocation handler setter method {@link Proxy#setHandler(MethodHandler)}.
     *
     * @param classFile target class file
     */
    private void addHandlerSetter(ClassFile classFile) {
        MethodInfo methodInfo = new MethodInfo(
                classFile.getConstantPool(),
                HANDLER_SETTER_METHOD_NAME,
                HANDLER_SETTER_METHOD_TYPE
        );

        methodInfo.setAccessFlags(AccessFlag.PUBLIC);

        CodeAttribute codeAttribute = new CodeAttribute(classFile.getConstantPool(), 2, 2);

        codeAttribute.addAload(0);
        codeAttribute.addAload(1);
        codeAttribute.addPutField(className, HANDLER_FIELD_NAME, HANDLER_TYPE);
        codeAttribute.addOpcode(CodeAttribute.Opcode.RETURN);
        methodInfo.setCodeAttribute(codeAttribute);
        classFile.addMethodInfo(methodInfo);
    }

    /**
     * Add static block to dynamic proxy class for resolve proxy method refs.
     *
     * @param classFile target class file.
     */
    private void addClassInitializer(ClassFile classFile) {
        MethodInfo methodInfo = new MethodInfo(classFile.getConstantPool(), MethodInfo.NAME_CLINIT, "()V");

        methodInfo.setAccessFlags(AccessFlag.STATIC);
        CodeAttribute codeAttribute = new CodeAttribute(classFile.getConstantPool(), 0, 0);

        codeAttribute.addNew(ArrayList.class.getName());
        codeAttribute.addOpcode(CodeAttribute.Opcode.DUP);
        codeAttribute.addInvokeSpecial(ArrayList.class.getName(), MethodInfo.NAME_INIT, "()V");

        codeAttribute.addPutStatic(className, HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);
        codeAttribute.addGetstatic(className, HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);

        for (Iterator<Method> methodIterator = methods.iterator(); methodIterator.hasNext();) {
            addCallFindMethod(codeAttribute, methodIterator.next());

            if (methodIterator.hasNext()) {
                codeAttribute.addGetstatic(className, HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);
            }
        }
        codeAttribute.addOpcode(CodeAttribute.Opcode.RETURN);
        methodInfo.setCodeAttribute(codeAttribute);
        classFile.addMethodInfo(methodInfo);
    }

    /**
     * Add static call {@link ProxyRuntimeHelper#findMethod(String, String, String[])} for find method in super class.
     *
     * @param code   target code attribute
     * @param method target method
     */
    private void addCallFindMethod(CodeAttribute code, Method method) {
        String findClass = ProxyRuntimeHelper.class.getName();
        String findDesc
                = "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/reflect/Method;";

        code.addLdc(superClassName);
        code.addLdc(method.getName());
        code.addIconst(method.getParameterTypes().length);
        code.addAnewarray(String.class.getName());

        if (method.getParameterTypes().length > 0) {
            int iconstNumber = 0;

            code.addOpcode(CodeAttribute.Opcode.DUP);
            for (Iterator<Class<?>> parametrTypeIterator = Arrays.asList(method.getParameterTypes()).iterator();
                 parametrTypeIterator.hasNext();) {
                code.addIconst(iconstNumber++);
                code.addLdc(parametrTypeIterator.next().getName());
                code.addOpcode(CodeAttribute.Opcode.AASTORE);

                if (parametrTypeIterator.hasNext()) {
                    code.addOpcode(CodeAttribute.Opcode.DUP);
                }
            }
        }
        code.addInvokeStatic(findClass, "findMethod", findDesc);
        code.addInvokeInterface(List.class.getName(), "add", "(Ljava/lang/Object;)Z", 2);
        code.addOpcode(CodeAttribute.Opcode.POP);
    }

    /**
     * Override methods in dynamic proxy class.
     *
     * @param classFile target class file
     */
    private void overrideMethods(ClassFile classFile) {
        int methodIndex = 0;

        for (Method method : methods) {
            MethodInfo methodInfo = new MethodInfo(
                    classFile.getConstantPool(),
                    method.getName(),
                    ByteCodeUtils.makeDescriptor(method)
            );

            methodInfo.setAccessFlags(Modifier.FINAL | Modifier.PUBLIC
                    | (method.getModifiers() & ~(Modifier.PRIVATE
                    | Modifier.PROTECTED
                    | Modifier.ABSTRACT
                    | Modifier.NATIVE
                    | Modifier.SYNCHRONIZED)));

            methodInfo.setCodeAttribute(toMethodCode(classFile.getConstantPool(), method, methodIndex));
            classFile.addMethodInfo(methodInfo);
            ++methodIndex;
        }
    }

    /**
     * Create proxied method code in dynamic proxy class.
     *
     * @param constantPool target constant pool
     * @param method       target method
     * @param methodIndex  target method index from methods list field in dynamic proxy class
     * @return proxied method code which proxy method call to invocation handler.
     */
    private CodeAttribute toMethodCode(ConstantPool constantPool, Method method, int methodIndex) {
        int argsSize = method.getParameterCount();
        CodeAttribute codeAttribute = new CodeAttribute(constantPool, 0, argsSize + 2);

        codeAttribute.addAload(0);
        codeAttribute.addGetField(className, HANDLER_FIELD_NAME, HANDLER_TYPE);
        codeAttribute.addGetstatic(className, HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);
        codeAttribute.addIconst(methodIndex);
        codeAttribute.addInvokeInterface(List.class.getName(), "get", "(I)Ljava/lang/Object;", 2);
        codeAttribute.addCheckCast("java/lang/reflect/Method");
        makeParameterList(codeAttribute, method.getParameterTypes());
        codeAttribute.addInvokeInterface(
                MethodHandler.class.getName(),
                PROXY_INTERFACE_INVOKE_METHOD_NAME,
                PROXY_INTERFACE_INVOKE_METHOD_DESC,
                PROXY_INTERFACE_INVOKE_METHOD_DEFINITION_COUTN
        );

        Class retType = method.getReturnType();
        addUnWrapper(codeAttribute, retType);
        addReturn(codeAttribute, retType);

        return codeAttribute;
    }

    /**
     * Make invoke handler args array.
     *
     * @param code   target method code
     * @param params target arg types
     */
    private static void makeParameterList(CodeAttribute code, Class[] params) {
        int regno = 1;
        int argsSize = params.length;

        code.addIconst(argsSize);
        code.addAnewarray("java/lang/Object");
        for (int i = 0; i < argsSize; i++) {
            code.addOpcode(CodeAttribute.Opcode.DUP);
            code.addIconst(i);
            Class type = params[i];
            if (type.isPrimitive()) {
                regno = makeWrapper(code, type, regno);
            } else {
                code.addAload(regno);
                regno++;
            }

            code.addOpcode(CodeAttribute.Opcode.AASTORE);
        }
    }

    /**
     * Make wrapper for primitive types. Example: 2->new Integer(2)...
     *
     * @param code  target method code
     * @param type  primitive type class {@link Class}
     * @param regno arg index in args array
     * @return regno + {@link ProxyFactoryHelper#DATA_SIZE}
     */
    private static int makeWrapper(CodeAttribute code, Class type, int regno) {
        int index = typeIndex(type);
        String wrapper = WRAPPER_TYPES[index];

        code.addNew(wrapper);
        code.addOpcode(CodeAttribute.Opcode.DUP);
        addLoad(code, regno, type);
        code.addInvokeSpecial(wrapper, "<init>",
                WRAPPER_DESC[index]);

        return regno + DATA_SIZE[index];
    }

    /**
     * Push n to local variable.
     *
     * @param code target method code
     * @param n    target variable
     * @param type primitive type class {@link Class}
     */
    private static void addLoad(CodeAttribute code, int n, Class type) {
        if (type.isPrimitive()) {
            if (type == Long.TYPE) {
                code.addLload(n);
            } else if (type == Float.TYPE) {
                code.addFload(n);
            } else if (type == Double.TYPE) {
                code.addDload(n);
            } else {
                code.addIload(n);
            }
        } else {
            code.addAload(n);
        }
    }

    /**
     * Add unwrap methods for return value. For primitive type int it will be {@link Integer#intValue()}.
     * Another add checkcast{@link CodeAttribute#addCheckCast(String)} instruction.
     *
     * @param code target method code
     * @param type method arg type
     */
    private static void addUnWrapper(CodeAttribute code, Class type) {
        if (type.isPrimitive()) {
            if (type == Void.TYPE) {
                code.addOpcode(CodeAttribute.Opcode.POP);
            } else {
                int index = typeIndex(type);
                String wrapper = WRAPPER_TYPES[index];
                code.addCheckCast(wrapper);
                code.addInvokeVirtual(wrapper,
                        UNWARP_METHODS[index],
                        UNWRAP_DESC[index]);
            }
        } else {
            code.addCheckCast(type.getName());
        }
    }

    /**
     * Add return instruction to method.
     *
     * @param code target method code
     * @param type arg type {@link Class}
     */
    private static void addReturn(CodeAttribute code, Class type) {
        if (type.isPrimitive()) {
            if (type == Long.TYPE) {
                code.addOpcode(CodeAttribute.Opcode.LRETURN);
            } else if (type == Float.TYPE) {
                code.addOpcode(CodeAttribute.Opcode.FRETURN);
            } else if (type == Double.TYPE) {
                code.addOpcode(CodeAttribute.Opcode.DRETURN);
            } else if (type == Void.TYPE) {
                code.addOpcode(CodeAttribute.Opcode.RETURN);
            } else {
                code.addOpcode(CodeAttribute.Opcode.IRETURN);
            }
        } else {
            code.addOpcode(CodeAttribute.Opcode.ARETURN);
        }
    }

    /**
     * Interface represent uid generator which would generate unique uids.
     */
    private interface UIDGenerator {

        /**
         * This method will be called for generate next unique uid.
         *
         * @return next uid
         */
        int nextUID();
    }

}
