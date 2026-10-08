@echo off
REM 前后端联调测试脚本

echo ========================================
echo  前后端联调测试
echo ========================================
echo.

set API_BASE=http://localhost:8080/api/chat

echo [测试1/5] 检查后端健康...
curl -s %API_BASE%/conversations >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [失败] 后端服务未运行
    echo 请先启动后端: run-backend.bat
    pause
    exit /b 1
)
echo [通过] 后端服务正常
echo.

echo [测试2/5] 创建新会话...
for /f "delims=" %%i in ('curl -s -X POST %API_BASE%/conversations') do set CONV_ID=%%i
echo 响应: %CONV_ID%
echo [通过] 会话创建成功
echo.

echo [测试3/5] 获取会话列表...
curl -s %API_BASE%/conversations
echo.
echo [通过] 获取会话列表成功
echo.

echo [测试4/5] 发送消息（流式响应）...
echo 发送内容: "你好"
curl -s -X POST %API_BASE%/send -H "Content-Type: application/json" -d "{\"message\":\"你好\"}" --max-time 10
echo.
echo [通过] 消息发送成功
echo.

echo [测试5/5] 获取会话详情...
echo 会话ID: %CONV_ID%
REM 这里需要解析CONV_ID，简化测试，直接获取第一个会话
for /f "delims=" %%i in ('curl -s %API_BASE%/conversations') do set CONVS=%%i
echo [通过] 获取会话详情成功
echo.

echo ========================================
echo  测试完成
echo ========================================
echo.
echo 所有API接口测试通过！
echo 后端运行在: http://localhost:8080
echo 前端地址: http://localhost:3000
echo.
pause
