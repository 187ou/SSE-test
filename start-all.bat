@echo off
REM ADP对话台 - 一键启动全部服务

echo ========================================
echo  ADP 对话台 - 一键启动
echo ========================================
echo.

echo [1/3] 启动后端服务...
start "ADP Backend" cmd /c "call mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080"

echo [2/3] 等待后端启动...
timeout /t 15 /nobreak >nul

echo [3/3] 启动前端...
start "ADP Frontend" cmd /c "cd frontend && python -m http.server 3000"

echo.
echo ========================================
echo  服务已启动
echo ========================================
echo.
echo 后端地址: http://localhost:8080
echo 前端地址: http://localhost:3000
echo.
echo 请在浏览器中打开: http://localhost:3000
echo.
echo 按任意键打开浏览器...
pause >nul

start http://localhost:3000
