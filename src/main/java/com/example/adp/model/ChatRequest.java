package com.example.adp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * ADP对话请求
 */
public class ChatRequest {
    @JsonProperty("RequestId")
    private String requestId;

    @JsonProperty("ConversationId")
    private String conversationId;

    @JsonProperty("AppKey")
    private String appKey;

    @JsonProperty("VisitorId")
    private String visitorId;

    @JsonProperty("UserId")
    private String userId;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("Contents")
    private List<Content> contents;

    @JsonProperty("StreamingThrottle")
    private Number streamingThrottle;

    @JsonProperty("SystemRole")
    private String systemRole;

    @JsonProperty("Incremental")
    private Boolean incremental;

    @JsonProperty("SearchNetwork")
    private String searchNetwork;

    @JsonProperty("ModelName")
    private String modelName;

    @JsonProperty("Stream")
    private String stream;

    @JsonProperty("WorkflowStatus")
    private String workflowStatus;

    @JsonProperty("EnableMultiIntent")
    private Boolean enableMultiIntent;

    @JsonProperty("GenerateAgain")
    private Boolean generateAgain;

    public ChatRequest() {}

    public ChatRequest(String requestId, String conversationId, String appKey,
                       String visitorId, String userId, String userName,
                       List<Content> contents) {
        this.requestId = requestId;
        this.conversationId = conversationId;
        this.appKey = appKey;
        this.visitorId = visitorId;
        this.userId = userId;
        this.userName = userName;
        this.contents = contents;
        this.incremental = true;
        this.stream = "enable";
        this.enableMultiIntent = true;
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getAppKey() { return appKey; }
    public void setAppKey(String appKey) { this.appKey = appKey; }

    public String getVisitorId() { return visitorId; }
    public void setVisitorId(String visitorId) { this.visitorId = visitorId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public List<Content> getContents() { return contents; }
    public void setContents(List<Content> contents) { this.contents = contents; }

    public Number getStreamingThrottle() { return streamingThrottle; }
    public void setStreamingThrottle(Number streamingThrottle) { this.streamingThrottle = streamingThrottle; }

    public String getSystemRole() { return systemRole; }
    public void setSystemRole(String systemRole) { this.systemRole = systemRole; }

    public Boolean getIncremental() { return incremental; }
    public void setIncremental(Boolean incremental) { this.incremental = incremental; }

    public String getSearchNetwork() { return searchNetwork; }
    public void setSearchNetwork(String searchNetwork) { this.searchNetwork = searchNetwork; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public String getWorkflowStatus() { return workflowStatus; }
    public void setWorkflowStatus(String workflowStatus) { this.workflowStatus = workflowStatus; }

    public Boolean getEnableMultiIntent() { return enableMultiIntent; }
    public void setEnableMultiIntent(Boolean enableMultiIntent) { this.enableMultiIntent = enableMultiIntent; }

    public Boolean getGenerateAgain() { return generateAgain; }
    public void setGenerateAgain(Boolean generateAgain) { this.generateAgain = generateAgain; }
}
