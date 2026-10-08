package com.example.adp;

import com.example.adp.client.ADPClient;
import com.example.adp.util.ConfigLoader;

import java.nio.charset.StandardCharsets;

/**
 * ADP Chat Client - MVP主程序
 *
 * 使用说明：
 * 1. 在 src/main/resources/application.properties 中配置您的AppKey
 * 2. 运行程序即可发送测试消息
 */
public class App {
    public static void main(String[] args) {
        // 设置控制台编码为UTF-8（解决Windows中文乱码）
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, StandardCharsets.UTF_8));

        try {
            // 加载配置
            ConfigLoader configLoader = new ConfigLoader();
            String apiUrl = configLoader.getProperty("adp.api.url");
            String appKey = configLoader.getProperty("adp.app.key");

            // 验证配置
            if (appKey == null || appKey.isEmpty() || appKey.equals("YOUR_APP_KEY_HERE")) {
                System.err.println("错误: 请在 application.properties 中配置有效的 AppKey");
                System.err.println("配置文件路径: src/main/resources/application.properties");
                System.exit(1);
            }

            System.out.println("========================================");
            System.out.println("  ADP Chat Client - MVP");
            System.out.println("========================================\n");

            // 创建客户端
            ADPClient client = new ADPClient(apiUrl, appKey);

            // 发送测试消息
            String testMessage = "你是谁";
            System.out.println("发送消息: " + testMessage + "\n");

            // 使用回调方式发送
            StringBuilder fullResponse = new StringBuilder();
            client.chat(
                    testMessage,
                    text -> {
                        System.out.print(text);
                        fullResponse.append(text);
                    },
                    () -> System.out.println("\n\n>>> 消息发送完成"),
                    error -> {
                        System.err.println("错误: " + error.getMessage());
                        error.printStackTrace();
                    }
            );

            Thread.sleep(5000); // 等待流式响应完成

            System.out.println("\n\n完整回复: " + fullResponse);

            System.out.println("\n========================================");
            System.out.println("  测试完成");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("程序异常: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
