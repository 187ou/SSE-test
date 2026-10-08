package com.example.adp.controller;

import com.example.adp.model.Conversation;
import com.example.adp.model.Message;
import com.example.adp.service.ChatService;
import com.example.adp.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * 对话API控制器
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final ConversationService conversationService;

    public ChatController(ChatService chatService, ConversationService conversationService) {
        this.chatService = chatService;
        this.conversationService = conversationService;
    }

    /**
     * 发送消息（流式响应）
     * POST /api/chat/send
     * Body: { "message": "你好" }
     */
    @PostMapping(value = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sendMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("消息不能为空"));
        }

        return chatService.sendMessage(message);
    }

    /**
     * 创建新会话
     * POST /api/conversations
     */
    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation() {
        Conversation conversation = conversationService.createConversation("新对话");
        return ResponseEntity.ok(conversation);
    }

    /**
     * 获取所有会话列表
     * GET /api/conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> listConversations() {
        return ResponseEntity.ok(conversationService.listConversations());
    }

    /**
     * 获取会话详情
     * GET /api/conversations/{id}
     */
    @GetMapping("/conversations/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable String id) {
        return conversationService.getConversation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除会话
     * DELETE /api/conversations/{id}
     */
    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String id) {
        boolean deleted = conversationService.deleteConversation(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
