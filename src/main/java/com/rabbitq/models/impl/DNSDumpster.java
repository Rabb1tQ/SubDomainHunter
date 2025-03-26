package com.rabbitq.models.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.GlobalConfig;
import com.rabbitq.util.PrintUtils;
import org.jsoup.Jsoup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.rabbitq.util.GlobalConfig.globalConfig;

@SubDomainInterfaceImplementation
public class DNSDumpster implements SubDomainInterface {


    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        String API_KEY;
        Set<String> subDomains = new HashSet<>();
        String source = "DNSDumpster";
        String targetURL = targetOptionsEntity.getDomain();
        String strAPI = "https://api.dnsdumpster.com/domain/" + targetURL;


        Object objVirusTotalKey = globalConfig.get("DNSDumpster");

        if (objVirusTotalKey == null || ((String) objVirusTotalKey).isEmpty()) {
            PrintUtils.error("未找到DNSDumpster API秘钥");
            return subDomains;
        } else {
            API_KEY = (String) objVirusTotalKey;
        }
        try {
            // 使用 Jsoup 发送 GET 请求，携带 API 密钥
            String result = Jsoup.connect(strAPI)
                    .header("X-API-Key", API_KEY)
                    .ignoreContentType(true)
                    .get()
                    .body()
                    .text();

            // 使用 Fastjson 解析 JSON 响应
            JSONObject jsonObject = JSON.parseObject(result);

            // 提取 A 记录中的子域名
            JSONArray aRecords = jsonObject.getJSONArray("a");
            if (aRecords != null) {
                for (int i = 0; i < aRecords.size(); i++) {
                    JSONObject record = aRecords.getJSONObject(i);
                    String subdomain = record.getString("host");
                    if (subdomain != null && subdomain.contains(targetURL)) {
                        subDomains.add(subdomain);
                    }
                }
            }

            // 提取 MX 记录中的子域名
            JSONArray mxRecords = jsonObject.getJSONArray("mx");
            if (mxRecords != null) {
                for (int i = 0; i < mxRecords.size(); i++) {
                    JSONObject record = mxRecords.getJSONObject(i);
                    String subdomain = record.getString("host");
                    if (subdomain != null && subdomain.contains(targetURL)) {
                        subDomains.add(subdomain);
                    }
                }
            }

            // 提取 NS 记录中的子域名
            JSONArray nsRecords = jsonObject.getJSONArray("ns");
            if (nsRecords != null) {
                for (int i = 0; i < nsRecords.size(); i++) {
                    JSONObject record = nsRecords.getJSONObject(i);
                    String subdomain = record.getString("host");
                    if (subdomain != null && subdomain.contains(targetURL)) {
                        subDomains.add(subdomain);
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