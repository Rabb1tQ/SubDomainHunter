package com.rabbitq.models.impl;

import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.NetworkUtils;
import com.rabbitq.util.PrintUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

@SubDomainInterfaceImplementation
public class DomainGlass implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) throws Exception {
        String targetURL = targetOptionsEntity.getDomain();
        String source = "DomainGlass";
        Set<String> subDomains = new HashSet<>();

        try {
            NetworkUtils.truestCert();
            Document document = Jsoup.connect("https://domain.glass/" + targetURL).get();
            Elements tables = document.select("table");


            for (Element table : tables) {
                if (table.text().contains("Subdomain") && table.text().contains("Cisco Umbrella DNS Rank") && table.text().contains("Majestic Rank")) {
                    Elements links = table.select("a");
                    for (Element link : links) {
                        subDomains.add(link.attr("title"));
                    }
                }
            }
            PrintUtils.sucess(source, subDomains.size());
        } catch (Exception e) {
            PrintUtils.error(source, e.getMessage(), subDomains);
        }


        return subDomains;
    }


}
