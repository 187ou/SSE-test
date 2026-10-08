# 对话端接口文档（HTTP SSE）

最近更新时间：2026-09-18 10:39:01

HTTP SSE 是单向通道，客户端发起 HTTP 请求之后，服务端持续推送流式数据到客户端，此时不支持双向交互。

## 1. HTTP SSE 接口请求

请求地址：`https://wss.lke.cloud.tencent.com/adp/v2/chat`
请求方式：POST

> **注意**：触发对话接口前，需要有已发布的应用。

### 1.1 参数说明

请放到 HTTP Body 中，以 JSON 的形式发送，具体如下：

| 名称              | 类型             | 是否必填 | 说明                                                         |
| ----------------- | ---------------- | -------- | ------------------------------------------------------------ |
| RequestId         | String           | 否       | 请求 ID，用于标识一个请求（作为消息串联，建议每个请求使用不同的 RequestId）。为了便于问题排查，建议必填。参数长度：32-64个字符。校验规则：`^[a-zA-Z0-9_-]{32,64}$`，一般可以用 uuid 来生成该值。uuid 示例：`1b9c0b03-dc83-47ac-8394-b366e3ea67ef` |
| ConversationId    | String           | 是       | 会话 ID，用于标识一个会话（外部系统提供，建议不同的用户端会话传入不同的 ConversationId，否则同一个应用下的不同用户的消息记录会串掉）。参数长度：32-64个字符。校验规则：`^[a-zA-Z0-9_-]{32,64}$`，一般可以用 uuid 来生成该值。uuid 示例：`1b9c0b03-dc83-47ac-8394-b366e3ea67ef` |
| AppKey            | String           | 是       | 应用密钥。应用发布后可以在"服务状态"处获取。                 |
| VisitorId         | String           | 是       | 访客 ID（外部输入，建议唯一，标识当前接入会话的用户）。**注意**：该参数即将废弃，请使用 UserId 代替。在开启了平台端用户权限的情况下，这个 ID 将与权限关联。 |
| UserId            | String           | 是       | 用户 ID（外部输入，建议唯一，标识当前接入会话的用户，在应用中作为 API 参数 VisitorBizId 的值，可以在应用中被引用） |
| UserName          | String           | 否       | 用户名（外部输入，在应用中作为 API 参数 VisitorUserAccount 的值，可以在应用中被引用） |
| Contents          | Array of Content | 是       | 消息内容信息列表。                                           |
| StreamingThrottle | Number           | 否       | 流式回复频率控制：控制应用回包频率。该值表示应用每积攒多少字符向调用方回包一次，值越小回包越频繁（体验上越流畅，但流量开销也越大）。当不传值或者为 0 时以系统配置为准。默认值：5，建议最大值：100。**注意**：该设置项也不会加快大模型输出的时间，只是改变了向调用方回包的频率。因此如果设置很大，则会出现很长时间没有回包的现象。 |
| SystemRole        | String           | 否       | 角色指令（提示词），为空时使用应用配置默认设定，填写时取当前值。最大值限制：不同模型最大值有差异，请参考 ListModel 接口的 RoleLenLimit。 |
| Incremental       | Boolean          | 否       | 控制回复的文本内容是以文本增量事件（text.delta）还是文本替换事件（text.replace）返回，默认 false。**注意**：Incremental 为 true 时，文本内容并不是完全以文本增量事件返回，ADP 后台在发现需要修改已经返回的内容时，会返回文本替换事件。 |
| SearchNetwork     | String           | 否       | 是否开启联网搜索。取值范围：空字符串：跟随应用配置；enable：打开联网搜索；disable：关闭联网搜索。**注意**：应用配置在未开启联网搜索时，如上取值字段无意义。 |
| ModelName         | String           | 否       | 支持在对话的过程中传入指定模型，同一个 ConversationId 下传入不同的模型名称可以保持上下文之间的关联。当前支持下面几个自定义模型：`TCADP/glm-5`：智谱 GLM-5；`TCADP/kimi-k2.5`：Kimi K2.5；`TCADP/minimax-m2.5`：MiniMax M2.5。**注意**：该参数只对标准模式生成模型生效。请确保账户下有足够的 PU 资源，避免在使用的过程中报错。 |
| Stream            | String           | 否       | 是否开启流式传输。取值范围：空字符串：跟随应用配置；enable：流式传输；disable：非流式传输 |
| WorkflowStatus    | String           | 否       | 是否开启工作流。取值范围：空字符串：跟随应用配置（页面无配置工作流开关的入口，默认是开启）；enable：开启工作流；disable：关闭工作流 |
| EnableMultiIntent | Boolean          | 否       | 是否开启多意图。                                             |
| GenerateAgain     | Boolean          | 否       | 是否重新生成请求。                                           |

### 1.2 如何获取 AppKey

在应用管理界面，找到您处于运行中的应用（需要先发布），单击调用，会弹出"调用信息"窗口。在调用信息窗口可以看到 AppKey，单击复制即可。

### 1.3 curl 调用示例

**文本问答**：

```bash
curl --location --request POST 'https://wss.lke.cloud.tencent.com/adp/v2/chat' \
--header 'Content-Type: application/json' \
--data '{
    "RequestId": "4feb312a-14e9-4161-bfe2-767c43ae0524",
    "ConversationId": "362b3aac-b7eb-4d2a-a154-204f288a4727",
    "AppKey": "<您的真实AppKey>",
    "Contents": [
        {
            "Type": "text",
            "Text": ""
        }
    ],
    "VisitorId": "100015179581",
    "Incremental": true,
    "EnableMultiIntent": true,
    "Stream": "enable"
}'
```

运行环境：Ubuntu 24.04.3 LTS / x86_64，GNU bash 5.2.21(1)-release

**图文问答**：

```bash
curl --location 'https://wss.lke.cloud.tencent.com/adp/v2/chat' \
--header 'Content-Type: application/json' \
--data '{
    "RequestId": "c12560d9-fd98-416e-a42c-a939d9f18cae",
    "ConversationId": "9ce50f0c-5f7a-43f0-a2d4-713d4534de92",
    "AppKey": "<您的真实AppKey>",
    "Contents": [
        {
            "Type": "text",
            "Text": "图片中的演员是谁"
        },
        {
            "Type": "image",
            "Image": {
                "Url": "https://lke-realtime-test-1251316161.cos.ap-guangzhou.myqcloud.com/public/1968969767807943680/1972219265921606848/image/kNiwiORTwRPFCuUlQfQr-2004384889086723008.png"
            }
        }
    ],
    "VisitorId": "100015179581",
    "Incremental": true,
    "EnableMultiIntent": true,
    "Stream": "enable"
}'
```

运行环境：Ubuntu 24.04.3 LTS / x86_64，GNU bash 5.2.21(1)-release

**回复示例**：

