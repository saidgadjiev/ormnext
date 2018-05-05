package ru.saidgadjiev.proxymaker;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class SecurityHelper {

    private SecurityHelper() {

    }

    static Method getDeclaredMethod(final Class clazz, final String name,
                                    final Class[] types) throws NoSuchMethodException {
        if (System.getSecurityManager() == null)
            return clazz.getDeclaredMethod(name, types);
        else {
            try {
                return (Method) AccessController
                        .doPrivileged(new PrivilegedExceptionAction() {
                            public Object run() throws Exception {
                                return clazz.getDeclaredMethod(name, types);
                            }
                        });
            }
            catch (PrivilegedActionException e) {
                if (e.getCause() instanceof NoSuchMethodException)
                    throw (NoSuchMethodException) e.getCause();

                throw new RuntimeException(e.getCause());
            }
        }
    }

    static void setAccessible(final AccessibleObject ao,
                              final boolean accessible) {
        if (System.getSecurityManager() == null)
            ao.setAccessible(accessible);
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    ao.setAccessible(accessible);
                    return null;
                }
            });
        }
    }

}
