package com.rabbitq;

import cn.hutool.core.io.FileUtil;
import com.beust.jcommander.JCommander;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.utils.AlienVault;
import com.rabbitq.utils.Rapiddns;
import com.rabbitq.utils.SiteDossier;
import com.rabbitq.utils.ThreadBrute;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class SubdomainBrute {


    public static void main(String[] args) {
        printBanner();

        TargetOptionsEntity targetOptionsEntity = new TargetOptionsEntity();
        JCommander commander = JCommander.newBuilder()
                .addObject(targetOptionsEntity)
                .build();
        try {
            commander.parse(args);
        } catch (Exception e) {
            commander.usage();
            return;
        }
        if (targetOptionsEntity.isHelp()) {
            commander.usage();
        }
        taskConf(targetOptionsEntity);
    }

    public static void taskConf(TargetOptionsEntity targetOptionsEntity) {
        //爆破
        Set<String> setSubDomain = new HashSet<>();
        ThreadBrute threadBrute = new ThreadBrute();
        Set<String> setTemp = threadBrute.bruteAction(targetOptionsEntity);
        setSubDomain.addAll(setTemp);
        System.out.println("\033[32m[*]\033[0m爆破已完成，共获取到" + setTemp.size() + "子域");


        String targetURL = targetOptionsEntity.getDomain();


        //通过sitedossier接口获取
        SiteDossier siteDossier = new SiteDossier();
        try {
            setTemp = siteDossier.getSiteDossierSubDomain(targetURL);
            setSubDomain.addAll(setTemp);
            System.out.println("\033[32m[*]\033[0m通过sitedossier接口获取完成" + "，共获取到" + setTemp.size() + "子域");
        }
        catch (Exception e) {
            System.out.println("\033[31msitedossier获取失败，原因：" + e);
        }




        //通过Rapiddns获取
        Rapiddns rapiddns = new Rapiddns();
        try {
            setTemp = rapiddns.getRapidSubDomain(targetURL);
            setSubDomain.addAll(setTemp);
            System.out.println("\033[32m[*]\033[0m通过Rapiddns接口获取完成" + "，共获取到" + setTemp.size() + "子域");
        } catch (Exception e) {
            System.out.println("\033[31mrapiddns获取失败，原因：" + e);
        }


        //通过AlienVault获取
        AlienVault AlienVault = new AlienVault();
        try {
            setTemp = AlienVault.getSiteAlienVaultSubDomain(targetURL);
            setSubDomain.addAll(setTemp);
            System.out.println("\033[32m[*]\033[0m通过AlienVaul接口获取完成" + "，共获取到" + setTemp.size() + "子域");
        } catch (Exception e) {
            System.out.println("\033[31mAlienVaul获取失败，原因：" + e);
        }
        LocalDateTime currentTime = LocalDateTime.now();

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        // 将当前时间格式化为字符串作为文件名
        String fileName = currentTime.format(formatter);

        // 添加文件扩展名（例如.txt）
        fileName += ".txt";
        String filePath=System.getProperty("user.dir")+"/"+fileName;
        FileUtil.writeLines(setSubDomain, new File(filePath), "UTF-8");
        System.out.println("\033[32m[*]\033[0m已将结果写入文件："+filePath);
    }


    public static void printBanner() {
        System.out.println(
                "██████╗ ██╗   ██╗    ██████╗  █████╗ ██████╗ ██████╗ ██╗████████╗ ██████╗ \n" +
                        "██╔══██╗╚██╗ ██╔╝    ██╔══██╗██╔══██╗██╔══██╗██╔══██╗██║╚══██╔══╝██╔═══██╗\n" +
                        "██████╔╝ ╚████╔╝     ██████╔╝███████║██████╔╝██████╔╝██║   ██║   ██║   ██║\n" +
                        "██╔══██╗  ╚██╔╝      ██╔══██╗██╔══██║██╔══██╗██╔══██╗██║   ██║   ██║▄▄ ██║\n" +
                        "██████╔╝   ██║       ██║  ██║██║  ██║██████╔╝██████╔╝██║   ██║   ╚██████╔╝\n" +
                        "╚═════╝    ╚═╝       ╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚═════╝ ╚═╝   ╚═╝    ╚══▀▀═╝ \n" +
                        "                                                                          ");
    }
}
