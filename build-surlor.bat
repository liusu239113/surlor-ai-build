@echo off
set JAVA_HOME=D:\jdk-17\jdk-17.0.13+11
set PATH=%JAVA_HOME%\bin;%PATH%
set GRADLE_OPTS=-Xmx10g -XX:MaxMetaspaceSize=2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError
cd /d D:\WorkBuddy\projects\Surlor AI
call "D:\gradle\gradle-8.13\bin\gradle.bat" :app:assembleDebug --no-daemon --gradle-user-home "D:\gradle-cache" > "D:\gradle-build.log" 2> "D:\gradle-build-err.log"
echo EXIT_CODE=%ERRORLEVEL% >> "D:\gradle-build.log"