```
event: request_ack
data: {"Type":"request_ack","RequestAck":{"Role":"user","RecordId":"VJx_20251223_020145_889_xiiAQ0s2","ConversationId":"d4bca75f-e713-42c4-ad37-67b7d6621f10","Status":"success","StatusDesc":"请求成功","Messages":[{"Type":"question","MessageId":"rpl_VJx_20251223_020145_889_xiiAQ0s2","Name":"question","Title":"用户提问","Icon":"","Status":"success","StatusDesc":"","Contents":[{"Type":"text","Text":"你好"}],"ExtraInfo":{"Elapsed":"0","StartTime":"1766426505000"}}],"ExtraInfo":{"RequestId":"ExmjIGL6Oy-7035160285","TraceId":"b3dcff6179e3a289b8b29dfb3d302bba / VJx_20251223_020145_889_xiiAQ0s2","Elapsed":"0","StartTime":"1766426505000","IsFromSelf":true,"IsLlmGenerated":false,"CanRating":false,"CanFeedback":false,"ReplyMethod":0,"FromName":"","FromAvatar":"","HasRead":false}}}

event: response.created
data: {"Type":"response.created","Response":{"Role":"assistant","RecordId":"I5m_20251223_020145_960_5mpLgT4a","RelatedRecordId":"VJx_20251223_020145_889_xiiAQ0s2","ConversationId":"d4bca75f-e713-42c4-ad37-67b7d6621f10","Status":"processing","StatusDesc":"","ExtraInfo":{"RequestId":"ExmjIGL6Oy-7035160285","TraceId":"b3dcff6179e3a289b8b29dfb3d302bba / I5m_20251223_020145_960_5mpLgT4a","Elapsed":"2089","StartTime":"1766426505888","IsFromSelf":false,"IsLlmGenerated":false,"CanRating":false,"CanFeedback":false,"ReplyMethod":0,"FromName":"","FromAvatar":"","HasRead":false}}}

event: response.processing
data: {"Type":"response.processing","Response":{"Role":"assistant","RecordId":"I5m_20251223_020145_960_5mpLgT4a","RelatedRecordId":"VJx_20251223_020145_889_xiiAQ0s2","ConversationId":"d4bca75f-e713-42c4-ad37-67b7d6621f10","Status":"processing","StatusDesc":"大模型直接回复","ExtraInfo":{"RequestId":"ExmjIGL6Oy-7035160285","TraceId":"b3dcff6179e3a289b8b29dfb3d302bba / I5m_20251223_020145_960_5mpLgT4a","Elapsed":"2089","StartTime":"1766426505888","IsFromSelf":false,"IsLlmGenerated":false,"CanRating":false,"CanFeedback":false,"ReplyMethod":0,"FromName":"","FromAvatar":"","HasRead":false}}}

event: response.processing
data: {"Type":"response.processing","Response":{"Role":"assistant","RecordId":"I5m_20251223_020145_960_5mpLgT4a","RelatedRecordId":"VJx_20251223_020145_889_xiiAQ0s2","ConversationId":"d4bca75f-e713-42c4-ad37-67b7d6621f10","Status":"processing","StatusDesc":"大模型直接回复","ExtraInfo":{"RequestId":"ExmjIGL6Oy-7035160285","TraceId":"b3dcff6179e3a289b8b29dfb3d302bba / I5m_20251223_020145_960_5mpLgT4a","Elapsed":"2373","StartTime":"1766426505888","IsFromSelf":false,"IsLlmGenerated":false,"CanRating":false,"CanFeedback":false,"ReplyMethod":1,"FromName":"","FromAvatar":"","HasRead":false}}}

event: message.added
data: {"Type":"message.added","Message":{"Type":"reply","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","Name":"reply","Title":"","Icon":"","Status":"processing","StatusDesc":"回复中","ExtraInfo":{"Elapsed":"0","StartTime":"1766426508261"}}}

event: content.added
data: {"Type":"content.added","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","ContentIndex":0,"Content":{"Type":"text"}}

event: text.delta
data: {"Type":"text.delta","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","ContentIndex":0,"Text":"您好！请问有什么可以帮您的"}

event: response.processing
data: {"Type":"response.processing","Response":{"Role":"assistant","RecordId":"I5m_20251223_020145_960_5mpLgT4a","RelatedRecordId":"VJx_20251223_020145_889_xiiAQ0s2","ConversationId":"d4bca75f-e713-42c4-ad37-67b7d6621f10","Status":"processing","StatusDesc":"大模型直接回复","ExtraInfo":{"RequestId":"ExmjIGL6Oy-7035160285","TraceId":"b3dcff6179e3a289b8b29dfb3d302bba / I5m_20251223_020145_960_5mpLgT4a","Elapsed":"2488","StartTime":"1766426505888","IsFromSelf":false,"IsLlmGenerated":false,"CanRating":false,"CanFeedback":false,"ReplyMethod":1,"FromName":"","FromAvatar":"","HasRead":false}}}

event: message.processing
data: {"Type":"message.processing","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","Message":{"Type":"reply","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","Name":"reply","Title":"","Icon":"","Status":"processing","StatusDesc":"回复中","ExtraInfo":{"Elapsed":"127","StartTime":"1766426508261"}}}

event: text.delta
data: {"Type":"text.delta","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","ContentIndex":0,"Text":"？"}

event: message.done
data: {"Type":"message.done","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","Message":{"Type":"reply","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","Name":"reply","Title":"","Icon":"","Status":"success","StatusDesc":"回复完成","Contents":[{"Type":"text","Text":"您好！请问有什么可以帮您的？"}],"ExtraInfo":{"Elapsed":"201","StartTime":"1766426508261"}}}

event: response.completed
data: {"Type":"response.completed","Response":{"Role":"assistant","RecordId":"I5m_20251223_020145_960_5mpLgT4a","RelatedRecordId":"VJx_20251223_020145_889_xiiAQ0s2","ConversationId":"d4bca75f-e713-42c4-ad37-67b7d6621f10","Status":"success","StatusDesc":"大模型直接回复","Messages":[{"Type":"reply","MessageId":"rpl_I5m_20251223_020145_960_5mpLgT4a","Name":"reply","Title":"","Icon":"","Status":"success","StatusDesc":"回复完成","Contents":[{"Type":"text","Text":"您好！请问有什么可以帮您的？"}],"ExtraInfo":{"Elapsed":"201","StartTime":"1766426508261"}}],"Procedures":[{"Name":"large_language_model","Title":"大模型直接回复","Status":"success","IntentCate":"self_awareness","ResourceStatus":0,"Type":"agent","Agent":{"ModelName":"","Input":"","Output":"","Content":"你好","System":"你是腾讯云智能体开发平台，一个善于进行文档阅读、知识问答的智能平台。具备海量的行业知识，对垂直行业超过10项基础能力进行精调优化，涵盖了文档阅读理解、语义理解、安全合规等，可以帮助用户解决行业问题、提高工作效率。 请注意现在的时间是 2025-12-23 02:01:47 ","RewriteQuery":""}}],"StatInfo":{"InputTokens":0,"OutputTokens":0,"TotalTokens":602,"TotalCost":"2574"},"ExtraInfo":{"RequestId":"ExmjIGL6Oy-7035160285","TraceId":"b3dcff6179e3a289b8b29dfb3d302bba / I5m_20251223_020145_960_5mpLgT4a","Elapsed":"2676","StartTime":"1766426505888","IsFromSelf":false,"IsLlmGenerated":true,"CanRating":true,"CanFeedback":false,"ReplyMethod":1,"FromName":"rodneyxiong_standard","FromAvatar":"https://cdn.xiaowei.qq.com/static/adp/app-default-avatar.png","HasRead":false}}}

event: done
data: [DONE]
```

### 1.4 Postman 调用示例

（文档中此节无详细内容）

## 2. HTTP SSE 接口返回

### 2.1 请求确认事件

事件名：`request_ack`
事件方向：后端 > 前端

> **注意**：如果发送的请求事件因为安全审核被拦截，或者请求超过并发数被拦截，后台不会返回请求确认事件，而是返回错误事件。

**数据结构**：

| 名称       | 类型   | 是否必填 | 说明                           |
| ---------- | ------ | -------- | ------------------------------ |
| Type       | String | 是       | 事件类型，固定为 request_ack。 |
| RequestAck |        | 是       | 请求确认信息。                 |

**示例**：

