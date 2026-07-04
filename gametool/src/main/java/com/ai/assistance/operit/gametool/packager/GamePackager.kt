package com.ai.assistance.operit.gametool.packager

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.ai.assistance.operit.gametool.models.GameProject
import com.ai.assistance.operit.gametool.models.GameType
import java.io.File

/**
 * 游戏打包器 - 将游戏项目打包为可安装的 APK
 *
 * 基于 Operit 已有的 ApkEditor 和 HTML Packager 能力。
 * H5 游戏会被封装到一个 WebView 壳子中形成 APK。
 */
object GamePackager {

    data class PackageConfig(
        val appName: String,
        val packageName: String,
        val versionName: String = "1.0.0",
        val versionCode: Int = 1,
        val icon: Bitmap? = null,
        val orientation: String = "portrait",
        val fullscreen: Boolean = true
    )

    /**
     * 打包为 Android APK
     * @param project 游戏项目
     * @param config 打包配置
     * @param outputDir 输出目录
     * @param onProgress 进度回调 (0-100)
     * @return 生成的 APK 文件路径
     */
    suspend fun packageAsApk(
        context: Context,
        project: GameProject,
        config: PackageConfig,
        outputDir: File,
        onProgress: (Int) -> Unit = {}
    ): Result<File> {
        return try {
            onProgress(10)

            // 1. 创建临时工作目录
            val workDir = File(outputDir, "build_${project.id}")
            workDir.mkdirs()

            onProgress(20)

            // 2. 准备 Web 资源
            when (project.type) {
                GameType.H5 -> prepareH5Resources(project, workDir)
                GameType.COMPOSE -> prepareComposeResources(project, workDir)
                else -> throw UnsupportedOperationException("暂不支持该类型打包")
            }

            onProgress(40)

            // 3. 创建 Android 壳工程
            createAndroidShell(context, workDir, config, project)

            onProgress(60)

            // 4. 调用 ApkEditor 打包（通过 Operit 的 subpack 系统）
            val apkFile = File(outputDir, "${config.appName}_v${config.versionName}.apk")
            buildApk(context, workDir, apkFile, config)

            onProgress(90)

            // 5. 签名 APK
            signApk(context, apkFile, config)

            onProgress(100)

            // 6. 清理临时文件
            workDir.deleteRecursively()

            Result.success(apkFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 将 H5 游戏打包为 APK（最快路径）
     * 利用 Operit 现有的 HTML Packager 机制
     */
    suspend fun quickPackageH5AsApk(
        context: Context,
        project: GameProject,
        config: PackageConfig,
        outputDir: File,
        onProgress: (Int) -> Unit = {}
    ): Result<File> {
        return try {
            onProgress(10)

            // 使用 Operit 的 HTML Packager 路径
            // 1. 提取 H5 源码到目录
            val sourceDir = File(outputDir, "h5_source_${project.id}")
            sourceDir.mkdirs()

            val entryFile = File(sourceDir, "index.html")
            project.sourceFiles.firstOrNull()?.let {
                entryFile.writeText(it.content)
            }

            onProgress(30)

            // 2. 使用标准的 WebView 壳 APK 模板
            // 从 assets 中提取壳 APK
            val templateApk = extractTemplateApk(context)

            onProgress(50)

            // 3. 替换壳 APK 中的资源
            // 使用 ApkEditor 替换图标、名称、Web 内容
            val outputApk = File(outputDir, "${config.appName}.apk")
            applyH5ToApkShell(templateApk, sourceDir, outputApk, config)

            onProgress(80)

            // 4. 重新签名
            signApk(context, outputApk, config)

            onProgress(100)

            Result.success(outputApk)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun prepareH5Resources(project: GameProject, workDir: File) {
        val wwwDir = File(workDir, "www")
        wwwDir.mkdirs()

        project.sourceFiles.forEach { file ->
            val target = File(wwwDir, file.path)
            target.parentFile?.mkdirs()
            target.writeText(file.content)
        }
    }

    private fun prepareComposeResources(project: GameProject, workDir: File) {
        // Compose 项目需要编译，这里生成 Android 项目结构
        val srcDir = File(workDir, "app/src/main/java/com/game")
        srcDir.mkdirs()

        project.sourceFiles.forEach { file ->
            val target = File(srcDir, file.path)
            target.writeText(file.content)
        }
    }

    private fun createAndroidShell(
        context: Context,
        workDir: File,
        config: PackageConfig,
        project: GameProject
    ) {
        // 生成 AndroidManifest.xml
        val manifestContent = generateManifest(config)
        File(workDir, "AndroidManifest.xml").writeText(manifestContent)
    }

    private fun generateManifest(config: PackageConfig): String = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="${config.packageName}">
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="${config.appName}"
        android:theme="@style/GameTheme">
        <activity
            android:name=".GameActivity"
            android:screenOrientation="${config.orientation}"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""".trimIndent()

    private suspend fun buildApk(
        context: Context,
        workDir: File,
        outputFile: File,
        config: PackageConfig
    ) {
        // 通过 Operit 的 subpack.ApkEditor API 进行打包
        // 实际实现中会调用 ApkEditor 的链式 API
    }

    private fun extractTemplateApk(context: Context): File {
        // 从 assets 提取壳 APK 模板
        val templateFile = File(context.cacheDir, "game_shell_template.apk")
        context.assets.open("game_engines/webview_shell.apk").use { input ->
            templateFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return templateFile
    }

    private fun applyH5ToApkShell(
        templateApk: File,
        sourceDir: File,
        outputApk: File,
        config: PackageConfig
    ) {
        // 使用 Operit 的 ApkEditor 替换资源
        // ApkEditor.fromFile(templateApk)
        //     .changeAppName(config.appName)
        //     .changePackageName(config.packageName)
        //     .changeVersionName(config.versionName)
        //     .changeVersionCode(config.versionCode)
        //     .changeIcon(config.icon)
        //     .replaceAssets(sourceDir)
        //     .setOutput(outputApk)
        //     .process()
    }

    private suspend fun signApk(
        context: Context,
        apkFile: File,
        config: PackageConfig
    ) {
        // 使用 Operit 的 apksig 签名工具
    }
}
