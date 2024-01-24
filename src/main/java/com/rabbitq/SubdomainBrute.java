package com.rabbitq;

import com.beust.jcommander.JCommander;
import com.rabbitq.entity.TargetOptionsEntity;
import com.rabbitq.utils.ThreadConf;

public class SubdomainBrute {

    public static void main(String[] args) {
        printBanner();

        TargetOptionsEntity targetOptionsEntity = new TargetOptionsEntity();
        JCommander commander = JCommander.newBuilder()
                .addObject(targetOptionsEntity)
                .build();
        try {
            commander.parse(args);
        } catch (Exception e){
			commander.usage();
            return;
		}
        if (targetOptionsEntity.isHelp()) {
            commander.usage();
            return;
        }
        ThreadConf threadConf = new ThreadConf();
        threadConf.bruteAction(targetOptionsEntity);
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
