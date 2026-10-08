package com.example.adp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 图片信息
 */
public class ImageInfo {
    @JsonProperty("Url")
    private String url;

    public ImageInfo() {}

    public ImageInfo(String url) {
        this.url = url;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
