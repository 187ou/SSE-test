package com.example.adp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天消息
 */
public class Message {
    private String id;
    private String role;  // user 或 assistant
    private String content;
    private LocalDateTime timestamp;
    private List<String> citations;  // 引用来源

    public Message() {}

    public Message(String id, String role, String content) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.citations = new ArrayList<>();
    }

    public Message(String id, String role, String content, List<String> citations) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.citations = citations;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<String> getCitations() { return citations; }
    public void setCitations(List<String> citations) { this.citations = citations; }
}
