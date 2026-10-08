package com.example.adp.client;

import com.example.adp.model.ChatRequest;
import com.example.adp.model.Content;
import com.example.adp.model.SSEEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * ADP对话客户端 - 支持回调处理SSE事件
 */
public class ADPClient {

    private final String apiUrl;
    private final String appKey;
    private final ObjectMapper objectMapper;

    /**
     * SSE事件回调接口
     */
    public interface SSECallback {
        void onEvent(SSEEvent event, String rawData);
    }

    public ADPClient(String apiUrl, String appKey) {
        this.apiUrl = apiUrl;
        this.appKey = appKey;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 发送文本消息并处理SSE响应流（使用回调）
     */
    public void chat(String message, Consumer<String> textDeltaCallback,
                     Runnable onComplete, Consumer<Throwable> onError) throws Exception {
        // 生成必要的ID
        String requestId = UUID.randomUUID().toString();
        String conversationId = UUID.randomUUID().toString();
        String visitorId = "visitor_" + UUID.randomUUID().toString().substring(0, 8);
        String userId = "user_" + UUID.randomUUID().toString().substring(0, 8);

        // 构建请求内容
        Content textContent = new Content("text", message);
        List<Content> contents = new ArrayList<>();
        contents.add(textContent);

        // 追加自定义变量
        Map<String, String> customVariables = new HashMap<>();
        customVariables.put("userName", "guest");
        customVariables.put("phoneNumber", "0000");
        Content customVariablesContent = new Content();
        customVariablesContent.setType("custom_variables");
        customVariablesContent.setCustomVariables(customVariables);
        contents.add(customVariablesContent);

        // 构建请求对象
        ChatRequest request = new ChatRequest(
                requestId,
                conversationId,
                appKey,
                visitorId,
                userId,
                null,
                contents
        );

        // 发送请求并获取SSE流
        sendRequest(request, textDeltaCallback, onComplete, onError);
    }

    /**
     * 发送HTTP请求并处理SSE响应（带回调）
     */
    private void sendRequest(ChatRequest request, Consumer<String> textDeltaCallback,
                             Runnable onComplete, Consumer<Throwable> onError) throws Exception {
        // 序列化请求体
        String requestBody = objectMapper.writeValueAsString(request);
        System.out.println("[后端] 发送请求到ADP");

        // 创建HTTP连接
        HttpURLConnection connection = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);

        // 发送请求体
        try (var outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        // 检查响应码
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP请求失败: " + responseCode + " " + connection.getResponseMessage());
        }

        // 读取SSE流
        StringBuilder fullResponse = new StringBuilder();
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            StringBuilder currentEventData = new StringBuilder();
            String currentEventType = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // 跳过空行
                if (line.isEmpty()) {
                    if (currentEventData.length() > 0 && currentEventType != null) {
                        // 处理完整事件
                        String eventData = currentEventData.toString();
                        processSSEEvent(currentEventType, eventData, textDeltaCallback, fullResponse);
                        currentEventData.setLength(0);
                        currentEventType = null;
                    }
                    continue;
                }

                // 跳过注释（但保留data:行）
                if (line.startsWith(":") && !line.startsWith("data:")) {
                    continue;
                }

                // 解析数据
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    if (currentEventData.length() > 0) {
                        currentEventData.append("\n");
                    }
                    currentEventData.append(data);
                    continue;
                }

                // 解析事件类型
                if (line.startsWith("event:")) {
                    currentEventType = line.substring(6).trim();
                    continue;
                }
            }

            // 处理最后一个事件（如果存在）
            if (currentEventData.length() > 0 && currentEventType != null) {
                String eventData = currentEventData.toString();
                processSSEEvent(currentEventType, eventData, textDeltaCallback, fullResponse);
            }
        }

        // 完成回调
        if (onComplete != null) {
            onComplete.run();
        }
    }

    /**
     * 处理单个SSE事件
     */
    private void processSSEEvent(String eventType, String eventData,
                                 Consumer<String> textDeltaCallback, StringBuilder fullResponse) {
        // 处理DONE标记
        if ("[DONE]".equals(eventData) || "done".equals(eventType)) {
            return;
        }

        try {
            // 解析事件数据
            SSEEvent event = objectMapper.readValue(eventData, SSEEvent.class);

            // 根据事件类型处理
            switch (event.getType()) {
                case "text.delta":
                    // 流式输出文本片段
                    if (event.getExtraFields() != null && event.getExtraFields().containsKey("Text")) {
                        String text = event.getExtraFields().get("Text").toString();
                        fullResponse.append(text);
                        if (textDeltaCallback != null) {
                            textDeltaCallback.accept(text);
                        }
                    }
                    break;

                case "message.done":
                    // 消息完成，可以提取完整回复
                    if (event.getExtraFields() != null && event.getExtraFields().containsKey("Message")) {
                        System.out.println("[后端] 消息完成: " + event.getExtraFields().get("Message"));
                    }
                    break;

                case "response.completed":
                    System.out.println("[后端] 响应已完成，完整回复长度: " + fullResponse.length());
                    break;

                case "error":
                    System.err.println("[后端] ADP返回错误: " + event.getExtraFields());
                    break;

                default:
                    // 其他事件类型忽略
                    break;
            }

        } catch (Exception e) {
            System.err.println("[后端] 解析事件失败: " + e.getMessage());
        }
    }
}
