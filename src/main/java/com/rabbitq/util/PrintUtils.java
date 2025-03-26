package com.rabbitq.util;

import java.util.Set;

public class PrintUtils {

    // ANSI 颜色代码
    private static final String GREEN = "\033[32m";
    private static final String RED = "\033[31m";
    private static final String RESET = "\033[0m";

    /**
     * 打印成功信息（绿色）
     * @param message 信息内容
     */
    public static void sucess(String message) {
        System.out.println(GREEN + "[*] " + message + RESET);
    }

    /**
     * 打印成功信息，带子域名数量（绿色）
     * @param source 来源（如 "DNSDumpster API"）
     * @param subdomainCount 子域名数量
     */
    public static void sucess(String source, int subdomainCount) {
        System.out.println(GREEN + "[*] 通过 " + source + " 获取完成，共获取到 " + subdomainCount + " 个子域" + RESET);
    }

    /**
     * 打印错误信息（红色）
     * @param message 信息内容
     */
    public static void error(String message) {
        System.out.println(RED + message + RESET);
    }

    /**
     * 打印错误信息，带来源和原因（红色）
     * @param source 来源（如 "DNSDumpster API"）
     * @param reason 错误原因
     */
    public static void error(String source, String reason) {
        error(source + " 获取失败，原因：" + reason + RESET);
    }

    /**
     * 打印错误信息，中途处理错误但已获得部分子域
     * @param source
     * @param reason
     * @param subDomains
     */
    public static void error(String source, String reason, Set<String> subDomains) {
        if(subDomains.isEmpty()){
            error(source,reason);

        }else {
            error(source,reason);
            PrintUtils.sucess("通过"+source+"接口获取完成，获取子域数量:"+subDomains.size());
        }
    }
}