package com.example.adp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置加载器
 */
public class ConfigLoader {
    private final Properties properties;

    public ConfigLoader() {
        this.properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("警告: 未找到 application.properties 配置文件");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("加载配置文件失败: " + e.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