```
event: request_ack
data: {
    "Type": "request_ack",
    "RequestAck": {
        "Role": "user",
        "RecordId": "p9G_20251220_225838_112_Pr4FhDcR",
        "ConversationId": "362b3aac-b7eb-4d2a-a154-204f288a4727",
        "Status": "success",
        "StatusDesc": "请求成功",
        "Messages": [
            {
                "Type": "question",
                "MessageId": "rpl_p9G_20251220_225838_112_Pr4FhDcR",
                "Name": "question",
                "Title": "用户提问",
                "Icon": "",
                "Status": "success",
                "StatusDesc": "",
                "Contents": [
                    {
                        "Type": "text",
                        "Text": "给我生成一张小猫图片"
                    }
                ],
                "ExtraInfo": {
                    "Elapsed": "0",
                    "StartTime": "1766242718000"
                }
            }
        ],
        "ExtraInfo": {
            "RequestId": "62bcA7Vknu-8384773123",
            "TraceId": "77fc0a9ce9ba9f3b511ac28ad05b5bab / p9G_20251220_225838_112_Pr4FhDcR",
            "Elapsed": "0",
            "StartTime": "1766242718000",
            "IsFromSelf": true,
            "IsLlmGenerated": false,
            "CanRating": false,
            "CanFeedback": false,
            "ReplyMethod": 0,
            "FromName": "",
            "FromAvatar": "",
            "HasRead": false
        }
    }
}
```

### 2.2 响应创建事件

事件名：`response.created`
事件方向：后端 > 前端

**数据结构**：

| 名称     | 类型   | 是否必填 | 说明                                |
| -------- | ------ | -------- | ----------------------------------- |
| Type     | String | 是       | 事件类型，固定为 response.created。 |
| Response |        | 是       | 响应信息。                          |

**示例**：

```
event: response.created
data: {
    "Type": "response.created",
    "Response": {
        "Role": "assistant",
        "RecordId": "09b_20251220_225838_205_QOy26o1I",
        "RelatedRecordId": "p9G_20251220_225838_112_Pr4FhDcR",
        "ConversationId": "362b3aac-b7eb-4d2a-a154-204f288a4727",
        "Status": "processing",
        "StatusDesc": "",
        "ExtraInfo": {
            "RequestId": "62bcA7Vknu-8384773123",
            "TraceId": "77fc0a9ce9ba9f3b511ac28ad05b5bab / 09b_20251220_225838_205_QOy26o1I",
            "Elapsed": "613",
            "StartTime": "1766242718110",
            "IsFromSelf": false,
            "IsLlmGenerated": false,
            "CanRating": false,
            "CanFeedback": false,
            "ReplyMethod": 0,
            "AgentName": "TT专家智能体-莫娇-prod",
            "AgentIcon": "https://qidian-qbot-1251316161.cos.ap-guangzhou.myqcloud.com/public/1864611313395171328/1937770498688842432/image/KxWmEzYIEKTUASYsrNmK-1939497567585093952.png",
            "HasRead": false
        }
    }
}
```

### 2.3 响应处理中事件

事件名：`response.processing`
事件方向：后端 > 前端

**数据结构**：

| 名称     | 类型   | 是否必填 | 说明                                   |
| -------- | ------ | -------- | -------------------------------------- |
| Type     | String | 是       | 事件类型，固定为 response.processing。 |
| Response |        | 是       | 响应信息。                             |

**示例**：

```
event: response.processing
data: {
    "Type": "response.processing",
    "Response": {
        "Role": "assistant",
        "RecordId": "09b_20251220_225838_205_QOy26o1I",
        "RelatedRecordId": "p9G_20251220_225838_112_Pr4FhDcR",
        "ConversationId": "362b3aac-b7eb-4d2a-a154-204f288a4727",
        "Status": "processing",
        "StatusDesc": "调用思考模型",
        "ExtraInfo": {
            "RequestId": "62bcA7Vknu-8384773123",
            "TraceId": "77fc0a9ce9ba9f3b511ac28ad05b5bab / 09b_20251220_225838_205_QOy26o1I",
            "Elapsed": "3592",
            "StartTime": "1766242718110",
            "IsFromSelf": false,
            "IsLlmGenerated": false,
            "CanRating": false,
            "CanFeedback": false,
            "ReplyMethod": 0,
            "FromName": "",
            "FromAvatar": "",
            "HasRead": false
        }
    }
}
```

### 2.4 消息增加事件

事件名：`message.added`
事件方向：后端 > 前端

**数据结构**：

| 名称    | 类型   | 是否必填 | 说明                             |
| ------- | ------ | -------- | -------------------------------- |
| Type    | String | 是       | 事件类型，固定为 message.added。 |
| Message |        | 是       | 消息信息。                       |

**示例**：

```
event: message.added
data: {
    "Type": "message.added",
    "Message": {
        "Type": "thought",
        "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
        "Name": "thought",
        "Title": "思考",
        "Icon": "https://lke-realtime-1251316161.cos.ap-guangzhou.myqcloud.com/icon/thinking.png",
        "Status": "processing",
        "StatusDesc": "思考中",
        "ExtraInfo": {
            "Elapsed": "1785",
            "StartTime": "1766242718724",
            "AgentName": "TT专家智能体-莫娇-prod",
            "AgentIcon": "https://qidian-qbot-1251316161.cos.ap-guangzhou.myqcloud.com/public/1864611313395171328/1937770498688842432/image/KxWmEzYIEKTUASYsrNmK-1939497567585093952.png"
        }
    }
}
```

### 2.5 消息处理中事件

事件名：`message.processing`
事件方向：后端 > 前端

**数据结构**：

| 名称      | 类型   | 是否必填 | 说明                                  |
| --------- | ------ | -------- | ------------------------------------- |
| Type      | String | 是       | 事件类型，固定为 message.processing。 |
| MessageId | String | 是       | 处理中的消息 ID。                     |
| Message   |        | 是       | 消息状态信息。                        |

**示例**：

```
event: message.processing
data: {
    "Type": "message.processing",
    "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
    "Message": {
        "Type": "thought",
        "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
        "Name": "thought",
        "Title": "思考",
        "Icon": "https://lke-realtime-1251316161.cos.ap-guangzhou.myqcloud.com/icon/thinking.png",
        "Status": "processing",
        "StatusDesc": "思考中",
        "ExtraInfo": {
            "Elapsed": "1887",
            "StartTime": "1766242718724"
        }
    }
}
```

### 2.6 内容增加事件

事件名：`content.added`
事件方向：后端 > 前端

**数据结构**：

| 名称         | 类型   | 是否必填 | 说明                             |
| ------------ | ------ | -------- | -------------------------------- |
| Type         | String | 是       | 事件类型，固定为 content.added。 |
| MessageId    | String | 是       | 增加内容的消息对应的消息 ID。    |
| ContentIndex | Number | 是       | 增加内容的序号。                 |
| Content      |        | 是       | 内容信息。                       |

**示例**：

```
event: content.added
data: {
    "Type": "content.added",
    "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
    "ContentIndex": 0,
    "Content": {
        "Type": "text",
        "Text": "当前"
    }
}
```

### 2.7 文本内容增量事件

事件名：`text.delta`
事件方向：后端 > 前端

**数据结构**：

| 名称         | 类型   | 是否必填 | 说明                              |
| ------------ | ------ | -------- | --------------------------------- |
| Type         | String | 是       | 事件类型，固定为 text.delta。     |
| MessageId    | String | 是       | 增加文本内容的消息对应的消息 ID。 |
| ContentIndex | Number | 是       | 增加文本内容的序号。              |
| Text         | String | 是       | 增加的文本内容。                  |

**示例**：

```
event: text.delta
data: {
    "Type": "text.delta",
    "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
    "ContentIndex": 0,
    "Text": "任务"
}
```

### 2.8 文本内容替换事件

事件名：`text.replace`
事件方向：后端 > 前端

**数据结构**：

| 名称         | 类型   | 是否必填 | 说明                              |
| ------------ | ------ | -------- | --------------------------------- |
| Type         | String | 是       | 事件类型，固定为 text.replace。   |
| MessageId    | String | 是       | 替换文本内容的消息对应的消息 ID。 |
| ContentIndex | Number | 是       | 替换文本内容的序号。              |
| Text         | String | 是       | 替换的文本内容。                  |

