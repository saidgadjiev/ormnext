package ru.saidgadjiev.proxymaker;

/**
 * Each proxy class implements this interface.
 */
public interface Proxy {

    /**
     * Provide proxy method handler {@link MethodHandler}.
     *
     * @param handler target method handler
     */
    void setHandler(MethodHandler handler);

    /**
     * Return method handler.
     *
     * @return method handler
     */
    MethodHandler getHandler();
}
