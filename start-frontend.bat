@echo off
REM ADP对话台 - 前端启动脚本

echo ========================================
echo  ADP 对话台前端
echo ========================================
echo.

echo [1/2] 检查前端文件...
if not exist "frontend\index.html" (
    echo [错误] 未找到前端文件 frontend/index.html
    pause
    exit /b 1
)

echo [2/2] 启动前端服务...
echo 请在浏览器中打开: frontend/index.html
echo 或者访问: http://localhost:3000
echo.

REM 检查是否安装了Python
python --version >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo 检测到Python，启动HTTP服务器...
    cd frontend
    python -m http.server 3000
) else (
    echo 未找到Python，直接打开浏览器...
    start frontend\index.html
)

pause
