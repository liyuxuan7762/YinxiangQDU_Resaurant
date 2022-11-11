package com.itheima.reggie.common;

/**
 * 这个工具类用来封装ThreadLocal的get和put方法
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void put(Long id) {
        threadLocal.set(id);
    }

    public static Long get() {
        return threadLocal.get();
    }
}
