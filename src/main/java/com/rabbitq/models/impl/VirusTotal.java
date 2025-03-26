package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;

import java.util.*;

import static com.rabbitq.util.GlobalConfig.globalConfig;

@SubDomainInterfaceImplementation
public class VirusTotal implements SubDomainInterface {
    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        String strVirusTotalKey;
        Set<String> subDomains = new HashSet<>();
        String source = "VirusTotal";
        String targetURL = targetOptionsEntity.getDomain();

        Object objVirusTotalKey = globalConfig.get("VirusTotal");
        if (objVirusTotalKey == null || ((String) objVirusTotalKey).isEmpty()) {
            PrintUtils.error("未找到VirusTotal API秘钥");
            return subDomains;
        } else {
            strVirusTotalKey = (String) objVirusTotalKey;
        }

        String strAPI = "https://www.virustotal.com/vtapi/v2/domain/report?domain=" + targetURL + "&apikey=" + strVirusTotalKey;


        try {

            String result = HttpRequest.get(strAPI).execute().body();
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null) {
                if (jsonObject.get("domain_siblings") != null) {
                    List<String> arrayDomainSiblings = (List<String>) jsonObject.get("domain_siblings");
                    subDomains.addAll(arrayDomainSiblings);
                }
                if (jsonObject.get("subdomains") != null) {
                    List<String> arrayDomainSiblings = (List<String>) jsonObject.get("subdomains");
                    subDomains.addAll(arrayDomainSiblings);
                }
            }
            PrintUtils.sucess(source, subDomains.size());

        } catch (Exception e) {
            PrintUtils.error(source, e.getMessage(), subDomains);

        }
        return subDomains;
    }
}

