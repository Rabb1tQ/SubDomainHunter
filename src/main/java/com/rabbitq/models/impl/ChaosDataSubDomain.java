package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SubDomainInterfaceImplementation
public class ChaosDataSubDomain implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        Set<String> subDomains = new HashSet<>();
        String source="ChaosData";
        String targetDomain = targetOptionsEntity.getDomain();
        String indexUrl = "https://chaos-data.projectdiscovery.io/index.json";

        try {
            // 1. 获取并解析 index.json
            String indexJson = HttpUtil.get(indexUrl);
            if (indexJson == null || indexJson.isEmpty()) {
                System.out.println("无法获取 index.json");
                return subDomains;
            }

            JSONArray indexArray = JSONArray.parseArray(indexJson);
            if (indexArray == null || indexArray.isEmpty()) {
                System.out.println("index.json 为空");
                return subDomains;
            }

            // 2. 遍历并过滤 ZIP 文件

            for (Object obj : indexArray) {
                JSONObject entry = (JSONObject) obj;
                String name = entry.getString("name").toLowerCase();
                String programUrl = entry.getString("program_url").toLowerCase();
                String zipUrl = entry.getString("URL");

                // 过滤条件：program_url 或 name 包含目标域名的一部分
                String domainKey = targetDomain.split("\\.")[0].toLowerCase(); // 提取 "baidu"
                if (!programUrl.contains(domainKey) && !name.contains(domainKey)) {
                    continue; // 跳过无关的 ZIP 文件
                }

                System.out.println("处理 ZIP 文件: " + name + " (" + zipUrl + ")");

                // 3. 在内存中处理 ZIP 文件
                try (InputStream inputStream = HttpUtil.createGet(zipUrl).timeout(30000).execute().bodyStream();
                     ZipInputStream zis = new ZipInputStream(inputStream)) {
                    ZipEntry zipEntry;
                    while ((zipEntry = zis.getNextEntry()) != null) {
                        if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".txt")) {
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(zis, StandardCharsets.UTF_8));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.endsWith("." + targetDomain) && !line.equals(targetDomain)) {
                                    subDomains.add(line);
                                }
                            }
                        }
                        zis.closeEntry();
                    }
                }
                catch (Exception e) {
                    PrintUtils.error("ChaosData接口地址：" +zipUrl + "处理错误，原因："+ e.getMessage());
                }
            }
            PrintUtils.sucess(source, subDomains.size());
        } catch (Exception e) {
            PrintUtils.error(source, e.getMessage(),subDomains);

        }
        return subDomains;
    }
}
