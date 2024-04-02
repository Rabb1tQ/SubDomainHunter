package com.rabbitq.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

public class AlienVault {


    public Set<String> getSiteAlienVaultSubDomain(String targetURL) throws NoSuchAlgorithmException, KeyManagementException {
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
        Set<String> setResult=new HashSet<>();
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//        String result1= HttpRequest.get(strAPI).setProxy(proxy).execute().body();
        String result1= HttpRequest.get(strAPI).execute().body();
        JSONObject jsonObject=JSONObject.parseObject(result1);
        JSONArray jsonArray= (JSONArray) jsonObject.get("passive_dns");
        for(int i=0;i<jsonArray.size();i++) {
            //System.out.println();
            setResult.add(String.valueOf(jsonArray.getJSONObject(i).get("hostname")));
        }
        return setResult;
    }
}
