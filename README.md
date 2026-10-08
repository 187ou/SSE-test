# ADP 对话台 - 前后端完整方案

一个完整的腾讯云ADP对话平台前后端解决方案。

## ✨ 核心特性

- ✅ **MVP已跑通** - 已验证的最小可用产品
- ✅ **RESTful API** - Spring Boot 2.7后端
- ✅ **SSE流式响应** - 实时显示AI回复
- ✅ **Vue.js前端** - 侧边栏式对话界面
- ✅ **会话管理** - 创建/切换/删除对话
- ✅ **一键启动** - 提供启动脚本

## 🚀 快速开始

### 方式一：一键启动

```bash
start-all.bat
```

### 方式二：分别启动

**后端** (端口8082)
```bash
run-backend.bat
```

**前端** (端口3000)
```bash
start-frontend.bat
```

或直接双击 `frontend/index.html`

## 📊 验证状态

### 后端API测试 ✅

```bash
# 创建会话
curl -X POST http://localhost:8080/api/chat/conversations
# 返回: {"id":"xxx","title":"新对话",...}

# 获取会话列表
curl http://localhost:8080/api/chat/conversations
# 返回: [{"id":"xxx",...}]

# 发送消息
curl -X POST http://localhost:8080/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{"message":"你好"}'
# 返回SSE流式响应
```

### 前端功能 ✅

- 对话列表侧边栏
- 新建/删除对话
- 流式消息显示
- 打字动画
- 响应式布局

## 📁 项目结构

```
├── backend/
│   ├── src/main/java/com/example/adp/
│   │   ├── ChatApplication.java          # Spring Boot主类
│   │   ├── client/ADPClient.java         # ADP SSE客户端
│   │   ├── config/CorsConfig.java        # CORS配置
│   │   ├── controller/ChatController.java # REST API
│   │   ├── model/                        # 数据模型
│   │   ├── service/                      # 业务逻辑
│   │   └── util/ConfigLoader.java        # 配置加载器
│   └── src/main/resources/
│       ├── application.yml               # Spring配置
│       └── application.properties        # ADP配置
├── frontend/
│   └── index.html                        # Vue.js前端
├── pom.xml                               # Maven配置
├── README.md                             # 本文件
├── 快速启动.md                            # 启动指南
├── run-backend.bat                        # 启动后端
├── start-frontend.bat                     # 启动前端
└── start-all.bat                          # 一键启动
```

## 🔧 技术栈

**后端** (端口8080)
- Java 11 + Spring Boot 2.7.18
- Spring Web + WebFlux
- Jackson JSON

**前端** (端口3000)
- Vue 3 + Element Plus
- Axios
- 原生HTML/CSS/JS

## ⚙️ 配置

### AppKey

编辑 `src/main/resources/application.properties`：

```properties
adp.app.key=你的真实AppKey
```

### 端口

编辑 `src/main/resources/application.yml`：

```yaml
server:
  port: 8080
```

### 前端API地址

编辑 `frontend/index.html` 第598行：

```javascript
const API_BASE = 'http://localhost:8080/api/chat';
```

## 📖 文档

- [快速启动](快速启动.md) - 5分钟启动指南
- [MVP验证清单](MVP验证清单.md) - 详细验证步骤
- [快速开始](快速开始.md) - 完整使用说明
- [request.md](request.md) - ADP接口文档

## 🎯 下一步

- ✅ MVP验证完成
- ✅ 前后端联调完成
- ✅ 所有API测试通过
- ⏭️ **准备就绪，可以开始业务集成**

---

**运行 `start-all.bat` 即可开始体验！** 🚀
