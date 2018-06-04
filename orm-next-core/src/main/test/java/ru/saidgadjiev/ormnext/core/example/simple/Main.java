package ru.saidgadjiev.ormnext.core.example.simple;

import ru.saidgadjiev.proxymaker.MethodHandler;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class Main {

    public static void main(String[] args) throws Throwable {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);

        constructor.setAccessible(true);

        Method method = Test.class.getDeclaredMethod("setId", int.class);
        MethodHandles.Lookup lookup = constructor.newInstance(Test.class);

        MethodHandle handle = lookup.unreflect(method);

        BiConsumer consumer = (BiConsumer<Object, Object>) LambdaMetafactory.metafactory(
                lookup,
                "accept",
                MethodType.methodType(BiConsumer.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                handle,
                handle.type()
        ).getTarget().invokeExact();
    }
}
