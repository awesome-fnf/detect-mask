package com.alibaba.viapi.function.demo.util;

/**
 * @author benxiang.hhq
 */
public class SystemUtils {

    public static String getStringEnvValue(String name , String defaultValue) {
        String value = System.getenv(name);
        return value ==null?defaultValue:value;
    }

    public static Long getLongEnvValue(String name , Long defaultValue) {
        String value = System.getenv(name);
        return value == null? defaultValue:Long.valueOf(value);
    }

}
