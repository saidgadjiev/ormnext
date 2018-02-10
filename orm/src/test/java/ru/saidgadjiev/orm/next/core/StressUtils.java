package ru.saidgadjiev.orm.next.core;

import java.util.concurrent.Callable;

/**
 * Created by said on 10.02.2018.
 */
public class StressUtils {

    private StressUtils() {

    }

    public static long stress(Callable<Void> callable, int times) throws Exception {
        long start = System.currentTimeMillis();

        for (int i = 0; i < times; ++i) {
            callable.call();
        }

        long end = System.currentTimeMillis();

        return end - start;
    }
}
