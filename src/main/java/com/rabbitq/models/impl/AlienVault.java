package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

@SubDomainInterfaceImplementation
public class AlienVault implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity){
        Set<String> setResult=new HashSet<>();
        try {
            String targetURL=targetOptionsEntity.getDomain();
            String strAPI="https://otx.alienvault.com/api/v1/indicators/domain/"+targetURL+"/passive_dns";
//        String proxyHost = "192.168.131.1";
//        int proxyPort = 7890;
//
//        // 创建代理服务器对象
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            AlienVault alienVault=new AlienVault();
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        }
                    }
            };

            // 安装信任所有证书的TrustManager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 创建一个忽略主机名验证的HostnameVerifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true; // 忽略主机名验证
                }
            };
            // 安装HostnameVerifier

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//        String result1= HttpRequest.get(strAPI).setProxy(proxy).execute().body();
            String result1= HttpRequest.get(strAPI).execute().body();
            JSONObject jsonObject=JSONObject.parseObject(result1);
            JSONArray jsonArray= (JSONArray) jsonObject.get("passive_dns");
            for(int i=0;i<jsonArray.size();i++) {
                //System.out.println();
                setResult.add(String.valueOf(jsonArray.getJSONObject(i).get("hostname")));
            }
            System.out.println("\033[32m[*]\033[0m通过AlienVaul接口获取完成" + "，共获取到" + setResult.size() + "子域");
        }
        catch (Exception e){
            System.out.println("\033[31mAlienVaul接口获取失败，原因：" + e);
        }

        return setResult;
    }
}
