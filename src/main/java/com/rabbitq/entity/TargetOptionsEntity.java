package com.rabbitq.entity;

import com.beust.jcommander.Parameter;

public class TargetOptionsEntity {

    @Parameter(names = {"--domain","-d"},
            description = "目标域名",
            required = true)
    private String domain;

    @Parameter(names = {"--nameserver","-n"},
            description = "超时时间")
    private String NameServer;

    @Parameter(names = {"--threads","-t"},
            description = "线程数")
    private int numOfThreads=100;



    @Parameter(names = {"help", "--help"},
            description = "查看帮助信息",
            help = true)
    private boolean help;

    public boolean isHelp() {
        return help;
    }

    public String getDomain() {
        return domain;
    }

    public String getNameServer() {
        return NameServer;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }
}
