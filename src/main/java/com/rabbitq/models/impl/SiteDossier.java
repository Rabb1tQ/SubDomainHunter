package com.rabbitq.models.impl;

import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SubDomainInterfaceImplementation
public class SiteDossier implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity){
        String targetURL=targetOptionsEntity.getDomain();
        Set<String> setResult =new HashSet<>();
        try {
            setResult = getSiteDossierSubDomain("http://www.sitedossier.com/parentdomain/"+targetURL);
            System.out.println("\033[32m[*]\033[0m通过sitedossier接口获取完成" + "，共获取到" + setResult.size() + "子域");
        } catch (IOException | InterruptedException e) {
            System.out.println("\033[31msitedossier获取失败，原因：" + e);
        }

        return setResult;
    }

    public Set<String> getSiteDossierSubDomain(String targetURL) throws IOException, InterruptedException {
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
            setResult.addAll(getSiteDossierSubDomain(nextTargetURL));

        }


        return setResult;
    }
}
