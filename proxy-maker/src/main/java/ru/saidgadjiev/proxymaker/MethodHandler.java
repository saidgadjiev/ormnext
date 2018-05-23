package ru.saidgadjiev.proxymaker;

import java.lang.reflect.Method;

/**
 * This class associated with each proxy instance.
 */
public interface MethodHandler {

    /**
     * This method will be called instead of proxy class method.
     * @param method proxy method
     * @param args proxy mehod args
     * @return the value to return from the method invocation on the proxy instance
     */
    Object invoke(Method method, Object[] args);
}