**示例**：

```
event: text.replace
data: {
    "Type": "text.replace",
    "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
    "ContentIndex": 0,
    "Text": "当前的任务是为用户生成"
}
```

### 2.9 消息处理完成事件

事件名：`message.done`
事件方向：后端 > 前端

**数据结构**：

| 名称      | 类型   | 是否必填 | 说明                            |
| --------- | ------ | -------- | ------------------------------- |
| Type      | String | 是       | 事件类型，固定为 message.done。 |
| MessageId | String | 是       | 处理完成的消息 ID。             |
| Message   |        | 是       | 处理完成的消息信息。            |

**示例**：

```
event: message.done
data: {
    "Type": "message.done",
    "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
    "Message": {
        "Type": "thought",
        "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
        "Name": "thought",
        "Title": "思考",
        "Icon": "https://lke-realtime-1251316161.cos.ap-guangzhou.myqcloud.com/icon/thinking.png",
        "Status": "success",
        "StatusDesc": "思考完成",
        "Contents": [
            {
                "Type": "text",
                "Text": "当前任务是为用户生成一张小猫图片，这在我的专业能力中不直接涉及图像创作，但我有能力调用文生图工具来完成这个需求。任务明确，不应再询问用户信息；直接执行文生图即可。"
            }
        ],
        "ExtraInfo": {
            "Elapsed": "2679",
            "StartTime": "1766242718724",
            "AgentName": "TT专家智能体-莫娇-prod",
            "AgentIcon": "https://qidian-qbot-1251316161.cos.ap-guangzhou.myqcloud.com/public/1864611313395171328/1937770498688842432/image/KxWmEzYIEKTUASYsrNmK-1939497567585093952.png"
        }
    }
}
```

### 2.10 响应完成事件

事件名：`response.completed`
事件方向：后端 > 前端

**数据结构**：

| 名称     | 类型   | 是否必填 | 说明                                  |
| -------- | ------ | -------- | ------------------------------------- |
| Type     | String | 是       | 事件类型，固定为 response.completed。 |
| Response |        | 是       | 处理完成的完整响应内容。              |

**示例**（结构示意，完整示例较长）：

```
event: response.completed
data: {
    "Type": "response.completed",
    "Response": {
        "Role": "assistant",
        "RecordId": "09b_20251220_225838_205_QOy26o1I",
        "RelatedRecordId": "p9G_20251220_225838_112_Pr4FhDcR",
        "ConversationId": "362b3aac-b7eb-4d2a-a154-204f288a4727",
        "Status": "success",
        "StatusDesc": "调用思考模型",
        "Messages": [
            {
                "Type": "thought",
                "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_0",
                "Name": "thought",
                "Title": "思考",
                "Icon": "https://lke-realtime-1251316161.cos.ap-guangzhou.myqcloud.com/icon/thinking.png",
                "Status": "success",
                "StatusDesc": "思考完成",
                "Contents": [
                    {
                        "Type": "text",
                        "Text": "当前任务是为用户生成一张小猫图片……"
                    }
                ],
                "ExtraInfo": {
                    "Elapsed": "2679",
                    "StartTime": "1766242718724",
                    "AgentName": "TT专家智能体-莫娇-prod",
                    "AgentIcon": "https://qidian-qbot-1251316161.cos.ap-guangzhou.myqcloud.com/public/1864611313395171328/1937770498688842432/image/KxWmEzYIEKTUASYsrNmK-1939497567585093952.png"
                }
            },
            {
                "Type": "tool_call",
                "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_1",
                "Name": "文生图",
                "Title": "工具执行",
                "Icon": "https://qidian-qbot-1251316161.cos.ap-guangzhou.myqcloud.com/public/workflow-tool.svg",
                "Status": "success",
                "StatusDesc": "工具执行完成",
                "Contents": [
                    {
                        "Type": "json_text",
                        "Text": "{\"code\":0,\"msg\":\"ok\",\"output\":\"{\\\"img_url\\\":\\\"https://edu-aigc-1307578727.cos.ap-guangzhou.tencentcos.cn/ai-image/2025-12-20/0df7d8d44ec94c3b87a87f132b421bb8.png\\\",\\\"request_id\\\":\\\"9dacdd61-485d-421d-8bd9-79449b85a1c2\\\"}\",\"response\":\"\"}"
                    }
                ],
                "ExtraInfo": {
                    "Elapsed": "5231",
                    "StartTime": "1766242721713",
                    "ToolName": "工作流/文生图",
                    "ToolIcon": "https://qidian-qbot-1251316161.cos.ap-guangzhou.myqcloud.com/public/workflow-tool.svg"
                }
            },
            {
                "Type": "thought",
                "MessageId": "prc_09b_20251220_225838_205_QOy26o1I_2",
                "Name": "thought",
                "Title": "思考",
                "Icon": "https://lke-realtime-1251316161.cos.ap-guangzhou.myqcloud.com/icon/thinking.png",
                "Status": "success",
                "StatusDesc": "思考完成",
                "Contents": [
                    {
                        "Type": "text",
                        "Text": "我已使用文生图工具生成了小猫图片并得到了返回的链接，接下来应直接向用户展示图片并用自然的语言收尾，然后结束任务。"
                    }
                ],
                "ExtraInfo": {
                    "Elapsed": "2190",
                    "StartTime": "1766242727002",
                    "AgentName": "TT专家智能体-莫娇-prod",
                    "AgentIcon": "https://qidian-qbot-1251316161.cos.ap-guangzhou.myqcloud.com/public/1864611313395171328/1937770498688842432/image/KxWmEzYIEKTUASYsrNmK-1939497567585093952.png"
                }
            },
            {
                "Type": "reply",
                "MessageId": "rpl_09b_20251220_225838_205_QOy26o1I",
                "Name": "reply",
                "Title": "",
                "Icon": "",
                "Status": "success",
                "StatusDesc": "回复完成",
                "Contents": [
                    {
                        "Type": "text",
                        "Text": "好的，我和你一起构思了一个温暖简洁的场景，用柔和的色彩呈现了一只可爱的小猫，姿态悠闲，带着自然与生活的情趣。请看——\n\n![可爱的小猫](https://edu-aigc-1307578727.cos.ap-guangzhou.tencentcos.cn/ai-image/2025-12-20/0df7d8d44ec94c3b87a87f132b421bb8.png)\n\n在设计里，我们常说"器物承载寓意、意趣和道义"，其实小动物也能成为生活中温柔的陪伴与灵感的来源。希望这张图片能给你带来片刻的轻松与愉悦。"
                    }
                ],
                "ExtraInfo": {
                    "Elapsed": "1549",
                    "StartTime": "1766242729698"
                }
            }
        ],
        "Procedures": [
            {
                "Name": "thinking_model",
                "Title": "调用思考模型",
                "Status": "success",
                "IntentCate": "",
                "ResourceStatus": 0,
                "StatInfos": [
                    {
                        "InputTokens": 2884,
                        "OutputTokens": 86,
                        "TotalTokens": 2970,
                        "ModelName": "100021406304-OpenAI/ouFPaKKL"
                    }
                ],
                "Type": "agent",
                "Agent": {
                    "ModelName": "100021406304-OpenAI/ouFPaKKL",
                    "Input": "",
                    "Output": "[{\"id\":\"call_af6a8234f2180102\",\"type\":\"function\",\"function\":{\"name\":\"文生图\",\"arguments\":\"{\\\"prompt\\\":\\\"一只可爱的小猫，毛色柔和，姿态悠闲，场景温暖简洁，体现自然与生活情趣\\\"}\"},\"index\":0}]\n",
                    "Content": "",
                    "RewriteQuery": ""
                }
            },
            {
                "Name": "tool_call",
                "Title": "调用文生图",
                "Status": "success",
                "IntentCate": "",
                "ResourceStatus": 0,
                "Type": "agent",
                "Agent": {
                    "ModelName": "",
                    "Input": "{\"name\":\"文生图\",\"arguments\":\"{\\\"prompt\\\":\\\"一只可爱的小猫，毛色柔和，姿态悠闲，场景温暖简洁，体现自然与生活情趣\\\"}\"}\n",
                    "Output": "{\"code\":0,\"msg\":\"ok\",\"output\":\"{\\\"img_url\\\":\\\"https://edu-aigc-1307578727.cos.ap-guangzhou.tencentcos.cn/ai-image/2025-12-20/0df7d8d44ec94c3b87a87f132b421bb8.png\\\",\\\"request_id\\\":\\\"9dacdd61-485d-421d-8bd9-79449b85a1c2\\\"}\",\"response\":\"\"}",
                    "Content": "",
                    "RewriteQuery": ""
                }
            },
            {
                "Name": "thinking_model",
                "Title": "调用思考模型",
                "Status": "success",
                "IntentCate": "",
                "ResourceStatus": 0,
                "StatInfos": [
                    {
                        "InputTokens": 3072,
                        "OutputTokens": 168,
                        "TotalTokens": 3240,
                        "ModelName": "100021406304-OpenAI/ouFPaKKL"
                    }
                ],
                "Type": "agent",
                "Agent": {
                    "ModelName": "100021406304-OpenAI/ouFPaKKL",
                    "Input": "",
                    "Output": "[{\"id\":\"call_fa9caa370c1d7aad\",\"type\":\"function\",\"function\":{\"name\":\"task_done\",\"arguments\":\"{\\\"success\\\": true, \\\"final_response\\\": \\\"好的，我和你一起构思了一个温暖简洁的场景，用柔和的色彩呈现了一只可爱的小猫，姿态悠闲，带着自然与生活的情趣。请看——\\\\n\\\\n![可爱的小猫](https://edu-aigc-1307578727.cos.ap-guangzhou.tencentcos.cn/ai-image/2025-12-20/0df7d8d44ec94c3b87a87f132b421bb8.png)\\\\n\\\\n在设计里，我们常说"器物承载寓意、意趣和道义"，其实小动物也能成为生活中温柔的陪伴与灵感的来源。希望这张图片能给你带来片刻的轻松与愉悦。\\\"}\"},\"index\":0}]\n",
                    "Content": "",
                    "RewriteQuery": ""
                }
            }
        ],
        "StatInfo": {
            "InputTokens": 5956,
            "OutputTokens": 254,
            "TotalTokens": 6210,
            "TotalCost": "13137"
        },
        "ExtraInfo": {
            "RequestId": "62bcA7Vknu-8384773123",
            "TraceId": "77fc0a9ce9ba9f3b511ac28ad05b5bab / 09b_20251220_225838_205_QOy26o1I",
            "Elapsed": "13142",
            "StartTime": "1766242718110",
            "IsFromSelf": false,
            "IsLlmGenerated": true,
            "CanRating": true,
            "CanFeedback": false,
            "ReplyMethod": 18,
            "FromName": "专家智能体测试",
            "FromAvatar": "https://qidian-qbot-1251316161.cos.ap-guangzhou.myqcloud.com/public/1864611313395171328/1937770498688842432/image/KxWmEzYIEKTUASYsrNmK-1939497567585093952.png",
            "HasRead": false
        }
    }
}
```

