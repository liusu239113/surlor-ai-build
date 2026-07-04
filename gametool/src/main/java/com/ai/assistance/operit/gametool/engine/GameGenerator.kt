package com.ai.assistance.operit.gametool.engine

import android.content.Context
import android.util.Log
import com.ai.assistance.llama.LlamaSession
import com.ai.assistance.operit.gametool.models.GameModelConfig
import com.ai.assistance.operit.gametool.models.GameProject
import com.ai.assistance.operit.gametool.models.GameType
import com.ai.assistance.operit.gametool.models.SourceFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 游戏生成引擎 - 通过 AI 对话生成游戏代码
 */
object GameGenerator {

    private const val TAG = "GameGenerator"

    /**
     * AI 游戏生成系统提示词
     */
    val GAME_GENERATION_SYSTEM_PROMPT = """
你是一个手机端游戏开发助手，可以根据用户描述生成可直接运行的 H5 游戏代码。
你生成的代码会被立即载入 WebView 实时预览，所以必须保证代码的完整性和正确性。

## 核心规则
1. 所有代码必须在一个 HTML 文件内完成（内联 CSS 和 JS）
2. 优先使用 Canvas 2D API 实现游戏渲染，兼容性最好
3. 不要使用外部 CDN 资源，所有代码自包含
4. 游戏必须适配 375x667 到 414x896 的屏幕范围
5. 提供触摸和鼠标两种输入支持
6. 游戏循环使用 requestAnimationFrame
7. 游戏要有明确的开始、进行中和结束状态

## 支持的 API 和框架
- Canvas 2D API (推荐)
- 原生 DOM/CSS 动画
- 如果需要复杂物理效果，使用内联精简实现
- 可以内联 Phaser.js 的精简版本（如果必要）

## 代码结构规范
```html
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
/* 样式在这里 */
</style>
</head>
<body>
<!-- HTML 结构 -->
<script>
// 游戏主逻辑在这里
// 使用 IIFE 封装，避免全局污染
(function() {
    'use strict';
    // 游戏配置
    // 游戏状态
    // 游戏循环
    // 输入处理
    // 渲染
})();
</script>
</body>
</html>
```

## 用户界面规范
- 游戏需要有开始界面（标题 + 开始按钮）
- 游戏进行中显示分数/进度
- 游戏结束显示结果和重新开始按钮
- 界面简洁美观，适合手机屏幕

现在，请根据用户的描述生成一个完整的 H5 游戏。
""".trimIndent()

