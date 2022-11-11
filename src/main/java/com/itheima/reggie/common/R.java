package com.itheima.reggie.common;

import lombok.Data;

import java.util.HashMap;

@Data
public class R<T> {
    private Integer code; // 状态码 0 失败 1 成功
    private String msg; // 返回前端的提示信息
    private T data; // 返回的数据
    private HashMap map = new HashMap(); // 其他数据

    // 成功时
    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.code = 1;
        r.data = object;
        return r;
    }

    // 失败
    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.code = 0;
        r.msg = msg;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
