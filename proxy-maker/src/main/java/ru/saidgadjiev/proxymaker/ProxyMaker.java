package ru.saidgadjiev.proxymaker;

import ru.saidgadjiev.proxymaker.bytecode.CodeAttribute;
import ru.saidgadjiev.proxymaker.bytecode.FieldInfo;
import ru.saidgadjiev.proxymaker.bytecode.MethodInfo;
import ru.saidgadjiev.proxymaker.bytecode.common.ByteCodeUtils;
import ru.saidgadjiev.proxymaker.bytecode.constantpool.ConstantPool;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.saidgadjiev.proxymaker.ProxyFactoryHelper.*;

public class ProxyMaker {

    private static final Class<?> OBJECT_CLASS = Object.class;

    private static final String HANDLER_FIELD_NAME = "handler";

    private static final String HANDLER_TYPE = 'L' + MethodHandler.class.getName().replace('.', '/') + ';';

    private static final String HANDLER_SETTER_METHOD_NAME = "setHandler";

    private static final String HANDLER_SETTER_METHOD_TYPE = "(" + HANDLER_TYPE + ")V";

    private static final String HOLDER_FIELD_NAME = "methods";

    private static final String HOLDER_FIELD_TYPE = "L" + List.class.getName().replace('.', '/') + ";";

    public static final String PROXY_INTERFACE_INVOKE_METHOD_NAME = "invoke";

    public static final String PROXY_INTERFACE_INVOKE_METHOD_DESC = "(Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";

    private Class<?> superClass;

    private String superClassName;

    private String className;

    private List<Method> methods = new ArrayList<>();

    private static final WeakHashMap<String, Class<?>> PROXY_CACHE = new WeakHashMap<>();

    private UIDGenerator uidGenerator = new UIDGenerator() {
        private AtomicInteger uid = new AtomicInteger();

        @Override
        public int nextUID() {
            return uid.getAndIncrement();
        }
    };

    private static final Map<String, Class<?>> OVERRIDE_IGNORE_METHODS = new HashMap<String, Class<?>>() {{
        put("getClass", Object.class);
        put("notify", Object.class);
        put("notifyAll", Object.class);
        put("wait", Object.class);
        put("finalize", Object.class);
        put("clone", Object.class);
    }};

    public ProxyMaker superClass(Class<?> superClass) {
        this.superClass = superClass;

        return this;
    }

    private void resolveSuperClassAndClassName() {
        if (superClass == null) {
            superClass = OBJECT_CLASS;
            superClassName = OBJECT_CLASS.getName();
        } else {
            superClassName = superClass.getName();
        }

        className = superClassName + uidGenerator.nextUID();
        if (className.startsWith("java."))
            className = "ru.proxy.maker.tmp." + className;
    }

    private void resolveMethods() {
        methods = getMethods(superClass);
    }

