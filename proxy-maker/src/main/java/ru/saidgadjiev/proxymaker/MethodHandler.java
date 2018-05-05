package ru.saidgadjiev.proxymaker;

import java.lang.reflect.Method;

public interface MethodHandler {

    Object invoke(Method method, Object[] args);
}
