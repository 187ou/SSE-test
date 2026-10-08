@echo off
REM ADP Chat Client 快速测试脚本

echo ========================================
echo  ADP Chat Client - MVP 测试
echo ========================================
echo.

REM 检查Maven
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未找到Maven，请先安装Maven
    pause
    exit /b 1
)

echo [1/3] 检查配置文件...
if not exist "src\main\resources\application.properties" (
    echo [错误] 未找到配置文件 application.properties
    pause
    exit /b 1
)

echo [2/3] 构建项目...
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 构建失败
    pause
    exit /b 1
)

echo [3/3] 运行测试...
echo.
call mvn exec:java -Dexec.mainClass="com.example.adp.App"

echo.
echo ========================================
pause