### 2.11 角标增加事件

事件名：`quote_info.added`
事件方向：后端 > 前端

**数据结构**：

| 名称         | 类型   | 是否必填 | 说明                                |
| ------------ | ------ | -------- | ----------------------------------- |
| Type         | String | 是       | 事件类型，固定为 quote_info.added。 |
| MessageId    | String | 是       | 增加角标的消息对应的消息 ID。       |
| ContentIndex | Number | 是       | 增加角标内容的序号。                |
| QuoteInfo    |        | 是       | 角标信息。                          |

**示例**：

```
event: quote_info.added
data: {
    "Type": "quote_info.added",
    "MessageId": "rpl_Uty_20251223_003736_015_GV5QLEq4",
    "ContentIndex": 0,
    "QuoteInfo": {
        "Position": 240,
        "Index": 1
    }
}
```

### 2.12 引文增加事件

事件名：`reference.added`
事件方向：后端 > 前端

**数据结构**：

| 名称         | 类型   | 是否必填 | 说明                               |
| ------------ | ------ | -------- | ---------------------------------- |
| Type         | String | 是       | 事件类型，固定为 reference.added。 |
| MessageId    | String | 是       | 增加引文内容的消息对应的消息 ID。  |
| ContentIndex | Number | 是       | 增加引文内容的序号。               |
| Reference    |        | 是       | 引文信息。                         |

**示例**：

```
event: reference.added
data: {
    "Type": "reference.added",
    "MessageId": "rpl_Uty_20251223_003736_015_GV5QLEq4",
    "ContentIndex": 0,
    "Reference": {
        "Index": 1,
        "Name": "深大新闻-深圳大学",
        "Type": 4,
        "WebSearchRefer": {
            "Url": "http://www.szu.edu.cn/index/sdxw.htm"
        }
    }
}
```

### 2.13 错误事件

事件名：`error`
事件方向：后端 > 前端

**数据结构**：

| 名称  | 类型   | 是否必填 | 说明                     |
| ----- | ------ | -------- | ------------------------ |
| Type  | String | 是       | 事件类型，固定为 error。 |
| Error |        | 是       | 错误信息。               |

**示例**：

```
event: error
data: {
    "Type": "error",
    "Error": {
        "Code": 460501,
        "Message": "我无法回答这个问题，您可以尝试问我其它问题。",
        "RequestId": "A9EhrrijXs-5778188698",
        "TraceId": "fda66d6df4cc43128ca76852cca66191",
        "Elapsed": "165",
        "StartTime": "1766421936159"
    }
}
```

## 3. 公共数据结构

### Content

消息内容信息的数据结构：

| 名称            | 类型               | 是否必填 | 说明                                                         |
| --------------- | ------------------ | -------- | ------------------------------------------------------------ |
| Type            | String             | 是       | 消息内容类型：text：文本；image：图片；file：文件；option_cards：选项卡（选项卡片）；custom_params：用户自定义参数；sandbox：沙箱信息；web_search：联网搜索信息；file_collection：文件收集信息；json_text：JSON 文本；custom_variables：用户自定义变量；e_chart：ECharts 图表信息；widget：widget 信息；tasks：任务信息；questionnaire：问卷（配合 reply 消息，表示需人工交互/等待用户作答）；widget_action：widget 动作信息 |
| Text            | String             | 否       | 文本内容。                                                   |
| Image           |                    | 否       | 图片信息。                                                   |
| File            |                    | 否       | 文件信息。                                                   |
| CustomVariables | Map[String]String  | 否       | 自定义变量信息。**注意**：自定义变量对应的是应用配置中的 API 参数，如果应用配置中的参数名称是 API.xxx，在请求中的格式就是："xxx":"value"。 |
| WidgetAction    |                    | 否       | Widget 动作信息。                                            |
| QuoteInfos      | Array of QuoteInfo | 否       | 引用信息（可选）。                                           |
| References      | Array of Reference | 否       | 参考文献信息（可选）。                                       |
| OptionCards     | Array of String    | 否       | 选项卡信息（可选）。                                         |
| OptionMode      | Number             | 否       | 标识 OptionCards 是否为多选：0 或 null：单选；1：多选        |
| CustomParams    | Array of String    | 否       | 用户自定义业务参数信息（可选）。                             |
| Sandbox         |                    | 否       | 沙盒信息。                                                   |
| WebSearch       |                    | 否       | 网页搜索内容。                                               |
| FileCollection  |                    | 否       | 文件收集信息。                                               |
| RelatedRecordId | String             | 否       | 关联的 RecordId。                                            |
| Widget          |                    | 否       | Widget 信息。                                                |
| Tasks           | Array of AgentTask | 否       | 任务执行信息                                                 |
| Questionnaire   |                    | 否       | 问卷信息                                                     |
| EChart          | String             | 否       | EChart 图表的 JSON 数据                                      |

