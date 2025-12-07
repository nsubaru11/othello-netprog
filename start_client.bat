@echo off
chcp 65001 > nul
echo Starting Othello Client...

set JAVA_HOME=C:\Program Files\Amazon Corretto\jdk1.8.0_472
set PATH=%JAVA_HOME%\bin;%PATH%
set SRC_DIR=%~dp0src
set OUT_DIR=%~dp0out\production\Othello-netprog

echo Compiling...
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
dir /s /b "%SRC_DIR%\model\*.java" "%SRC_DIR%\client\*.java" > "%~dp0sources.txt"
"%JAVA_HOME%\bin\javac" -encoding UTF-8 -d "%OUT_DIR%" @"%~dp0sources.txt"
del "%~dp0sources.txt"

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM 画像リソースをコピー
xcopy /s /y /i "%SRC_DIR%\client\assets" "%OUT_DIR%\client\assets" > nul

echo Starting client...
"%JAVA_HOME%\bin\java" -cp "%OUT_DIR%" client.Main

pause

