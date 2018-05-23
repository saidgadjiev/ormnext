package ru.saidgadjiev.proxymaker;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Security utils.
 */
public final class SecurityHelper {

    /**
     * Utils class can't be instantiated.
     */
    private SecurityHelper() {
    }

    /**
     * Get declared class method via {@link AccessController}.
     *
     * @param clazz target class {@link Class}
     * @param name  target method name
     * @param types target method type classes
     * @return method reference {@link Method}
     * @throws NoSuchMethodException throws in {@link Class#getDeclaredMethod(String, Class[])}
     */
    static Method getDeclaredMethod(final Class<?> clazz, final String name,
                                    final Class[] types) throws NoSuchMethodException {
        if (System.getSecurityManager() == null) {
            return clazz.getDeclaredMethod(name, types);
        } else {
            try {
                return (Method) AccessController
                        .doPrivileged((PrivilegedExceptionAction) () -> clazz.getDeclaredMethod(name, types));
            } catch (PrivilegedActionException e) {
                if (e.getCause() instanceof NoSuchMethodException) {
                    throw (NoSuchMethodException) e.getCause();
                }

                throw new RuntimeException(e.getCause());
            }
        }
    }

    /**
     * Set accessible for {@code ao} via {@link SecurityManager}.
     *
     * @param ao         target object
     * @param accessible if true set accessible true, else false
     */
    static void setAccessible(final AccessibleObject ao,
                              final boolean accessible) {
        if (System.getSecurityManager() == null) {
            ao.setAccessible(accessible);
        } else {
            AccessController.doPrivileged((PrivilegedAction) () -> {
                ao.setAccessible(accessible);
                return null;
            });
        }
    }

}
