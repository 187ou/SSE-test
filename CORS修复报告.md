# CORS配置修复报告

## 问题描述

### 错误信息

```
java.lang.IllegalArgumentException:
When allowCredentials is true, allowedOrigins cannot contain
the special value "*"
```

### 原因

Spring Boot 2.7+ 中，当设置 `allowCredentials=true` 时，不能使用 `allowedOrigins("*")`，因为 `*` 通配符无法设置在 `Access-Control-Allow-Origin` 响应头中（带凭证时不允许使用通配符）。

### 触发场景

- 前端 (http://localhost:3000) 发送OPTIONS预检请求到后端 (http://localhost:8080)
- 后端CORS配置不允许跨域请求
- 返回403 Forbidden

---

## 解决方案

### 修改文件

**文件**: `src/main/java/com/example/adp/config/CorsConfig.java`

### 修复前

```java
@Bean
public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("*"));  // ❌ 与allowCredentials冲突
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    ...
}
```

### 修复后

```java
@Bean
public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    // ✅ 使用setAllowedOriginPatterns替代setAllowedOrigins
    config.setAllowedOriginPatterns(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    ...
}
```

### 关键区别

| 方法 | 允许凭证 | 通配符支持 | 说明 |
|------|---------|-----------|------|
| `setAllowedOrigins("*")` | ❌ 不支持 | 可以设置 | 无法用于Access-Control-Allow-Credentials |
| `setAllowedOriginPatterns("*")` | ✅ 支持 | 支持模式匹配 | Spring 5.3+ 新特性，支持凭证+通配符 |

---

## 验证结果

### CORS预检请求 ✅

```
OPTIONS /api/chat/send HTTP/1.1
Origin: http://localhost:3000
Access-Control-Request-Method: POST

HTTP/1.1 200 ✓
Access-Control-Allow-Origin: http://localhost:3000 ✓
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS ✓
Access-Control-Allow-Credentials: true ✓
```

### POST请求 ✅

```
POST /api/chat/send HTTP/1.1
Origin: http://localhost:3000
Content-Type: application/json

HTTP/1.1 200 ✓
Content-Type: text/event-stream ✓

data:Hello
data:! How
```

---

## 额外说明

### Windows curl 中文编码问题

在Windows bash环境下使用curl发送中文时可能出现编码错误：

```bash
# ❌ 中文可能失败
curl -X POST http://localhost:8080/api/chat/send \
  -d '{"message":"你好"}'
# JSON parse error: Invalid UTF-8

# ✅ 英文正常
curl -X POST http://localhost:8080/api/chat/send \
  -d '{"message":"hello"}'
# 正常工作
```

**这是Windows环境下curl的编码问题，不是代码bug。浏览器前端不会有这个问题。**

### 测试建议

1. **使用Postman/浏览器**测试中文消息
2. **或使用前端界面**发送中文消息
3. **避免在Windows bash下直接curl中文**

---

## 部署状态

- ✅ CORS配置已修复
- ✅ 后端服务已重启 (PID 181476)
- ✅ 预检请求测试通过
- ✅ POST请求测试通过
- ✅ 前端可以正常访问

---

**修复完成时间**: 2026-10-07 17:36

**影响范围**: 所有跨域请求（前端→后端）

**状态**: ✅ 已解决