    private List<Method> getMethods(Class<?> superClass) {
        Map<String, Method> methods = new LinkedHashMap<>();
        Set<Class<?>> visitedClasses = new HashSet<>();

        getMethods(methods, superClass, visitedClasses);

        return new ArrayList<>(methods.values());
    }

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
            if (!Modifier.isPrivate(method.getModifiers()) &&
                    !(OVERRIDE_IGNORE_METHODS.containsKey(method.getName()) && OVERRIDE_IGNORE_METHODS.get(method.getName()).equals(method.getDeclaringClass()))) {
                String key = method.getName() + ":" + ByteCodeUtils.makeDescriptor(method);

                methods.put(key, method);
            }
        }
    }

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

    public Object make(Class<?>[] parametrTypes, Object[] args, MethodHandler handler) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String key = getKey();

        if (PROXY_CACHE.containsKey(key)) {
            return PROXY_CACHE.get(key);
        }

        resolveSuperClassAndClassName();
        resolveMethods();
        ClassFile classFile = new ClassFile(className, superClassName);

        classFile.setAccessFlags(AccessFlag.PUBLIC);
        addDefaultConstructor(classFile);
        setInterfaces(classFile, new Class[0], Proxy.class);
        addDefaultClassFields(classFile);
        addClassInitializer(classFile);
        addHandlerSetter(classFile);
        overrideMethods(classFile);

        Class<?> proxyClass = ProxyFactoryHelper.toClass(classFile);

        PROXY_CACHE.put(getKey(), proxyClass);
        Constructor<?> constructor = proxyClass.getConstructor(parametrTypes);
        Object proxyInstance = constructor.newInstance(args);

        ((Proxy) proxyInstance).setHandler(handler);

        return proxyInstance;
    }

    private String getKey() {
        return superClass.getName();
    }

    private void addDefaultClassFields(ClassFile classFile) {
        FieldInfo handlerField = new FieldInfo(classFile.getConstantPool(), HANDLER_FIELD_NAME, HANDLER_TYPE);

        handlerField.setAccessFlags(AccessFlag.PRIVATE);
        classFile.addFieldInfo(handlerField);

        FieldInfo holderField = new FieldInfo(classFile.getConstantPool(), HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);

        holderField.setAccessFlags(AccessFlag.PRIVATE | AccessFlag.STATIC);
        classFile.addFieldInfo(holderField);
    }

    private void addHandlerSetter(ClassFile classFile) {
        MethodInfo methodInfo = new MethodInfo(classFile.getConstantPool(), HANDLER_SETTER_METHOD_NAME, HANDLER_SETTER_METHOD_TYPE);

        methodInfo.setAccessFlags(AccessFlag.PUBLIC);

        CodeAttribute codeAttribute = new CodeAttribute(classFile.getConstantPool(), 2, 2);

        codeAttribute.addAload(0);
        codeAttribute.addAload(1);
        codeAttribute.addPutField(className, HANDLER_FIELD_NAME, HANDLER_TYPE);
        codeAttribute.addOpcode(CodeAttribute.Opcode.RETURN);
        methodInfo.setCodeAttribute(codeAttribute);
        classFile.addMethodInfo(methodInfo);
    }

    private void addClassInitializer(ClassFile classFile) {
        MethodInfo methodInfo = new MethodInfo(classFile.getConstantPool(), MethodInfo.NAME_CLINIT, "()V");

        methodInfo.setAccessFlags(AccessFlag.STATIC);
        CodeAttribute codeAttribute = new CodeAttribute(classFile.getConstantPool(), 0, 0);

        codeAttribute.addNew(ArrayList.class.getName());
        codeAttribute.addOpcode(CodeAttribute.Opcode.DUP);
        codeAttribute.addInvokeSpecial(ArrayList.class.getName(), MethodInfo.NAME_INIT, "()V");

        codeAttribute.addPutStatic(className, HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);
        codeAttribute.addGetstatic(className, HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);

        for (Iterator<Method> methodIterator = methods.iterator(); methodIterator.hasNext(); ) {
            addCallFindMethod(codeAttribute, methodIterator.next());

            if (methodIterator.hasNext()) {
                codeAttribute.addGetstatic(className, HOLDER_FIELD_NAME, HOLDER_FIELD_TYPE);
            }
        }
        codeAttribute.addOpcode(CodeAttribute.Opcode.RETURN);
        methodInfo.setCodeAttribute(codeAttribute);
        classFile.addMethodInfo(methodInfo);
    }

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
            for (Iterator<Class<?>> parametrTypeIterator = Arrays.asList(method.getParameterTypes()).iterator(); parametrTypeIterator.hasNext(); ) {
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

    private void overrideMethods(ClassFile classFile) {
        int methodIndex = 0;

        for (Method method : methods) {
            MethodInfo methodInfo = new MethodInfo(classFile.getConstantPool(), method.getName(), ByteCodeUtils.makeDescriptor(method));

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
        codeAttribute.addInvokeInterface(MethodHandler.class.getName(), PROXY_INTERFACE_INVOKE_METHOD_NAME, PROXY_INTERFACE_INVOKE_METHOD_DESC, 3);

        Class retType = method.getReturnType();
        addUnWrapper(codeAttribute, retType);
        addReturn(codeAttribute, retType);

        return codeAttribute;
    }

    private static void makeParameterList(CodeAttribute code, Class[] params) {
        int regno = 1;
        int argsSize = params.length;

        code.addIconst(argsSize);
        code.addAnewarray("java/lang/Object");
        for (int i = 0; i < argsSize; i++) {
            code.addOpcode(CodeAttribute.Opcode.DUP);
            code.addIconst(i);
            Class type = params[i];
            if (type.isPrimitive())
                regno = makeWrapper(code, type, regno);
            else {
                code.addAload(regno);
                regno++;
            }

            code.addOpcode(CodeAttribute.Opcode.AASTORE);
        }
    }

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

    private static int addLoad(CodeAttribute code, int n, Class type) {
        if (type.isPrimitive()) {
            if (type == Long.TYPE) {
                code.addLload(n);
                return 2;
            }
            else if (type == Float.TYPE)
                code.addFload(n);
            else if (type == Double.TYPE) {
                code.addDload(n);
                return 2;
            }
            else
                code.addIload(n);
        }
        else
            code.addAload(n);

        return 1;
    }

    private static void addUnWrapper(CodeAttribute code, Class type) {
        if (type.isPrimitive()) {
            if (type == Void.TYPE)
                code.addOpcode(CodeAttribute.Opcode.POP);
            else {
                int index = typeIndex(type);
                String wrapper = WRAPPER_TYPES[index];
                code.addCheckCast(wrapper);
                code.addInvokeVirtual(wrapper,
                        UNWARP_METHODS[index],
                        UNWRAP_DESC[index]);
            }
        } else
            code.addCheckCast(type.getName());
    }

    private static int addReturn(CodeAttribute code, Class type) {
        if (type.isPrimitive()) {
            if (type == Long.TYPE) {
                code.addOpcode(CodeAttribute.Opcode.LRETURN);
                return 2;
            } else if (type == Float.TYPE)
                code.addOpcode(CodeAttribute.Opcode.FRETURN);
            else if (type == Double.TYPE) {
                code.addOpcode(CodeAttribute.Opcode.DRETURN);
                return 2;
            } else if (type == Void.TYPE) {
                code.addOpcode(CodeAttribute.Opcode.RETURN);
                return 0;
            } else
                code.addOpcode(CodeAttribute.Opcode.IRETURN);
        } else
            code.addOpcode(CodeAttribute.Opcode.ARETURN);

        return 1;
    }

    private void setInterfaces(ClassFile classFile, Class<?>[] interfaces, Class<?> proxyInterface) {
        Collection<String> interfaceNames = Arrays.asList(proxyInterface.getName());

        for (Class<?> userInterface : interfaces) {
            interfaceNames.add(userInterface.getName());
        }

        classFile.setInterfaces(interfaceNames);
    }

    private interface UIDGenerator {
        int nextUID();
    }

}
