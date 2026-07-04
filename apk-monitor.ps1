# APK 自动监控和安装脚本 - 每10秒轮巡
# 当 APK 生成后立即安装到手机

$APKPath = "D:\WorkBuddy\projects\operit-game-dev\app\build\outputs\apk\debug\app-debug.apk"
$Device = "adb-161d7700"
$LogFile = "D:\apk-monitor.log"
$MaxWaitSeconds = 3600  # 最多等待1小时
$CheckInterval = 10       # 每10秒检查一次

function Log($Message) {
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logMessage = "[$timestamp] $Message"
    Write-Output $logMessage | Tee-Object -FilePath $LogFile -Append
}

Log "开始监控 APK 生成..."
Log "APK 路径: $APKPath"
Log "目标设备: $Device"

$elapsed = 0
while ($elapsed -lt $MaxWaitSeconds) {
    # 检查 APK 文件是否存在
    if (Test-Path $APKPath) {
        Log "✅ APK 已生成: $APKPath"
        $fileSize = (Get-Item $APKPath).Length / 1MB
        Log "文件大小: $([math]::Round($fileSize, 2)) MB"
        
        # 等待文件写入完成（APK 可能还在写入中）
        Start-Sleep -Seconds 5
        
        # 安装到手机
        Log "开始安装 APK 到设备: $Device"
        $installOutput = adb -s $Device install -r $APKPath 2>&1
        Log "安装输出: $installOutput"
        
        if ($LASTEXITCODE -eq 0) {
            Log "✅ APK 安装成功！"
            Log "任务完成，监控脚本退出。"
            exit 0
        } else {
            Log "❌ APK 安装失败，请检查。"
            Log "错误输出: $installOutput"
            exit 1
        }
    }
    
    # 检查 Gradle 进程是否还在运行
    $gradleProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*gradle*" }
    if (-not $gradleProcess) {
        Log "⚠️  Gradle 进程已结束，但 APK 未生成！"
        Log "检查构建日志..."
        exit 1
    }
    
    # 每60秒输出一次状态（避免日志过大）
    if (($elapsed % 60) -eq 0) {
        Log "⏳ 等待中... (已等待 ${elapsed}秒)"
    }
    
    Start-Sleep -Seconds $CheckInterval
    $elapsed += $CheckInterval
}

Log "⚠️  等待超时（${MaxWaitSeconds}秒），APK 未生成。"
exit 1
