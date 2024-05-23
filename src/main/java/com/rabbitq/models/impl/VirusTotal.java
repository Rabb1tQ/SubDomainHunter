package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;

import java.util.*;

import static com.rabbitq.util.GlobalConfig.globalConfig;

@SubDomainInterfaceImplementation
public class VirusTotal implements SubDomainInterface {
    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        String strVirusTotalKey;
        if (globalConfig != null) {
            Object objVirusTotalKey = globalConfig.get("VirusTotal");
            if (objVirusTotalKey == null || ((String) objVirusTotalKey).isEmpty()) {
                return Collections.emptySet();
            } else {
                strVirusTotalKey = (String) objVirusTotalKey;
            }
        } else {
            return Collections.emptySet();
        }
        Set<String> setResult = new HashSet<>();
        try {
            String targetURL = targetOptionsEntity.getDomain();
            String strAPI = "https://www.virustotal.com/vtapi/v2/domain/report?domain=" + targetURL + "&apikey=" + strVirusTotalKey;
            String result = HttpRequest.get(strAPI).execute().body();
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null) {
                if (jsonObject.get("domain_siblings") != null) {
                    List<String> arrayDomainSiblings = (List<String>) jsonObject.get("domain_siblings");
                    setResult.addAll(arrayDomainSiblings);
                }
                if (jsonObject.get("subdomains") != null) {
                    List<String> arrayDomainSiblings = (List<String>) jsonObject.get("subdomains");
                    setResult.addAll(arrayDomainSiblings);
                }
            }
            System.out.println("\033[32m[*]\033[0m通过VirusTotal接口获取完成" + "，共获取到" + setResult.size() + "子域");
        } catch (Exception e) {
            System.out.println("\033[31mVirusTotal接口获取失败，原因：" + e);
        }
        return setResult;
    }
}

