package com.rabbitq.utils;


import com.rabbitq.entity.TargetOptionsEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadConf {
    public void bruteAction(TargetOptionsEntity targetOptionsEntity) {
        String bruteDomain=targetOptionsEntity.getDomain();
        String nameServer=targetOptionsEntity.getNameServer();
        if(nameServer==null||nameServer.equals("")){
            nameServer="8.8.8.8";
        }
        TCPThread.dnsServer=nameServer;
        int numOfThreads=targetOptionsEntity.getNumOfThreads();
        //设置目标地址
        List<String> subDomainList = SubDic(bruteDomain);

        System.out.println("\033[32m[*]\033[m最大启动线程数为"+ numOfThreads);
        int subDomainSize = subDomainList.size();
        if(subDomainSize<numOfThreads){
            numOfThreads=subDomainSize;
        }
        List<List<String>> burteSubdomain= averageAssign(subDomainList,numOfThreads);
        ThreadPoolExecutor myExecutor = new ThreadPoolExecutor(numOfThreads, numOfThreads, 200, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>());
        TCPThread.numOfThreads = numOfThreads;
        System.out.println("\033[32m[*]\033[0m存活主机：");
        for (int i = 0; i < numOfThreads; i++){
            ArrayList<String> castedList = new ArrayList<>(burteSubdomain.get(i));
            //String[] array = castedList.toArray(new Integer[castedList.size()]);
            String[] array = castedList.toArray(new String[castedList.size()]);
            myExecutor.submit(new TCPThread("T" + i, array));

        }

        myExecutor.shutdown();
    }
    public List<String> SubDic(String bruteDomain) {
        List<String> subDomainList = new ArrayList<>();
        File file = new File(System.getProperty("user.dir") + "/SubDic");
        Scanner scanner = null;
        int count=0;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine()+"."+bruteDomain;
                subDomainList.add(line);
            }
            scanner.close();
            count++;
        } catch (FileNotFoundException e) {
            System.out.println("\033[31m需复制SubDic文件到当前Jar包所在目录下\033[0m");
            System.exit(0);
        }
        System.out.println("\033[32m[*]\033[m读取SubDic文件成功，共读取到"+count+"个域名");
        //System.out.println("读取SubDic文件成功，共读取到"+count+"个域名");
        return subDomainList;
    }
    public static <T> List<List<T>> averageAssign(List<T> source, int n){
        List<List<T>> result=new ArrayList<List<T>>();
        int remaider=source.size()%n;  //(先计算出余数)
        int number=source.size()/n;  //然后是商
        int offset=0;//偏移量
        for(int i=0;i<n;i++){
            List<T> value=null;
            if(remaider>0){
                value=source.subList(i*number+offset, (i+1)*number+offset+1);
                remaider--;
                offset++;
            }else{
                value=source.subList(i*number+offset, (i+1)*number+offset);
            }
            result.add(value);
        }
        return result;
    }


}
