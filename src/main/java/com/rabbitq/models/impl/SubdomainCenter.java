package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;

import java.util.HashSet;
import java.util.Set;

@SubDomainInterfaceImplementation
public class SubdomainCenter implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        Set<String> subDomains = new HashSet<>();
        String source="SubdomainCenter";
        String targetURL = targetOptionsEntity.getDomain();
        String strAPI = "https://api.subdomain.center/?domain=" + targetURL;

        try {
            while (true) {
                // 发送 GET 请求
                String result = HttpRequest.get(strAPI)
                        .timeout(10000) // 设置 10 秒超时
                        .execute()
                        .body();

                // 解析 JSON 响应
                JSONArray subdomains = JSON.parseArray(result);

                // 如果返回空数组，停止请求
                if (subdomains == null || subdomains.isEmpty()) {
                    PrintUtils.sucess("Subdomain.center API 返回空列表，停止请求");
                    break;
                }

                // 提取子域名
                for (int i = 0; i < subdomains.size(); i++) {
                    String subdomain = subdomains.getString(i);
                    if (subdomain != null && subdomain.contains(targetURL)) {
                        subDomains.add(subdomain);
                    }
                }
            }

            PrintUtils.sucess(source,subDomains.size());
        } catch (Exception e) {
            PrintUtils.error(source, e.getMessage(),subDomains);
        }

        return subDomains;
    }
}