    /**
     * 使用 AI 生成游戏代码
     */
    suspend fun generateGame(
        context: Context,
        description: String,
        project: GameProject,
        onCodeGenerated: (SourceFile) -> Unit
    ): Result<GameProject> {
        return try {
            // 构建 AI 提示
            val prompt = buildPrompt(description, project.type)

            // 优先调用本地 LLM 生成代码，未下载模型时给出占位并提示
            val generatedCode = callLocalModel(context, prompt)

            val sourceFile = SourceFile(
                path = project.entryFile,
                content = generatedCode,
                type = when (project.type) {
                    GameType.H5 -> com.ai.assistance.operit.gametool.models.FileType.HTML
                    GameType.COMPOSE -> com.ai.assistance.operit.gametool.models.FileType.KOTLIN
                    GameType.GODOT -> com.ai.assistance.operit.gametool.models.FileType.GDSCRIPT
                }
            )

            onCodeGenerated(sourceFile)

            val updatedProject = project.copy(
                sourceFiles = listOf(sourceFile),
                updatedAt = System.currentTimeMillis()
            )
            Result.success(updatedProject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildPrompt(description: String, type: GameType): String {
        return when (type) {
            GameType.H5 -> """
${GAME_GENERATION_SYSTEM_PROMPT}

## 用户需求
${description}

请生成完整的游戏 HTML 代码。
""".trimIndent()
            GameType.COMPOSE -> """
你是一个 Android Jetpack Compose 游戏开发专家。
根据用户需求生成一个完整的 Kotlin + Compose 游戏代码。

用户需求：
${description}

要求：
- 使用 Jetpack Compose 编写
- 单文件可运行
- 适配手机屏幕
- 包含 Canvas 绘制和触摸交互
""".trimIndent()
            GameType.GODOT -> """
你是一个 Godot 4 游戏开发专家。
根据用户需求生成 GDScript 游戏代码。

用户需求：
${description}
""".trimIndent()
        }
    }

    /**
     * 调用本地模型生成代码
     * 优先使用 llama.cpp 加载内置 GGUF 模型；模型未下载时返回占位并提示。
     */
    private suspend fun callLocalModel(context: Context, prompt: String): String {
        val modelDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "models")
        val model = GameModelConfig.getRecommendedModel()
        val modelFile = File(modelDir, "${model.id}.gguf")

        if (!modelFile.exists()) {
            Log.w(TAG, "本地模型不存在: ${modelFile.absolutePath}")
            return generatePlaceholderWithDownloadHint(model)
        }

        if (!LlamaSession.isAvailable()) {
            Log.e(TAG, "LlamaSession 不可用")
            return generatePlaceholderWithDownloadHint(model, "当前设备不支持本地推理，请检查 so 库是否已打包。")
        }

        return withContext(Dispatchers.Default) {
            val session = LlamaSession.create(
                modelFile.absolutePath,
                LlamaSession.Config(
                    nThreads = Runtime.getRuntime().availableProcessors().coerceAtMost(4),
                    nCtx = 2048,
                    nBatch = 512,
                    nUBatch = 512,
                    nGpuLayers = 0,
                    useMmap = false,
                    flashAttention = false,
                    kvUnified = true,
                    offloadKqv = false
                )
            )

            if (session == null) {
                Log.e(TAG, "创建 LlamaSession 失败")
                return@withContext generatePlaceholderWithDownloadHint(model, "模型加载失败，请检查模型文件是否完整。")
            }

            try {
                session.setSamplingParams(
                    temperature = 0.35f,
                    topP = 0.9f,
                    topK = 40,
                    repetitionPenalty = 1.05f,
                    frequencyPenalty = 0.0f,
                    presencePenalty = 0.0f,
                    penaltyLastN = 64
                )

                val builder = StringBuilder()
                session.generateStream(prompt, maxTokens = 2048) { token ->
                    builder.append(token)
                    true
                }

                val raw = builder.toString()
                extractHtmlCode(raw) ?: raw
            } finally {
                session.release()
            }
        }
    }

    private fun extractHtmlCode(raw: String): String? {
        val start = raw.indexOf("<!DOCTYPE html")
        if (start == -1) return null
        val end = raw.lastIndexOf("</html>")
        if (end == -1) return raw.substring(start)
        return raw.substring(start, end + 7)
    }

    private fun generatePlaceholderWithDownloadHint(
        model: com.ai.assistance.operit.gametool.models.ModelInfo,
        extraMessage: String = ""
    ): String {
        val message = buildString {
            append("模型: ${model.displayName}<br>")
            append("下载: <a href=\"${model.mirrorUrl}\">${model.mirrorUrl}</a><br>")
            append("放置路径: &lt;外部存储&gt;/Android/data/&lt;package&gt;/files/models/${model.id}.gguf<br>")
            if (extraMessage.isNotEmpty()) append("$extraMessage<br>")
        }
        return """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { 
    width: 100vw; height: 100vh; 
    overflow: hidden; 
    background: #1a1a2e; 
    display: flex; 
    justify-content: center; 
    align-items: center; 
    font-family: 'Segoe UI', sans-serif;
    touch-action: none;
    -webkit-user-select: none;
    user-select: none;
}
canvas { display: block; }
</style>
</head>
<body>
<div id="hint" style="position:fixed;inset:0;display:flex;flex-direction:column;justify-content:center;align-items:center;padding:24px;background:#0d1117;color:#f2f6ff;text-align:center;z-index:10;font-size:14px;line-height:1.6;">
  <div style="font-size:48px;margin-bottom:12px;">🤖</div>
  <div style="max-width:420px;">$message</div>
</div>
<canvas id="game"></canvas>
<script>
(function() {
    'use strict';
    const canvas = document.getElementById('game');
    const ctx = canvas.getContext('2d');
    
    function resize() {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }
    resize();
    window.addEventListener('resize', resize);
    
    // 游戏状态
    const state = {
        score: 0,
        gameOver: false,
        started: false
    };
    
    // 游戏主循环
    function gameLoop(timestamp) {
        update(timestamp);
        render();
        requestAnimationFrame(gameLoop);
    }
    
    function update(timestamp) {
        // 游戏逻辑更新
    }
    
    function render() {
        ctx.fillStyle = '#1a1a2e';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        
        if (!state.started) {
            drawStartScreen();
            return;
        }
        if (state.gameOver) {
            drawGameOverScreen();
            return;
        }
        drawGame();
    }
    
    function drawStartScreen() {
        ctx.fillStyle = '#e94560';
        ctx.font = 'bold 36px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText('🎮 我的游戏', canvas.width/2, canvas.height/3);
        ctx.fillStyle = '#0f3460';
        ctx.fillRect(canvas.width/2-80, canvas.height/2, 160, 50);
        ctx.fillStyle = '#fff';
        ctx.font = '20px sans-serif';
        ctx.fillText('开始游戏', canvas.width/2, canvas.height/2+34);
    }
    
    function drawGameOverScreen() {
        ctx.fillStyle = '#e94560';
        ctx.font = 'bold 32px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText('游戏结束', canvas.width/2, canvas.height/3);
        ctx.fillStyle = '#fff';
        ctx.font = '24px sans-serif';
        ctx.fillText('得分: ' + state.score, canvas.width/2, canvas.height/2);
        ctx.fillStyle = '#0f3460';
        ctx.fillRect(canvas.width/2-80, canvas.height/2+40, 160, 50);
        ctx.fillStyle = '#fff';
        ctx.font = '18px sans-serif';
        ctx.fillText('重新开始', canvas.width/2, canvas.height/2+74);
    }
    
    function drawGame() {
        ctx.fillStyle = '#16213e';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = '#e94560';
        ctx.font = '20px sans-serif';
        ctx.textAlign = 'left';
        ctx.fillText('得分: ' + state.score, 16, 32);
    }
    
    // 触摸/点击处理
    canvas.addEventListener('click', function(e) {
        if (!state.started) {
            state.started = true;
            return;
        }
        if (state.gameOver) {
            state.score = 0;
            state.gameOver = false;
            state.started = true;
            return;
        }
        // 游戏交互
        state.score++;
    });
    
    canvas.addEventListener('touchstart', function(e) {
        e.preventDefault();
        if (!state.started) {
            state.started = true;
            return;
        }
        if (state.gameOver) {
            state.score = 0;
            state.gameOver = false;
            state.started = true;
            return;
        }
        state.score++;
    });
    
    requestAnimationFrame(gameLoop);
})();
</script>
</body>
</html>
        """.trimIndent()
    }
}
