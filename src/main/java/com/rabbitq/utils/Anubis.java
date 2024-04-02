package com.rabbitq.utils;

import cn.hutool.http.HttpRequest;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Anubis {

    public Set<String> getRapidSubDomain(String strAPI) {
//        String proxyHost = "192.168.131.1";
//        int proxyPort = 7890;
//
//        // 创建代理服务器对象
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

//        String result1= HttpRequest.get(strAPI).setProxy(proxy).execute().body();
        String result1= HttpRequest.get(strAPI).execute().body();
        String[] arrResult= result1.substring(1, result1.length() - 1).split(",");
        return Arrays.stream(arrResult).collect(Collectors.toSet());
    }
}
