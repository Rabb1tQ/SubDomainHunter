package com.rabbitq.models.impl;

import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;
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
        Set<String> subDomains =new HashSet<>();
        String source="SiteDossier";
        try {
            subDomains = getSiteDossierSubDomain("http://www.sitedossier.com/parentdomain/"+targetURL);
            PrintUtils.sucess(source,subDomains.size());
        } catch (IOException | InterruptedException e) {
            PrintUtils.error(source, e.getMessage(),subDomains);
        }
        return subDomains;
    }

    public Set<String> getSiteDossierSubDomain(String targetURL) throws IOException, InterruptedException {
        Set<String> subDomains = new HashSet<>();

        String tempSubDomain="";

        Document doc = Jsoup.connect(targetURL).get();
        for (int i=0;i<doc.select("ol li").size();i++){
            tempSubDomain=doc.select("ol li").get(i).text().replace("http://","");
            try {
                subDomains.add(tempSubDomain.substring(0, tempSubDomain.length() - 1));
            }
            catch (Exception e){
                PrintUtils.error(tempSubDomain,e.getMessage());
            }

        }
        if(!doc.select("ol+a").isEmpty()){
            Thread.sleep(3000);
            String nextTargetURL="http://www.sitedossier.com"+doc.select("ol+a").attr("href");
            subDomains.addAll(getSiteDossierSubDomain(nextTargetURL));
        }


        return subDomains;
    }
}
