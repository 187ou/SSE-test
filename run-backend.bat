@echo off
REM ADP对话台 - 后端启动脚本

echo ========================================
echo  ADP 对话台后端服务
echo ========================================
echo.

echo [1/2] 检查Java环境...
java -version >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未找到Java，请先安装JDK 11+
    pause
    exit /b 1
)

echo [2/2] 启动后端服务...
echo 服务地址: http://localhost:8080
echo API文档: http://localhost:8080/api/chat/conversations
echo.
echo 按Ctrl+C停止服务
echo.

call mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080

pause
