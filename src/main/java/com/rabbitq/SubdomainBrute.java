package com.rabbitq;

import cn.hutool.core.io.FileUtil;
import com.beust.jcommander.JCommander;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.models.SubDomainInterface;
import com.rabbitq.util.PrintUtils;
import com.rabbitq.util.SubDoaminClassScanner;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
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
        List<SubDomainInterface> implementations = SubDoaminClassScanner.scan();

        Set<String> setSubDomain = new HashSet<>();
        for (SubDomainInterface impl : implementations) {
            try {
                setSubDomain.addAll(impl.getSubDomain(targetOptionsEntity));
                //System.out.println(impl.getClass().getName());
            } catch (Exception e) {
                // 捕捉子类方法执行的异常
                System.out.println("Exception in calling method : "+impl.getClass().getName());
                System.out.println("具体异常："+e.getMessage());
            }
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
        PrintUtils.sucess("已完成任务，共收集"+setSubDomain.size()+"个子域名");
        PrintUtils.sucess("已将结果写入文件："+filePath);
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
