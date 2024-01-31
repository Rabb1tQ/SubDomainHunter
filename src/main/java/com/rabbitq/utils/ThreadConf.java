package com.rabbitq.utils;


import com.rabbitq.entity.TargetOptionsEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class ThreadConf {
    public void bruteAction(TargetOptionsEntity targetOptionsEntity) {
        long start = System.currentTimeMillis();
        String bruteDomain=targetOptionsEntity.getDomain();
        String nameServer=targetOptionsEntity.getNameServer();
        if(nameServer==null||nameServer.equals("")){
            nameServer="8.8.8.8";
        }
        System.setProperty("sun.net.spi.nameservice.nameservers", nameServer);
        System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
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
        TCPThreadCallable.numOfThreads = numOfThreads;
        System.out.println("\033[32m[*]\033[0m存活主机：");
        TCPThreadCallable workers[] = new TCPThreadCallable[numOfThreads];
        Future[] futures = new Future[numOfThreads];
        for (int i = 0; i < numOfThreads; i++){
            ArrayList<String> castedList = new ArrayList<>(burteSubdomain.get(i));
            String[] array = castedList.toArray(new String[castedList.size()]);
            workers[i] = new TCPThreadCallable("T" + i, array);
            futures[i] = myExecutor.submit(workers[i]);

        }
        for (int i = 0; i < numOfThreads; i++) {
            try {
                if(futures[i].get()!=null){
                    List<String> list= (List<String>) futures[i].get();
                    for (int j=0;j<list.size();j++){
                        System.out.println(list.get(j));
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }

        myExecutor.shutdown();
        long end = System.currentTimeMillis(); // 记录结束时间
        long duration = end - start; // 计算运行时间（单位为毫秒）

        System.out.println("程序运行时间：" + duration + "ms");
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
                count++;
            }
            scanner.close();
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
