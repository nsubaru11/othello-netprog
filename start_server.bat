@echo off
chcp 65001 > nul
echo Starting Othello Server...

set JAVA_HOME=C:\Program Files\Amazon Corretto\jdk1.8.0_472
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=%~dp0out\production\Othello-netprog

"%JAVA_HOME%\bin\java" -cp "%CLASSPATH%" server.OthelloServer

pause
