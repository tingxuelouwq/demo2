package com.example.demo2.user;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * kevin<br/>
 * 2020/11/26 13:37<br/>
 */
@Configuration
@ConfigurationProperties(prefix = "properties.message")
public class MyConfig {
    private Map<String, String> infoMap;

    public Map<String, String> getInfoMap() {
        return infoMap;
    }

    public void setInfoMap(Map<String, String> infoMap) {
        this.infoMap = infoMap;
    }
}
