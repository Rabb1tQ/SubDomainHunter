package com.rabbitq.models.impl;

import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;
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
        String targetURL = targetOptionsEntity.getDomain();
        Set<String> subDomains = new HashSet<>();
        String source = "Rapiddns";
        Document document = null;
        try {
            document = Jsoup.connect("https://rapiddns.io/subdomain/" + targetURL + "?full=1").get();
            Elements tempElements = document.select("#table").select("tr");
            for (int j = 1; j <= tempElements.size(); j++) {
                try {
                    String strTempSubDomain = tempElements.get(j).select("td").get(0).text();
                    subDomains.add(strTempSubDomain);
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            PrintUtils.sucess(source, subDomains.size());
        } catch (IOException e) {
            PrintUtils.error(source, e.getMessage(), subDomains);
        }
        return subDomains;
    }


}
