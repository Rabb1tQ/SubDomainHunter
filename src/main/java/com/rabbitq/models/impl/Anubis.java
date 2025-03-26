package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SubDomainInterfaceImplementation
public class Anubis implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        Set<String> subDomains=new HashSet<>();
        String source="Anubis";
        String targetURL=targetOptionsEntity.getDomain();
        String strAPI = "https://jldc.me/anubis/subdomains/";

        try {
            String result1 = HttpRequest.get(strAPI + targetURL).execute().body();
            result1=result1.replaceAll("\"","");
            String[] arrResult = result1.substring(1, result1.length() - 1).split(",");
            subDomains=Arrays.stream(arrResult).collect(Collectors.toSet());
            PrintUtils.sucess(source,subDomains.size());
        }catch (Exception e){
            PrintUtils.error(source, e.getMessage(),subDomains);
        }

        return subDomains;
    }

}
