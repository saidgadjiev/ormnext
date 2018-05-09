package ru.saidgadjiev.ormnext.core;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.Function;

public class TestMethodPerf
{

    private int i;
    public static void main(String... args) throws Throwable {
        TestClass testClass = new TestClass();

        testClass.getI();
        Method reflected = TestClass.class
                .getDeclaredMethod("getI");

        reflected.invoke(testClass);
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mh = lookup.unreflect(reflected);
        MethodType methodType = MethodType.methodType(int.class, TestClass.class);
        Function lambda = (Function) LambdaMetafactory.metafactory(
                lookup, "apply", MethodType.methodType(Function.class),
                MethodType.methodType(Object.class, Object.class), mh, methodType).getTarget().invokeExact();

        System.out.println( lambda.apply(testClass));


    }
}