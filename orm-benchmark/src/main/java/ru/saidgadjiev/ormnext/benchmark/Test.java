package ru.saidgadjiev.ormnext.benchmark;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class Test {

    public static void main(String[] args) throws Throwable {
        Test test = new Test();
        Method method = Test.class.getDeclaredMethod("setId", int.class);
        long startAccessByMethod = System.currentTimeMillis();

        for (int i = 0; i < 10000000; ++i) {
            method.invoke(test, i);
        }
        System.out.println("Method access " + (System.currentTimeMillis() - startAccessByMethod));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.unreflect(method);
        long startAccessByMethodHandle = System.currentTimeMillis();

        for (int i = 0; i < 10000000; ++i) {
            methodHandle.invoke(test, i);
        }
        System.out.println("MethodHandle access " + (System.currentTimeMillis() - startAccessByMethodHandle));

        BiConsumer setter = (BiConsumer) LambdaMetafactory.metafactory(
                lookup,
                "accept",
                MethodType.methodType(BiConsumer.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                methodHandle,
                methodHandle.type()
        ).getTarget().invokeExact();
        long startAccessByLambda = System.currentTimeMillis();

        for (int i = 0; i < 10000000; ++i) {
            setter.accept(test, i);
        }
        System.out.println("Lambda access " + (System.currentTimeMillis() - startAccessByLambda));

        long directAccess = System.currentTimeMillis();

        for (int i = 0; i < 10000000; ++i) {
            test.setId(i);
        }
        System.out.println("Direct access " + (System.currentTimeMillis() - directAccess));

    }

    public void setId(int id) {

    }
}
