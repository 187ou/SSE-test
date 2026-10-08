# MVP验证清单

## 运行前检查

- [ ] 已在 `application.properties` 中配置真实的AppKey
- [ ] 确保ADP平台上的应用已发布
- [ ] 网络可以访问 `https://wss.lke.cloud.tencent.com`

## 运行步骤

### 1. 编译项目

```bash
mvn clean compile
```

预期输出：BUILD SUCCESS

### 2. 运行测试

```bash
mvn test
```

预期输出：Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

### 3. 运行MVP程序

```bash
mvn exec:java -Dexec.mainClass="com.example.adp.App"
```

或直接运行：

```bash
run-test.bat
```

## 验证MVP闭环

运行后，控制台应该输出以下流程：

```
========================================
  ADP Chat Client - MVP
========================================

=== 发送请求 ===
URL: https://wss.lke.cloud.tencent.com/adp/v2/chat
Request Body: {请求的JSON数据}
================

=== 接收SSE流 ===
[Event: request_ack]
SSEEvent{type='request_ack'}
>>> 请求已确认

[Event: response.created]
SSEEvent{type='response.created'}
>>> 响应已创建

[Event: response.processing]
SSEEvent{type='response.processing'}
>>> 正在处理响应...

[Event: message.added]
SSEEvent{type='message.added'}
>>> 新消息已添加

[Event: content.added]
SSEEvent{type='content.added'}
>>> 内容已添加

[多个 text.delta 事件]
[Event: text.delta]
SSEEvent{type='text.delta', extraFields={Text=您好！请问有什么可以帮您的}}
>>> 流式输出文本

[Event: message.done]
SSEEvent{type='message.done'}
>>> 消息处理完成

[Event: response.completed]
SSEEvent{type='response.completed'}
>>> 响应已完成

========================================
  测试完成
========================================
```

## 成功标准

如果看到以下所有内容，说明MVP闭环已跑通：

✅ **请求阶段**
- HTTP状态码200
- 收到 `request_ack` 事件（请求确认）

✅ **响应阶段**
- 收到 `response.created` 事件
- 收到 `response.processing` 事件（至少1次）

✅ **消息阶段**
- 收到 `message.added` 事件
- 收到 `content.added` 事件
- 收到至少1个 `text.delta` 事件（包含实际回复内容）
- 收到 `message.done` 事件

✅ **完成阶段**
- 收到 `response.completed` 事件
- 程序正常退出，无异常堆栈

## 常见问题

### 问题1: 400错误 - 请求参数错误
**原因**：RequestId或ConversationId格式不符合要求
**解决**：确保使用UUID格式（例如：`1b9c0b03-dc83-47ac-8394-b366e3ea67ef`）

### 问题2: 403错误 - 拒绝访问
**原因**：AppKey无效或应用未发布
**解决**：
1. 检查AppKey是否正确
2. 在ADP平台发布应用
3. 确认应用状态为"运行中"

### 问题3: 460048错误 - 应用未发布
**原因**：应用未发布
**解决**：在ADP平台发布应用

### 问题4: 网络超时
**原因**：网络连接问题或接口地址错误
**解决**：
1. 检查网络连接
2. 确认能访问 `https://wss.lke.cloud.tencent.com`

### 问题5: 控制台乱码
**原因**：编码问题
**解决**：确保控制台使用UTF-8编码

## 下一步扩展

MVP跑通后，可以继续扩展：

1. **多轮对话**：保持ConversationId，实现上下文记忆
2. **图文问答**：添加图片内容支持
3. **流式处理优化**：更好的流式输出控制
4. **错误处理增强**：更详细的错误码处理
5. **日志系统**：集成Logback/Log4j2
6. **配置外部化**：支持从环境变量或配置中心读取
7. **连接池**：使用HttpClient连接池提升性能

## 技术验证点

本次MVP验证了：

✅ HTTP/HTTPS请求能力
✅ JSON序列化/反序列化
✅ SSE流式响应解析
✅ 事件类型识别
✅ 流式文本输出
✅ 异常处理机制