### Image

图片信息的数据结构：

| 名称 | 类型   | 是否必填 | 说明                                                         |
| ---- | ------ | -------- | ------------------------------------------------------------ |
| Url  | String | 是       | 文件 URL。**注意**：请使用上传到 ADP 的文件链接（参考离线文档上传），或者可以公开访问的文件链接。 |

### FileInfo

文件信息的数据结构：

| 名称       | 类型   | 是否必填 | 说明                                                         |
| ---------- | ------ | -------- | ------------------------------------------------------------ |
| FileName   | String | 是       | 文件名称。                                                   |
| FileSize   | String | 是       | 文件大小。                                                   |
| FileUrl    | String | 是       | 文件 URL。**注意**：请使用上传到 ADP 的文件链接（参考离线文档上传），或者可以公开访问的文件链接。 |
| FileType   | String | 是       | 文件类型。                                                   |
| DocId      | String | 否       | 实时文档解析接口返回的 doc_id。**注意**：在使用标准模式的文件对话时，这个字段必填。 |
| CreateTime | Number | 是       | 文件创建时间，单位 ms。                                      |

### Widget

Widget 信息：

| 名称        | 类型   | 是否必填 | 说明                                                         |
| ----------- | ------ | -------- | ------------------------------------------------------------ |
| WidgetId    | String | 是       | Widget 配置 ID。                                             |
| WidgetRunId | String | 是       | Widget 实例 ID。                                             |
| State       | String | 是       | Widget 状态数据，用于兜底显示。                              |
| Position    | Number | 否       | Widget 位置。                                                |
| View        | String | 是       | JSON 格式的 Widget 信息。**注意**：Widget 信息的渲染可以使用腾讯开发的基于 Web Components 标准开发的跨框架 UI 组件 SDK。SDK 说明文档详见 ADP-Widget SDK。 |

### WidgetAction

Widget 动作信息的数据结构：

| 名称        | 类型   | 是否必填 | 说明                    |
| ----------- | ------ | -------- | ----------------------- |
| WidgetId    | String | 是       | Widget 配置 ID。        |
| WidgetRunId | String | 是       | Widget 实例 ID。        |
| ActionType  | String | 是       | Widget 动作。           |
| Payload     | String | 是       | Widget 动作提交的数据。 |

### QuoteInfo

参考来源索引：

| 名称     | 类型   | 是否必填 | 说明               |
| -------- | ------ | -------- | ------------------ |
| Position | Number | 是       | 参考来源位置。     |
| Index    | Number | 是       | 参考来源索引顺序。 |

### Reference

参考文献信息：

| 名称           | 类型   | 是否必填 | 说明                                                         |
| -------------- | ------ | -------- | ------------------------------------------------------------ |
| Index          | Number | 是       | 引用来源索引 ID。                                            |
| Type           | Number | 是       | 参考来源类型。1：问答；2：文档片段；4：联网检索到的内容；5：知识图谱 |
| Name           | String | 是       | 参考来源名称。                                               |
| DocRefer       |        | 否       | 文档片段参考信息。                                           |
| QaRefer        |        | 否       | 问答参考信息。                                               |
| WebSearchRefer |        | 否       | 联网检索内容参考信息。                                       |
| GraphRAGRefer  |        | 否       | 知识图谱参考信息。                                           |

### GraphRAGRefer

知识图谱参考信息：

| 名称          | 类型   | 是否必填 | 说明                                                         |
| ------------- | ------ | -------- | ------------------------------------------------------------ |
| ReferenceId   | String | 是       | 知识图谱参考 ID，该字段对应 DescribeRefer 中的 ReferBizIds 字段。 |
| KnowledgeId   | String | 是       | 知识图谱所在知识库业务 ID。                                  |
| KnowledgeName | String | 否       | 知识图谱所在知识库名称。                                     |

### DocRefer

文档片段参考信息：

| 名称          | 类型   | 是否必填 | 说明                                                         |
| ------------- | ------ | -------- | ------------------------------------------------------------ |
| ReferenceId   | String | 是       | 文档片段参考 ID，该字段对应 DescribeRefer 中的 ReferBizIds 字段。 |
| DocId         | String | 是       | 文档业务 ID。                                                |
| DocName       | String | 是       | 文档名称。                                                   |
| KnowledgeId   | String | 是       | 文档所在知识库业务 ID。                                      |
| KnowledgeName | String | 否       | 文档所在知识库名称。                                         |
| Url           | String | 是       | 文档访问地址。                                               |

### QaRefer

问答参考信息：

| 名称          | 类型   | 是否必填 | 说明                                                         |
| ------------- | ------ | -------- | ------------------------------------------------------------ |
| ReferenceId   | String | 是       | 文档片段参考 ID，该字段对应 DescribeRefer 中的 ReferBizIds 字段。 |
| QaId          | String | 是       | 问答业务 ID。                                                |
| KnowledgeId   | String | 是       | 问答所在知识库业务 ID。                                      |
| KnowledgeName | String | 否       | 问答所在知识库名称。                                         |

### WebSearchRefer

联网检索内容参考信息：

| 名称 | 类型   | 是否必填 | 说明       |
| ---- | ------ | -------- | ---------- |
| Url  | String | 是       | 网页 URL。 |

### Sandbox

沙盒信息内容：

| 名称       | 类型   | 是否必填 | 说明                            |
| ---------- | ------ | -------- | ------------------------------- |
| Url        | String | 否       | 沙盒的 URL 地址。               |
| DisplayUrl | String | 否       | 沙盒通过浏览器打开的 URL 地址。 |
| Content    | String | 否       | 沙盒的输出内容。                |

### WebSearch

网页搜索内容：

| 名称    | 类型   | 是否必填 | 说明                               |
| ------- | ------ | -------- | ---------------------------------- |
| Content | String | 是       | 网页搜索结果，json 格式的 string。 |

### FileCollection

文件收集信息：

| 名称               | 类型            | 是否必填 | 说明                 |
| ------------------ | --------------- | -------- | -------------------- |
| MaxFileCount       | Number          | 是       | 最大上传文件的数量。 |
| SupportedFileTypes | Array of String | 是       | 支持的上传文件类型。 |

### Record

消息记录信息：

| 名称            | 类型               | 是否必填 | 说明                                                         |
| --------------- | ------------------ | -------- | ------------------------------------------------------------ |
| Role            | String             | 是       | 角色类型。user：代表该条消息内容是用户发送的。assistant：代表该条消息内容是智能体发送的。 |
| RecordId        | String             | 是       | 当前记录的 ID。                                              |
| RelatedRecordId | String             | 否       | 关联记录的 ID。                                              |
| ConversationId  | String             | 是       | 会话的唯一标识符。                                           |
| Status          | String             | 是       | 当前状态，如 "processing"。                                  |
| StatusDesc      | String             | 是       | 状态描述。                                                   |
| Messages        | Array of Message   | 否       | 消息列表，包含多个消息对象。                                 |
| Procedures      | Array of Procedure | 否       | 处理过程列表，包含多个过程对象。                             |
| StatInfo        |                    | 否       | 统计信息。                                                   |
| ExtraInfo       |                    | 否       | 扩展信息。                                                   |

### RecordExtraInfo

消息记录扩展信息：

