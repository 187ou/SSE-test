package com.example.adp.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * SSE事件基类
 */
public class SSEEvent {
    @JsonProperty("Type")
    private String type;

    // 使用Map存储未知字段
    private Map<String, Object> extraFields;

    @JsonAnySetter
    public void setExtraField(String key, Object value) {
        if (extraFields == null) {
            extraFields = new java.util.HashMap<>();
        }
        extraFields.put(key, value);
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, Object> getExtraFields() { return extraFields; }
    public void setExtraFields(Map<String, Object> extraFields) { this.extraFields = extraFields; }

    @Override
    public String toString() {
        return "SSEEvent{" +
                "type='" + type + '\'' +
                '}';
    }
}
