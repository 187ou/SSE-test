package com.example.adp.service;

import com.example.adp.model.Conversation;
import com.example.adp.model.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理服务
 */
@Service
public class ConversationService {

    // 内存存储（生产环境应该用数据库）
    private final ConcurrentHashMap<String, Conversation> conversations = new ConcurrentHashMap<>();

    /**
     * 创建新会话
     */
    public Conversation createConversation(String title) {
        String id = UUID.randomUUID().toString();
        Conversation conversation = new Conversation(id, title);
        conversations.put(id, conversation);
        return conversation;
    }

    /**
     * 获取会话
     */
    public Optional<Conversation> getConversation(String id) {
        return Optional.ofNullable(conversations.get(id));
    }

    /**
     * 列出所有会话
     */
    public List<Conversation> listConversations() {
        return new ArrayList<>(conversations.values());
    }

    /**
     * 删除会话
     */
    public boolean deleteConversation(String id) {
        return conversations.remove(id) != null;
    }

    /**
     * 添加消息到会话
     */
    public void addMessage(String conversationId, Message message) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            conversation.addMessage(message);
        }
    }
}
