package com.rabbitq.models.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.NetworkUtils;
import com.rabbitq.util.PrintUtils;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

@SubDomainInterfaceImplementation
public class AlienVault implements SubDomainInterface {

    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity){
        String targetURL=targetOptionsEntity.getDomain();
        String strAPI="https://otx.alienvault.com/api/v1/indicators/domain/"+targetURL+"/passive_dns";
        Set<String> subDomains=new HashSet<>();
        String source="AlienVaul";

        try {
            NetworkUtils.truestCert();
            String result1= HttpRequest.get(strAPI).execute().body();
            JSONObject jsonObject=JSONObject.parseObject(result1);
            JSONArray jsonArray= (JSONArray) jsonObject.get("passive_dns");
            for(int i=0;i<jsonArray.size();i++) {
                //System.out.println();
                subDomains.add(String.valueOf(jsonArray.getJSONObject(i).get("hostname")));
            }
            PrintUtils.sucess(source,subDomains.size());
        }
        catch (Exception e){
            PrintUtils.error(source, e.getMessage(),subDomains);
        }

        return subDomains;
    }
}
