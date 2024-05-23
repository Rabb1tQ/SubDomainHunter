package com.rabbitq.util;

import java.util.Map;

public class GlobalConfig {
    public static final Map<String,Object> globalConfig= ConfigLoader.INSTANCE.getConfig();
}
