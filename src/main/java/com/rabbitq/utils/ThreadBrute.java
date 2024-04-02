package com.rabbitq.utils;


import com.rabbitq.entity.TargetOptionsEntity;
import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.*;

public class ThreadBrute {
    public Set<String> bruteAction(TargetOptionsEntity targetOptionsEntity) {

        String bruteDomain = targetOptionsEntity.getDomain();
        String nameServer = targetOptionsEntity.getNameServer();
        if (nameServer == null || nameServer.equals("")) {
            nameServer = "223.5.5.5";
        }
        System.setProperty("sun.net.spi.nameservice.nameservers", nameServer);
        System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
        int numOfThreads = targetOptionsEntity.getNumOfThreads();
        //设置目标地址
        List<String> subDomainList = SubDic(bruteDomain);

        System.out.println("\033[32m[*]\033[m最大启动线程数为" + numOfThreads);
        int subDomainSize = subDomainList.size();
        if (subDomainSize < numOfThreads) {
            numOfThreads = subDomainSize;
        }
        List<List<String>> burteSubdomain = averageAssign(subDomainList, numOfThreads);
        ThreadPoolExecutor myExecutor = new ThreadPoolExecutor(numOfThreads, numOfThreads, 200, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>());

        ConcurrentHashMap<String, Boolean> resultsMap = new ConcurrentHashMap<>();
        ConcurrentSkipListSet<String> setSubDomain = new ConcurrentSkipListSet<>();
        System.out.println("\033[32m[*]\033[0m存活主机：");
        ProgressBar pb = new ProgressBar("爆破进度：", subDomainList.size());
        for (List<String> subDomain : burteSubdomain) {
            myExecutor.submit(() -> {
                for (String domain : subDomain) {
                    try {
                        InetAddress address = InetAddress.getByName(domain);
                        setSubDomain.add(domain);
                    } catch (UnknownHostException e) {
                        // Host not reachable
                    }
                    pb.step();
                }
            });

        }
        myExecutor.shutdown();
        try {
            myExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pb.close();
        //System.out.println(setSubDomain);

        return setSubDomain;
    }

    public List<String> SubDic(String bruteDomain) {
        List<String> subDomainList = new ArrayList<>();
        File file = new File(System.getProperty("user.dir") + "/SubDic");
        Scanner scanner = null;
        int count = 0;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine() + "." + bruteDomain;
                subDomainList.add(line);
                count++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("\033[31m需复制SubDic文件到当前Jar包所在目录下\033[0m");
            System.exit(0);
        }
        System.out.println("\033[32m[*]\033[m读取SubDic文件成功，共读取到" + count + "个域名");
        //System.out.println("读取SubDic文件成功，共读取到"+count+"个域名");
        return subDomainList;
    }

    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


}