| 名称           | 类型    | 是否必填 | 说明                                                         |
| -------------- | ------- | -------- | ------------------------------------------------------------ |
| RequestId      | String  | 是       | 请求 ID，用于标识一个请求（作为消息串联，建议每个请求使用不同的 RequestId）。 |
| TraceId        | String  | 是       | 链路 ID。                                                    |
| Elapsed        | Number  | 是       | 事件耗时, 单位 ms。                                          |
| StartTime      | Number  | 是       | 事件开始时间，单位 ms。                                      |
| EndTime        | Number  | 是       | 事件结束时间，单位 ms。                                      |
| IsFromSelf     | Boolean | 是       | 消息是否由客户端发出。                                       |
| IsLlmGenerated | Boolean | 否       | 是否为模型生成内容。                                         |
| CanRating      | Boolean | 否       | 该消息记录是否能评价。                                       |
| CanFeedback    | Boolean | 否       | 该消息记录是否能反馈。**注意**：此字段可能返回 null，表示取不到有效值。示例值：false |
| ReplyMethod    | Number  | 否       | 回复方式。1：大模型回复；2：未知问题回复；3：拒答问题回复；4：敏感回复；5：已采纳问答对优先回复；6：欢迎语回复；7：并发数超限回复；8：全局干预知识；9：任务流回复；10：任务流答案；11：搜索引擎回复；12：知识润色后回复；13：图片理解回复；14：实时文档回复；15：澄清确认回复；16：工作流回复；17：工作流运行结束；18：智能体回复；19：多意图回复 |

### Message

消息详情：

| 名称       | 类型             | 是否必填 | 说明                                                         |
| ---------- | ---------------- | -------- | ------------------------------------------------------------ |
| Type       | String           | 是       | 消息类型。reply：回复消息；thought：思考消息；tool_call：工具调用消息；task_execution：任务执行消息；recommendation：推荐问题；question：用户提问（WebSocket 连接建立后，标记为用户发送的对话）；notice：中间提示消息（仅适配旧版协议使用）；plan：任务规划 |
| MessageId  | String           | 是       | 消息的唯一标识。                                             |
| Name       | String           | 是       | 消息名称。                                                   |
| Title      | String           | 是       | 消息标题。                                                   |
| Icon       | String           | 否       | 图标 URL。                                                   |
| Status     | String           | 是       | 消息状态，如四种情况：processing：处理中（如"回复中""思考中""工具执行中"）；success：成功完成（如"回复完成""思考完成"）；failed：失败（如"回复失败""工具执行失败"）；stop：已停止（如用户中断生成、计划被取消） |
| StatusDesc | String           | 否       | 状态描述，如 "思考完成"。                                    |
| Contents   | Array of Content | 否       | 内容数组，包含多个内容对象。                                 |
| ExtraInfo  |                  | 否       | 扩展信息。                                                   |

### MessageExtraInfo

消息额外信息：

| 名称            | 类型    | 是否必填 | 说明                   |
| --------------- | ------- | -------- | ---------------------- |
| Elapsed         | Number  | 是       | 消息持续时间。         |
| StartTime       | Number  | 是       | 消息开始时间。         |
| EndTime         | Number  | 是       | 消息结束时间。         |
| AgentName       | String  | 否       | 输出消息的智能体名称。 |
| AgentIcon       | String  | 否       | 输出消息的智能体图标。 |
| ParentMessageId | String  | 否       | 父消息 ID。            |
| IsSubAgent      | Boolean | 是       | 是否是子 Agent。       |

### Procedure

处理过程详情：

| 名称           | 类型              | 是否必填 | 说明                             |
| -------------- | ----------------- | -------- | -------------------------------- |
| Name           | String            | 是       | 过程名称。                       |
| Title          | String            | 是       | 过程标题。                       |
| Status         | String            | 是       | 过程状态，如 "success"。         |
| IntentCate     | String            | 否       | 意图分类。                       |
| ResourceStatus | Number            | 否       | 计费资源状态。1：可用；2：不可用 |
| Type           | String            | 是       | 过程类型，如 "agent"。           |
| Knowledge      |                   | 否       | 知识库执行详情。                 |
| Workflow       |                   | 否       | 工作流执行详情。                 |
| Agent          |                   | 否       | 智能体执行详情。                 |
| StatInfos      | Array of StatInfo | 否       | 模型统计。                       |

### Knowledge

知识库执行详情：

| 名称            | 类型             | 是否必填 | 说明             |
| --------------- | ---------------- | -------- | ---------------- |
| Content         | String           | 是       | 检索 Query。     |
| System          | String           | 否       | 系统 Prompt。    |
| RewriteQuery    | String           | 否       | 改写后 Query。   |
| CustomVariables | Array of String  | 否       | 自定义参数。     |
| Histories       | Array of History | 否       | 多轮历史信息。   |
| Outputs         |                  | 否       | 知识库检索输出。 |

### History

历史信息：

| 名称      | 类型   | 是否必填 | 说明         |
| --------- | ------ | -------- | ------------ |
| Assistant | String | 否       | 智能体输出。 |
| User      | String | 否       | 用户输入。   |

### KnowledgeOutput

知识库检索输出信息：

| 名称    | 类型   | 是否必填 | 说明                           |
| ------- | ------ | -------- | ------------------------------ |
| Type    | Number | 是       | 数据类型。1：问答；2：文档片段 |
| Content | String | 是       | 知识内容。                     |

### WorkflowProcedure

工作流调用详情：

| 名称                | 类型             | 是否必填 | 说明                            |
| ------------------- | ---------------- | -------- | ------------------------------- |
| WorkflowId          | String           | 是       | 工作流 ID。                     |
| WorkflowName        | String           | 是       | 工作流名称。                    |
| WorkflowReleaseTime | String           | 是       | 工作流发布时间（Unix 时间戳）。 |
| WorkflowRunId       | String           | 是       | 工作流运行 ID。                 |
| Content             | String           | 是       | 工作流输入信息。                |
| Outputs             | Array of String  | 是       | 多气泡的输出结果。              |
| OptionCardIndex     |                  | 否       | 选项卡索引。                    |
| OptionCards         | Array of String  | 否       | 选项卡。                        |
| RunNodes            | Array of RunNode | 是       | 工作流节点信息。                |

### OptionCardIndex

选项卡索引信息：

| 名称     | 类型   | 是否必填 | 说明           |
| -------- | ------ | -------- | -------------- |
| RecordId | String | 是       | 消息唯一标识。 |
| Index    | Number | 是       | 选项卡索引。   |

### RunNode

工作流节点信息：

| 名称          | 类型              | 是否必填 | 说明                                    |
| ------------- | ----------------- | -------- | --------------------------------------- |
| Elapsed       | String            | 是       | 节点调用耗时。                          |
| NodeId        | String            | 是       | 节点 ID。                               |
| NodeName      | String            | 是       | 节点名称。                              |
| NodeType      | Number            | 是       | 节点类型。                              |
| Status        | Number            | 是       | 节点执行状态。                          |
| Input         | String            | 否       | 节点输入。                              |
| InputRef      | String            | 否       | 节点输入的完整内容的链接。              |
| Output        | String            | 否       | 节点输出。                              |
| OutputRef     | String            | 否       | 节点的输出的完整内容的链接。            |
| TaskOutput    | String            | 否       | 任务输出。                              |
| TaskOutputRef | String            | 否       | 任务的原始输出的完整内容的链接。        |
| Reply         | String            | 否       | 当前节点的回复内容。                    |
| FailCode      | String            | 否       | 异常信息对应的错误码。                  |
| FailMessage   | String            | 否       | 异常信息。                              |
| BelongNodeId  | String            | 否       | 节点所属工作流被引用时的引用节点的 ID。 |
| IsCurrent     | Boolean           | 否       | 当前走过的节点。                        |
| StatInfos     | Array of StatInfo | 否       | 大模型统计信息。                        |

