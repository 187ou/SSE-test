package com.example.adp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * 消息内容
 */
public class Content {
    @JsonProperty("Type")
    private String type;

    @JsonProperty("Text")
    private String text;

    @JsonProperty("Image")
    private ImageInfo image;

    @JsonProperty("CustomVariables")
    private Map<String, String> customVariables;

    public Content() {}

    public Content(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public Content(String type, ImageInfo image) {
        this.type = type;
        this.image = image;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public ImageInfo getImage() { return image; }
    public void setImage(ImageInfo image) { this.image = image; }

    public Map<String, String> getCustomVariables() { return customVariables; }
    public void setCustomVariables(Map<String, String> customVariables) { this.customVariables = customVariables; }
}
