package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SubDomainInterfaceImplementation
public class DomainGlass implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) throws Exception {
        String targetURL=targetOptionsEntity.getDomain();
        Set<String> setResult =new HashSet<>();
        Document document = null;
        try {

            // 信任所有证书的代码
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            document = Jsoup.connect("https://domain.glass/" + targetURL).get();
            setResult=solvePage(document);
            System.out.println("\033[32m[*]\033[0m通过DomainGlass接口获取完成" + "，共获取到" + setResult.size() + "子域");
        } catch (IOException e) {
            System.out.println("\033[31mDomainGlass获取失败，原因：" + e);
        }


        return setResult;
    }

    private Set<String> solvePage(Document document) {
        Elements tables =document.select("table");

        Set<String> setResult = new HashSet<>();

        for (Element table : tables) {
            if (table.text().contains("Subdomain") && table.text().contains("Cisco Umbrella DNS Rank") && table.text().contains("Majestic Rank")) {
                Elements links = table.select("a");
                for (Element link : links) {
                    setResult.add(link.attr("title"));
//                    System.out.println(links.text());
                }
            }
        }


        return setResult;
    }
}
