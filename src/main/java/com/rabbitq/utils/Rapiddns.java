package com.rabbitq.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Rapiddns {
    //https://rapiddns.io/s/baidu.com?page=2
    public Set<String> getRapidSubDomain(String targetURL) throws IOException {
        Document document = Jsoup.connect("https://rapiddns.io/subdomain/" + targetURL + "?full=1").get();

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
        return new HashSet<>(solvePage(document));
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
