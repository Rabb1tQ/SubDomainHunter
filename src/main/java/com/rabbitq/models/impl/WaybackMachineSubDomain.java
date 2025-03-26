package com.rabbitq.models.impl;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SubDomainInterfaceImplementation
public class WaybackMachineSubDomain implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity){
        Set<String> subDomains = new HashSet<>();
        String source="WaybackMachine";
        String domain = targetOptionsEntity.getDomain(); // 替换为你的目标域名
        String url = "http://web.archive.org/cdx/search/cdx?url=*." + domain + "&output=json&fl=original";

        try {
            // 1. 发送请求
            String jsonResponse = HttpUtil.get(url);
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                PrintUtils.error("请求失败或返回为空");
                return subDomains;
            }

            // 2. 解析JSON
            JSONArray jsonArray = JSONArray.parseArray(jsonResponse);
            if (jsonArray == null || jsonArray.size() <= 1) { // 第一个元素是字段名
                PrintUtils.error("未找到历史记录");
                return subDomains;
            }

            // 3. 提取子域名

            Pattern subdomainPattern = Pattern.compile("^(?:https?://)?([a-zA-Z0-9.-]+\\." + Pattern.quote(domain) + ")(?::\\d+)?/?");
            // 从第二行开始遍历（跳过字段名）
            for (int i = 1; i < jsonArray.size(); i++) {
                JSONArray entry = jsonArray.getJSONArray(i);
                if (entry != null && !entry.isEmpty()) {
                    String originalUrl = entry.getString(0); // 获取数组中的第一个元素（URL）
                    Matcher matcher = subdomainPattern.matcher(originalUrl);
                    if (matcher.find()) {
                        String subdomain = matcher.group(1); // 提取域名部分，去掉协议和端口
                        if (!subdomain.equals(domain)) { // 排除主域名
                            subDomains.add(subdomain);
                        }
                    }
                }
            }
            PrintUtils.sucess(source,subDomains.size());

        }
        catch (Exception e) {
            PrintUtils.error(source, e.getMessage(),subDomains);
        }
        return subDomains;
    }
}
