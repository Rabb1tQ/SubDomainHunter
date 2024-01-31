package com.rabbitq.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TCPThreadCallable implements Callable<List<String>> {

    //目标端口
    private String[] burteDomainDic;

    // 最大启动线程数
    static int numOfThreads;


    public TCPThreadCallable(String name, String[] burteDomainDic) {
        this.burteDomainDic = burteDomainDic;

    }
    /**
     * 端口扫描，按照分好的端口列表进行扫描
     */
    private List<String> bruteSubdomain(String[] domainDic) {
        List<String> list=new ArrayList<>();
        for (int i = 0; i < domainDic.length; i++) {
            String domain = domainDic[i];
            try {
                InetAddress address = InetAddress.getByName(domain);
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }
            list.add(domain);
        }
        return list;
    }


    @Override
    public List<String> call() throws Exception {
        return bruteSubdomain(burteDomainDic);
    }
}