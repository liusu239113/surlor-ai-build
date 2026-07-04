#!/bin/bash
# Surlor AI APK 构建 + 自动安装监控脚本
# 每10秒检查构建进度，APK 生成后自动安装到手机

APK_PATH="D:/WorkBuddy/projects/operit-game-dev/app/build/outputs/apk/debug/app-debug.apk"
LOG_FILE="D:/gradle-build-final.log"
STATUS_FILE="D:/apk-build-status.log"
DEVICE_SERIAL=""

echo "=== Surlor AI 构建监控启动 $(date) ===" > "$STATUS_FILE"
echo "APK 路径: $APK_PATH" >> "$STATUS_FILE"
echo "日志文件: $LOG_FILE" >> "$STATUS_FILE"

# 函数：检查设备连接
check_device() {
    DEVICE=$(adb devices 2>/dev/null | grep -v "List of devices" | grep "device$" | head -1 | awk '{print $1}')
    if [ -n "$DEVICE" ]; then
        echo "设备已连接: $DEVICE" >> "$STATUS_FILE"
        DEVICE_SERIAL="$DEVICE"
        return 0
    else
        echo "设备未连接，尝试重连..." >> "$STATUS_FILE"
        return 1
    fi
}

# 函数：安装 APK
install_apk() {
    if [ -f "$APK_PATH" ]; then
        echo "APK 已生成: $APK_PATH" >> "$STATUS_FILE"
        echo "文件大小: $(ls -lh "$APK_PATH" 2>/dev/null | awk '{print $5}')" >> "$STATUS_FILE"
        
        # 检查设备
        if check_device; then
            echo "开始安装 APK 到设备 $DEVICE_SERIAL..." >> "$STATUS_FILE"
            adb -s "$DEVICE_SERIAL" install -r "$APK_PATH" >> "$STATUS_FILE" 2>&1
            if [ $? -eq 0 ]; then
                echo "✅ APK 安装成功！" >> "$STATUS_FILE"
                echo "INSTALL_SUCCESS" > "D:/apk-install-done.flag"
            else
                echo "❌ APK 安装失败" >> "$STATUS_FILE"
            fi
        else
            echo "⚠️ 设备未连接，无法安装。请连接手机后手动运行:" >> "$STATUS_FILE"
            echo "  adb install -r \"$APK_PATH\"" >> "$STATUS_FILE"
        fi
    fi
}

# 主循环：每10秒检查一次
COUNT=0
while true; do
    COUNT=$((COUNT + 1))
    TIMESTAMP=$(date +"%H:%M:%S")
    
    # 检查 APK 是否已生成
    if [ -f "$APK_PATH" ]; then
        echo "[$TIMESTAMP] 检查 #$COUNT: APK 已生成！" >> "$STATUS_FILE"
        install_apk
        break
    fi
    
    # 检查构建进程是否还在运行
    if ps aux 2>/dev/null | grep -q "[g]radle" || tasklist 2>/dev/null | grep -i "java" | grep -v grep > /dev/null; then
        echo "[$TIMESTAMP] 检查 #$COUNT: 构建中..." >> "$STATUS_FILE"
    else
        # 构建进程已结束但 APK 未生成 = 构建失败
        if [ $COUNT -gt 10 ]; then
            echo "[$TIMESTAMP] 检查 #$COUNT: 构建进程已结束但 APK 未生成，构建可能失败" >> "$STATUS_FILE"
            echo "BUILD_FAILED" > "D:/apk-build-failed.flag"
            break
        fi
    fi
    
    sleep 10
done

echo "=== 监控结束 $(date) ===" >> "$STATUS_FILE"
