package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，用来代替try catch
 */
// 拦截RestController 和Controller里面的所有方法 通过AOP实现给所有方法通过代理方式添加try catch
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody // 需要返回Json数据
@Slf4j
public class GlobalExceptionHandler {

    // 创建拦截违反SQL唯一性约束异常的处理方法
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) // 标明这个方法用来处理这个异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        String message = exception.getMessage();
        // 判断errorMsg中是否包含duplicate keys信息 如果是那么就可以确定是违反唯一性约束异常
        if(message.contains("Duplicate entry")) {
            String duplicateKey = message.split(" ")[2];
            return R.error( duplicateKey + "已存在");
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomerException.class) // 标明这个方法用来处理这个异常
    public R<String> exceptionHandler(CustomerException exception) {
        String message = exception.getMessage();
        // 判断errorMsg中是否包含duplicate keys信息 如果是那么就可以确定是违反唯一性约束异常
        return R.error(exception.getMessage());
    }

    @ExceptionHandler(Exception.class) // 标明这个方法用来处理这个异常
    public R<String> exceptionHandler(Exception exception) {
        log.info(exception.getMessage());
        return R.error("未知错误");
    }
}
