package com.ai.assistance.operit.gametool.preview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ai.assistance.operit.gametool.models.GameProject
import com.ai.assistance.operit.gametool.models.GameType
import com.ai.assistance.operit.gametool.models.SourceFile

/**
 * 游戏实时预览引擎
 * 
 * H5 游戏可以直接在 WebView 中实时预览，无需构建。
 * 代码修改后刷新 WebView 即可看到效果。
 */
class GamePreviewEngine {

    private var webView: WebView? = null
    private var currentProject: GameProject? = null
    private var bridge: GameBridge? = null

    /**
     * 预览 H5 游戏
     */
    fun previewH5Game(webView: WebView, sourceFile: SourceFile) {
        this.webView = webView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            setSupportMultipleWindows(false)
            builtInZoomControls = false
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
            mediaPlaybackRequiresUserGesture = false
        }
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean = false
        }

        // 注入游戏桥接接口
        bridge = GameBridge()
        webView.addJavascriptInterface(bridge!!, "GameKitBridge")

        // 加载游戏 HTML
        webView.loadDataWithBaseURL(
            "file:///android_asset/",
            sourceFile.content,
            "text/html",
            "UTF-8",
            null
        )
    }

    /**
     * 实时更新游戏代码（热重载）
     */
    fun hotReload(sourceFile: SourceFile) {
        webView?.let { wv ->
            wv.post {
                wv.loadDataWithBaseURL(
                    "file:///android_asset/",
                    sourceFile.content,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }
    }

    /**
     * 发送消息到游戏
     */
    fun sendMessage(json: String) {
        webView?.evaluateJavascript("window._onKitMessage($json)", null)
    }

    /**
     * 清理
     */
    fun destroy() {
        bridge = null
        webView?.removeJavascriptInterface("GameKitBridge")
        webView = null
    }

    /**
     * 游戏与宿主的桥接接口
     */
    class GameBridge {
        @JavascriptInterface
        fun onGameReady() {
            // 游戏加载完成回调
        }

        @JavascriptInterface
        fun onScoreUpdate(score: Int) {
            // 分数更新
        }

        @JavascriptInterface
        fun onGameEvent(event: String) {
            // 游戏事件
        }

        @JavascriptInterface
        fun onLog(message: String) {
            // 调试日志
            android.util.Log.d("GameKit", message)
        }
    }
}

/**
 * Compose 可用的游戏预览组件
 * 支持所有游戏类型的预览
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GamePreviewView(
    project: GameProject,
    sourceFile: SourceFile?,
    modifier: Modifier = Modifier,
    onEngineReady: (GamePreviewEngine) -> Unit = {}
) {
    val engine = remember { GamePreviewEngine() }
    var isReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            engine.destroy()
        }
    }

    when (project.type) {
        GameType.H5 -> {
            AndroidView(
                modifier = modifier,
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        if (sourceFile != null) {
                            engine.previewH5Game(this, sourceFile)
                            onEngineReady(engine)
                            isReady = true
                        }
                    }
                }
            )
        }
        GameType.COMPOSE -> {
            // Compose 原生游戏直接在 Compose 中渲染
            // 通过 Compose DSL 渲染
        }
        GameType.GODOT -> {
            // Godot 游戏需要 Godot runtime
            // 暂不支持即时预览
        }
    }
}
