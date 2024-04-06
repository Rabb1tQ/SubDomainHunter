package com.rabbitq.models.impl;

import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SubDomainInterfaceImplementation
public class Rapiddns implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        String targetURL=targetOptionsEntity.getDomain();
        Set<String> setResult =new HashSet<>();
        Document document = null;
        try {
            document = Jsoup.connect("https://rapiddns.io/subdomain/" + targetURL + "?full=1").get();
            setResult=solvePage(document);
            System.out.println("\033[32m[*]\033[0m通过Rapiddns接口获取完成" + "，共获取到" + setResult.size() + "子域");
        } catch (IOException e) {
            System.out.println("\033[31mrapiddns获取失败，原因：" + e);
        }

/*
        int intPageSize = Integer.parseInt(document.select(".page-item").get(8).text());

        //处理后续页
        for (int i = 2; i <= intPageSize; i++) {
            String strTableURL = "https://rapiddns.io/s/" + targetURL + "?page=" + i;
            Document tempDocument = Jsoup.connect(strTableURL).get();
            setResult.addAll(solvePage(tempDocument));
            System.out.println("正在请求第" + i + "页");
        }
*/

        return setResult;
    }

    public Set<String> solvePage(Document targetDocument) {
        Elements tempElements = targetDocument.select("#table").select("tr");
        Set<String> setResult = new HashSet<>();
        for (int j = 1; j <= tempElements.size(); j++) {
            try {
                String strTempSubDomain = tempElements.get(j).select("td").get(0).text();
                setResult.add(strTempSubDomain);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        return setResult;
    }

}
