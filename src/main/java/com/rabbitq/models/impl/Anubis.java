package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SubDomainInterfaceImplementation
public class Anubis implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        Set<String> setResult=new HashSet<>();
        try {
            String targetURL=targetOptionsEntity.getDomain();
//        String proxyHost = "192.168.131.1";
//        int proxyPort = 7890;
//
//        // 创建代理服务器对象
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

//        String result1= HttpRequest.get(strAPI).setProxy(proxy).execute().body();
            String strAPI = "https://jonlu.ca/anubis/subdomains/";

            String result1 = HttpRequest.get(strAPI + targetURL).execute().body();
            result1=result1.replaceAll("\"","");
            String[] arrResult = result1.substring(1, result1.length() - 1).split(",");
            setResult=Arrays.stream(arrResult).collect(Collectors.toSet());
            System.out.println("\033[32m[*]\033[0m通过anubis接口获取完成" + "，共获取到" + setResult.size() + "子域");
        }catch (Exception e){
            System.out.println("\033[31msitedossier获取失败，原因：" + e);
        }

        return setResult;
    }

}
