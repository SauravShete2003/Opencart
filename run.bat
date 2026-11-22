@echo off
cd /d "%~dp0"
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;C:\Program Files\apache-maven-3.10.1\bin;%PATH%"
call mvn clean test
pause
