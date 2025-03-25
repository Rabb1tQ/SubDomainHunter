package com.rabbitq.models.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;

@SubDomainInterfaceImplementation
public class CrtShSubDomain implements SubDomainInterface {
    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        Set<String> subDomains = new HashSet<>();
        String domain = targetOptionsEntity.getDomain();
        try {
            String strAPI = "https://crt.sh/?q=" + domain + "&output=json";
            String result1= HttpRequest.get(strAPI).execute().body();
            JSONArray jsonArray=JSONArray.parseArray(result1);
            if (jsonArray == null || jsonArray.isEmpty()) {
                System.out.println("解析结果为空");
                return Collections.emptySet();
            }

            // 3. 存储子域名的集合（去重）
            Set<String> subdomains = new HashSet<>();

            for (int i=0;i<jsonArray.size();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String nameValue = jsonObject.getString("name_value"); // 证书中的域名信息
                if (nameValue != null) {
                    // 处理换行符（name_value可能包含多个域名）
                    String[] domains = nameValue.split("\n");
                    for (String domainName : domains) {
                        domainName = domainName.trim();
                        // 处理通配符域名
                        if (domainName.startsWith("*")) {
                            // 去掉前面的 * 和点（如果有），但保留子域结构
                            String cleanedDomain = domainName.replaceFirst("^\\*\\.", "");
                            // 只收集与目标域名相关的域名
                            if (cleanedDomain.endsWith("." + domain) || cleanedDomain.equals(domain)) {
                                subdomains.add(cleanedDomain);
                            }
                        } else {
                            // 非通配符域名，直接判断
                            if (domainName.endsWith("." + domain) || domainName.equals(domain)) {
                                subdomains.add(domainName);
                            }
                        }
                    }
                }
            }

            System.out.println("找到的子域名数量: " + subdomains.size());

        } catch (Exception e) {
            System.err.println("Error while fetching from crt.sh: " + e.getMessage());
        }
        return subDomains;
    }
}