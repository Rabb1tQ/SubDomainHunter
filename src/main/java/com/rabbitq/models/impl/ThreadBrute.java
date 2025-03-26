package com.rabbitq.models.impl;


import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;
import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

@SubDomainInterfaceImplementation
public class ThreadBrute implements SubDomainInterface {
    @Override
    public Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) {
        String strBruteDomain = targetOptionsEntity.getDomain();
        String source = "ThreadBrute";
        String nameServer = targetOptionsEntity.getNameServer();
        int numOfThreads = targetOptionsEntity.getNumOfThreads();
        //设置目标地址
        List<String> subDomainList = SubDic(strBruteDomain);

        List<List<String>> burteSubdomain = averageAssign(subDomainList, numOfThreads);


        //泛解析识别
        if (wildcardDomain(strBruteDomain)) {
            PrintUtils.error("未进行枚举子域失败，原因：泛解析");
            return Collections.emptySet();
        }
        ConcurrentSkipListSet<String> subDomains = new ConcurrentSkipListSet<>();
        try {


            if (nameServer == null || nameServer.isEmpty()) {
                nameServer = "8.8.8.8";
            }
            System.setProperty("sun.net.spi.nameservice.nameservers", nameServer);
            System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");

            System.out.println("\033[32m[*]\033[m最大启动线程数为" + numOfThreads);
            int subDomainSize = subDomainList.size();
            if (subDomainSize < numOfThreads) {
                numOfThreads = subDomainSize;
            }

            ThreadPoolExecutor myExecutor = new ThreadPoolExecutor(numOfThreads, numOfThreads, 200, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>());

            System.out.println("\033[32m[*]\033[0m存活主机：");
            ProgressBar pb = new ProgressBar("爆破进度：", subDomainList.size());
            for (List<String> subDomain : burteSubdomain) {
                myExecutor.submit(() -> {
                    for (String domain : subDomain) {
                        try {
                            InetAddress address = InetAddress.getByName(domain);
                            subDomains.add(domain);
                        } catch (UnknownHostException e) {
                            // Host not reachable
                        }
                        pb.step();
                    }
                });

            }
            myExecutor.shutdown();
            myExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            pb.close();
            System.out.println("\033[32m[*]\033[0m爆破已完成，共获取到" + subDomains.size() + "子域");
        } catch (Exception e) {
            PrintUtils.error(source, e.getMessage(), subDomains);
        }


        return subDomains;
    }

    public boolean wildcardDomain(String strBruteDomain) {
        boolean result = false;
        UUID uuid = UUID.randomUUID();
        String strTarget = uuid + "." + strBruteDomain;
        try {
            InetAddress address = InetAddress.getByName(strTarget);
            result = true;

        } catch (UnknownHostException ignored) {
        }
        return result;
    }

    public List<String> SubDic(String bruteDomain) {
        List<String> subDomainList = new ArrayList<>();
        File file = new File(System.getProperty("user.dir") + "/SubDic");
        Scanner scanner;
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
            PrintUtils.error("需复制SubDic文件到当前Jar包所在目录下");
            System.exit(0);
        }
        PrintUtils.sucess("读取SubDic文件成功，共读取到" + count + "个域名");
        return subDomainList;
    }

    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value;
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
