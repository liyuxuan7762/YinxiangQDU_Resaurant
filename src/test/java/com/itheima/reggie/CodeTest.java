package com.itheima.reggie;

import java.util.Random;

public class CodeTest {
    public static Random randObj = new Random();

    // 生成6位随机验证码
    public static String generateCode() {
        return Integer.toString(100000 + randObj.nextInt(900000));
    }

    // 生成4位随机验证码
    public static String generateCode4() {
        return Integer.toString(1000 + randObj.nextInt(9000));
    }


    public static void main(String[] args) {
        System.out.println(generateCode4());
    }
}
