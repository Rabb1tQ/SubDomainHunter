package com.rabbitq.models.impl;


import cn.hutool.http.HttpUtil;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;

import java.util.HashSet;
import java.util.Set;

@SubDomainInterfaceImplementation
public class HackerTargetSubDomain implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        Set<String> subDomains = new HashSet<>();
        String source = "HackerTarget";
        String targetURL = targetOptionsEntity.getDomain();
        String url = "https://api.hackertarget.com/hostsearch/?q=" + targetURL;

        try {

            // 1. 发送请求
            String response = HttpUtil.get(url);
            if (response == null || response.isEmpty()) {
                PrintUtils.error("hackertarget接口请求失败或返回为空");
                return subDomains;
            }

            // 2. 解析返回的文本数据

            String[] lines = response.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // 分割 subdomain 和 IP
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        String subdomain = parts[0].trim();
                        // 只添加与目标域名相关的子域名，排除主域名本身
                        if (subdomain.endsWith("." + targetURL) && !subdomain.equals(targetURL)) {
                            subDomains.add(subdomain);
                        }
                    }
                }
            }
            PrintUtils.sucess(source, subDomains.size());
        } catch (Exception e) {
            PrintUtils.error(source, e.getMessage(), subDomains);
        }
        return subDomains;
    }
}
