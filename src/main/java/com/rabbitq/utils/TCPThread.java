package com.rabbitq.utils;

import java.net.*;
import java.util.List;
import java.util.Map;

public class TCPThread extends Thread {

    //目标端口
    private String[] burteDomainDic;
    static String dnsServer;

    // 最大启动线程数
    static int numOfThreads;


    public TCPThread(String name, String[] burteDomainDic) {
        super(name);
        this.burteDomainDic = burteDomainDic;

    }

    /**
     * 运行方法
     */
    @Override
    public void run() {
        try {
            bruteSubdomain(burteDomainDic);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 端口扫描，按照分好的端口列表进行扫描
     */
    private void bruteSubdomain(String[] domainDic) throws UnknownHostException {
        for (int i = 0; i < domainDic.length; i++) {
            String domain = domainDic[i];
            try {
                // 指定DNS服务器
                InetSocketAddress dnsSocketAddress = new InetSocketAddress(dnsServer, 53);

                // 创建Socket并连接到DNS服务器
                Socket socket = new Socket();
                socket.connect(dnsSocketAddress);

                // 解析主机名的IP地址
                InetAddress[] addresses = InetAddress.getAllByName(domain);
                if (addresses.length==0){
                    throw new Exception("解析错误");
                }
                socket.close();
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }
            System.out.println(domain);
        }
    }

    @Override
    public String toString() {
        return "ThreadName:" + this.getName();
    }
}