package com.example.adp.service;

import com.example.adp.client.ADPClient;
import com.example.adp.model.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * 对话服务
 */
@Service
public class ChatService {

    private final ADPClient adpClient;

    public ChatService() {
        this.adpClient = new ADPClient(
                "https://wss.lke.cloud.tencent.com/adp/v2/chat",
                "JEeIZqVV"
        );
    }

    /**
     * 发送消息并返回流式响应
     */
    public Flux<String> sendMessage(String message) {
        return Flux.create(sink -> {
            try {
                adpClient.chat(
                        message,
                        sink::next,      // text delta callback
                        sink::complete,   // complete callback
                        sink::error       // error callback
                );
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    /**
     * 创建欢迎消息
     */
    public Message createWelcomeMessage() {
        return new Message(
                UUID.randomUUID().toString(),
                "assistant",
                "你好！我是你的AI助手，有什么可以帮助你的吗？"
        );
    }
}
