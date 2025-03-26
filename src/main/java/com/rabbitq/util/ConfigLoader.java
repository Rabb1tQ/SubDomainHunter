package com.rabbitq.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ConfigLoader {
    INSTANCE;

    private Map<String, Object> mapConfigEntity=new HashMap<>();

    private ConfigLoader() {
        Yaml yaml = new Yaml();
        try{
            FileInputStream inputStream = new FileInputStream(new File("config.yaml"));
            this.mapConfigEntity = yaml.load(inputStream);
        } catch (IOException e) {
            PrintUtils.error("配置文件读取失败，需账户的API接口将无法使用：" + e);
        }

    }

    public Map<String, Object> getConfig() {
        return mapConfigEntity;
    }
}
