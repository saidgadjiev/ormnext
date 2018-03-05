package ru.saidgadjiev.orm.next.core.criteria;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();

        Set<String> proxySet = (Set<String>) Proxy.newProxyInstance(set.getClass().getClassLoader(), new Class[]{Set.class}, (proxy, method, args1) -> {
            System.out.println("Invoked " + method + " args = " + Arrays.toString(args1));

            return method.invoke(set, args1);
        });

        proxySet.add("tes");
    }
}
