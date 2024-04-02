package com.rabbitq.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashSet;
import java.util.Set;

public class SiteDossier {

    public Set<String> getSiteDossierSubDomain(String targetURL){
        Set<String> setResult= null;
        try {
            setResult = getSubDomain("http://www.sitedossier.com/parentdomain/"+targetURL);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return setResult;
    }

    public Set<String> getSubDomain(String targetURL) throws IOException, InterruptedException {
        Set<String> setResult = new HashSet<>();
//        String proxyHost = "192.168.131.1";
//        int proxyPort = 7890;
//
//        // 创建代理服务器对象
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

        String tempSubDomain="";
        // 使用代理连接到目标网站
        //Document doc = Jsoup.connect(targetURL).proxy(proxy).get();

        Document doc = Jsoup.connect(targetURL).get();
        for (int i=0;i<doc.select("ol li").size();i++){
            tempSubDomain=doc.select("ol li").get(i).text().replace("http://","");
            try {
                setResult.add(tempSubDomain.substring(0, tempSubDomain.length() - 1));
            }
            catch (Exception e){
                System.out.println(tempSubDomain);
            }

        }
        //System.out.println(targetURL);
        if(!doc.select("ol+a").isEmpty()){
            Thread.sleep(3000);
            String nextTargetURL="http://www.sitedossier.com"+doc.select("ol+a").attr("href");
            setResult.addAll(getSubDomain(nextTargetURL));

        }


        return setResult;
    }
}
