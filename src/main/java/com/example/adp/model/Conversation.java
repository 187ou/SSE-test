package com.example.adp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天会话
 */
public class Conversation {
    private String id;
    private String title;
    private List<Message> messages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Conversation() {
        this.messages = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Conversation(String id, String title) {
        this();
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void addMessage(Message message) {
        this.messages.add(message);
        this.updatedAt = LocalDateTime.now();
    }
}
