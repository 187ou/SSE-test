package com.example.adp;

import com.example.adp.model.ChatRequest;
import com.example.adp.model.Content;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 模型类单元测试
 */
public class ModelTest {

    @Test
    public void testChatRequestSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Content content = new Content("text", "你好");
        ChatRequest request = new ChatRequest(
                "test-request-id",
                "test-conversation-id",
                "test-app-key",
                "test-visitor-id",
                "test-user-id",
                "测试用户",
                java.util.List.of(content)
        );

        String json = mapper.writeValueAsString(request);
        assertNotNull(json);
        assertTrue(json.contains("test-request-id"));
        assertTrue(json.contains("test-app-key"));
        assertTrue(json.contains("你好"));
    }

    @Test
    public void testContentSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Content content = new Content("text", "测试消息");
        String json = mapper.writeValueAsString(content);

        assertNotNull(json);
        assertTrue(json.contains("\"Type\":\"text\""));
        assertTrue(json.contains("\"Text\":\"测试消息\""));
    }
}