### Agent

智能体执行详情：

| 名称            | 类型            | 是否必填 | 说明                                |
| --------------- | --------------- | -------- | ----------------------------------- |
| Input           | String          | 否       | 工具、大模型的输入信息，json 格式。 |
| Output          | String          | 否       | 工具、大模型的输出信息，json 格式。 |
| ModelName       | String          | 否       | 模型名。                            |
| Content         | String          | 是       | 检索 Query。                        |
| System          | String          | 否       | 系统 Prompt。                       |
| RewriteQuery    | String          | 否       | 改写后 Query。                      |
| CustomVariables | Array of String | 否       | 自定义参数。                        |

### StatInfo

统计信息：

| 名称           | 类型   | 是否必填 | 说明                |
| -------------- | ------ | -------- | ------------------- |
| Elapsed        | Number | 否       | 响应耗时。          |
| StartTime      | Number | 否       | 响应开始时间。      |
| InputTokens    | Number | 否       | 输入 token 总数。   |
| OutputTokens   | Number | 否       | 输出 token 总数。   |
| TotalTokens    | Number | 否       | 总 token 数。       |
| ModelName      | String | 否       | 使用模型名。        |
| FirstTokenCost | Number | 否       | 模型首 token 耗时。 |
| TotalCost      | Number | 否       | 模型总耗时。        |

### Error

错误信息：

| 名称      | 类型   | 是否必填 | 说明                           |
| --------- | ------ | -------- | ------------------------------ |
| Code      | Number | 是       | 错误码。                       |
| Message   | String | 是       | 错误消息。                     |
| RequestId | String | 是       | 错误关联的 RequestId。         |
| TraceId   | String | 是       | 错误的 TraceId。               |
| Elapsed   | Number | 否       | 请求耗时（毫秒）。             |
| StartTime | Number | 否       | 请求开始时间（毫秒级时间戳）。 |

### AgentTask

| 名称    | 类型   | 是否必填 | 说明                                                         |
| ------- | ------ | -------- | ------------------------------------------------------------ |
| Index   | Number | 是       | 任务序号                                                     |
| Content | String | 是       | 任务内容。任务要做什么的文字描述。比如"检索知识库"、"调用天气接口"、"生成报告"等。 |
| Status  | String | 是       | 消息状态，如四种情况：processing：处理中（如"回复中""思考中""工具执行中"）；success：成功完成（如"回复完成""思考完成"）；failed：失败（如"回复失败""工具执行失败"）；stop：已停止（如用户中断生成、计划被取消） |

### Questionnaire

| 名称      | 类型                    | 是否必填 | 说明                                   |
| --------- | ----------------------- | -------- | -------------------------------------- |
| Title     | string                  | 否       | 整张问卷的标题，比如"请完善以下信息"。 |
| Questions | Array of Question       | 否       | 具体要问用户的问题列表。               |
| Answers   | Array of QuestionAnswer | 否       | 用户已填答案列表（回显）。             |

### Question

| 名称     | 类型                    | 是否必填 | 说明                       |
| -------- | ----------------------- | -------- | -------------------------- |
| Index    | Number                  | 否       | 问题索引。                 |
| Question | String                  | 否       | 具体问题。                 |
| Type     | Number                  | 否       | 问题类型：1：单选；2：多选 |
| Required | Boolean                 | 否       | 是否必须回答               |
| Options  | Array of QuestionOption | 否       | 选项数组                   |

### QuestionAnswer

| 名称           | 类型            | 是否必填 | 说明                      |
| -------------- | --------------- | -------- | ------------------------- |
| Question       | string          | 否       | 对应的问题文案            |
| SelectedLabels | Array of string | 否       | 用户选中的选项 Label 列表 |

### QuestionOption

| 名称        | 类型   | 是否必填 | 说明           |
| ----------- | ------ | -------- | -------------- |
| Label       | string | 否       | 选项的内容     |
| Description | string | 否       | 选项内容的解释 |

## 4. 错误码

| 错误码  | 错误信息                                                     |
| ------- | ------------------------------------------------------------ |
| 400     | 请求参数错误, 请参阅接入文档。                               |
| 403     | 拒绝访问                                                     |
| 500     | 服务器内部错误                                               |
| 460001  | Token 校验失败。                                             |
| 460002  | 事件处理器不存在。                                           |
| 460004  | 应用不存在。                                                 |
| 460006  | 消息不存在或没有操作权限。                                   |
| 460007  | 会话创建失败。                                               |
| 460008  | Prompt 渲染失败。                                            |
| 460009  | 访客用户不存在。                                             |
| 460010  | 会话不存在或没有操作权限。                                   |
| 460011  | 超出应用配置模型 QPM 并发数限制。                            |
| 460020  | 模型请求超时。                                               |
| 460021  | 知识库未发布。                                               |
| 460022  | 访客创建失败。                                               |
| 460023  | 消息点赞点踩失败。                                           |
| 460024  | 标签不合法。                                                 |
| 460025  | 图像识别失败。                                               |
| 460026  | 应用停用或欠费。                                             |
| 460030  | 该应用类型不支持当前请求                                     |
| 460031  | 超出应用 QPS 并发数限制。                                    |
| 460032  | 当前应用模型余额不足。                                       |
| 460033  | 应用不存在或没有操作权限。                                   |
| 460034  | 输入内容过长。                                               |
| 460035  | 计算内容过长, 已经停止。                                     |
| 460036  | 任务流程节点预览参数异常。                                   |
| 460037  | 搜索资源已用尽，调用失败。                                   |
| 460038  | 该 AppID 请求存在异常行为，调用失败。                        |
| 460039  | 消息记录不合法                                               |
| 460048  | 应用未发布                                                   |
| 460100  | 工作流功能未启用                                             |
| 460101  | 工作流运行异常                                               |
| 460102  | 您选择的工作流未开启，请开启后再调试                         |
| 460103  | 您选择的工作流待调试，请先在工作流中对其进行调试             |
| 460104  | 您选择的工作流已被删除，请重新选择                           |
| 460105  | 您选择的工作流未发布，请先在工作流中对其进行开启             |
| 460106  | 该工作流中引用了其他工作流，其他工作流因'未启用'或'待调试'导致不可用，请'启用'或'调试'后再进行测试。 |
| 460107  | 您尚未选择工作流，请进行选择                                 |
| 460301  | 内部异常                                                     |
| 460400  | 事件不存在                                                   |
| 460401  | 请求处理超时                                                 |
| 460501  | 输入内容不合法                                               |
| 460502  | 输出内容不合法                                               |
| 460600  | 分享信息不存在                                               |
| 460701  | 专属模型超出并发数限制                                       |
| 460702  | 专属模型余额不足                                             |
| 460711  | 空间 TPM 超限                                                |
| 460712  | 空间 QPM 超限                                                |
| 460713  | 空间每日 Token 使用超限                                      |
| 460721  | 应用 TPM 超限                                                |
| 460722  | 应用 QPM 超限                                                |
| 460723  | 应用每日 Token 使用超限                                      |
| 460800  | 不支持的 widget 动作                                         |
| 460801  | widget 不存在                                                |
| 460802  | 未知的 widget 澄清类型                                       |
| 460916  | 当前应用未配置可执行的技能运行时                             |
| 460917  | 技能运行时暂时不可用                                         |
| 460918  | Claw 服务未配置完成                                          |
| 460919  | 会话 ID 已存在，请更换后重试                                 |
| 460920  | 工作空间不存在或没有操作权限                                 |
| 460921  | 没有操作该工作空间的权限                                     |
| 460922  | 该工作空间沙箱类型不支持此操作                               |
| 460923  | 工作空间无关联对话，无法定位沙箱实例                         |
| 4505004 | APPKEY 无效，请核实 APPKEY 来源对应发送事件。                |
