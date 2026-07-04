package com.ai.assistance.operit.gametool.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.assistance.operit.gametool.engine.GameGenerator
import com.ai.assistance.operit.gametool.models.GameProject
import com.ai.assistance.operit.gametool.models.GameType
import com.ai.assistance.operit.gametool.models.SourceFile
import com.ai.assistance.operit.gametool.preview.GamePreviewView
import kotlinx.coroutines.launch

/**
 * 游戏编辑器屏幕
 * 包含: AI 聊天区 + 代码预览 + 实时游戏预览
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEditorScreen(
    project: GameProject,
    onBack: () -> Unit,
    onPackageApk: () -> Unit = {},
    onSave: (GameProject) -> Unit = {}
) {
    var currentSource by remember { mutableStateOf(
        project.sourceFiles.firstOrNull()?.content ?: ""
    ) }
    var showPreview by remember { mutableStateOf(true) }
    var messages by remember { mutableStateOf(
        listOf(
            ChatMessage(
                role = "assistant",
                content = "🎮 你好！我是你的游戏开发助手。你可以描述你想做的游戏，我帮你生成代码并实时预览。\n\n例如：\"做一个类似 Flappy Bird 的游戏，小鸟上下飞过管道\""
            )
        )
    ) }
    var inputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(project.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(project.type.displayName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 切换预览
                    IconButton(onClick = { showPreview = !showPreview }) {
                        Icon(
                            if (showPreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "切换预览"
                        )
                    }
                    // 打包 APK
                    IconButton(onClick = onPackageApk) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = "打包APK")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (showPreview) {
            // 分屏模式: 上面预览 + 下面 AI 聊天
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 游戏预览区 (上半部分)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.45f)
                ) {
                    if (project.type == GameType.H5 && currentSource.isNotEmpty()) {
                        GamePreviewView(
                            project = project,
                            sourceFile = SourceFile(
                                path = project.entryFile,
                                content = currentSource
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // 预览占位
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎮", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "游戏预览区域",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "AI 生成游戏代码后将在此实时显示",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                // AI 聊天区 (下半部分)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.55f)
                ) {
                    // 聊天消息列表
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(messages) { msg ->
                            ChatBubble(message = msg)
                        }
                        if (isGenerating) {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("🤖 AI 正在生成游戏代码...", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    // 输入框
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 输入框
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                BasicTextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    decorationBox = { innerTextField ->
                                        if (inputText.isEmpty()) {
                                            Text(
                                                "描述你想要的游戏...",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // 发送按钮
                            FilledIconButton(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        val userMsg = inputText
                                        inputText = ""
                                        isGenerating = true
                                        
                                        messages = messages + ChatMessage(
                                            role = "user",
                                            content = userMsg
                                        )
                                        
                                        // 使用 Compose 协程作用域启动
                                        coroutineScope.launch {
                                            val result = GameGenerator.generateGame(
                                                context = context,
                                                description = userMsg,
                                                project = project.copy(description = userMsg)
                                            ) { }
                                            val newCode = result.getOrNull()?.sourceFiles?.firstOrNull()?.content
                                                ?: generateGameCode(userMsg)
                                            currentSource = newCode
                                            messages = messages + ChatMessage(
                                                role = "assistant",
                                                content = """
✅ 游戏代码已生成并更新到预览区！

- 游戏类型: H5
- 屏幕适配: 已自动调整
- 触摸/鼠标: 已启用

✨ 你可以：
1. 直接在预览区试玩
2. 继续描述修改需求
3. 点击打包按钮生成 APK

如果预览区提示需要下载模型，请到设置中下载内置 GGUF 模型后重试。
                                                """.trimIndent()
                                            )
                                            isGenerating = false
                                        }
                                    }
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "发送")
                            }
                        }
                    }
                }
            }
        } else {
            // 纯聊天模式
            ChatOnlyMode(
                padding = padding,
                messages = messages,
                inputText = inputText,
                onInputChange = { inputText = it },
                onSend = { msg ->
                    messages = messages + ChatMessage(role = "user", content = msg)
                    inputText = ""
                },
                isGenerating = isGenerating
            )
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🤖", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(14.dp),
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
        
        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("👤", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ChatOnlyMode(
    padding: PaddingValues,
    messages: List<ChatMessage>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: (String) -> Unit,
    isGenerating: Boolean
) {
    val listState = rememberLazyListState()
    
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { msg -> ChatBubble(message = msg) }
            if (isGenerating) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("🤖 AI 正在思考...", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) Text("描述你想要的游戏...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            innerTextField()
                        }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = { if (inputText.isNotBlank()) { onSend(inputText) } },
                    modifier = Modifier.size(48.dp)
                ) { Icon(Icons.Default.Send, contentDescription = "发送") }
            }
        }
    }
}

// ========== 数据类 ==========

data class ChatMessage(
    val role: String,
    val content: String
)

// 简易游戏代码生成器 (占位，实际使用 AI 模型推理生成)
private fun generateGameCode(description: String): String {
    // TODO: 接入本地 AI 模型推理
    return generateSimpleGameHtml(description)
}

private fun generateSimpleGameHtml(desc: String): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#1a1a2e;display:flex;justify-content:center;align-items:center;font-family:sans-serif;touch-action:none;}
canvas{display:block;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    function resize(){c.width=innerWidth;c.height=innerHeight;} resize();
    addEventListener('resize',resize);
    
    let s={started:false,over:false,score:0};
    
    function loop(t){u(t);draw();requestAnimationFrame(loop);}
    
    function u(t){
        if(!s.started||s.over)return;
    }
    
    function draw(){
        ctx.fillStyle='#1a1a2e';ctx.fillRect(0,0,c.width,c.height);
        
        if(!s.started){
            ctx.fillStyle='#e94560';ctx.font='bold 30px sans-serif';ctx.textAlign='center';
            ctx.fillText('🎮 AI 生成的游戏',c.width/2,c.height/3);
            ctx.textContent = '${desc.escapeHtml()}';
            ctx.fillStyle='#0f3460';ctx.fillRect(c.width/2-80,c.height/2-15,160,50);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';
            ctx.fillText('点击开始',c.width/2,c.height/2+12);
            return;
        }
        
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.5)';ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#fff';ctx.font='bold 28px sans-serif';ctx.textAlign='center';
            ctx.fillText('游戏结束',c.width/2,c.height/2-30);
            ctx.font='18px sans-serif';ctx.fillText('得分: '+s.score,c.width/2,c.height/2+5);
            ctx.fillStyle='#e94560';ctx.fillRect(c.width/2-60,c.height/2+30,120,40);
            ctx.fillStyle='#fff';ctx.font='14px sans-serif';ctx.fillText('重来',c.width/2,c.height/2+55);
            return;
        }
        
        ctx.fillStyle='#16213e';ctx.fillRect(0,0,c.width,c.height);
        ctx.fillStyle='#e94560';ctx.font='20px sans-serif';ctx.textAlign='left';
        ctx.fillText('分数: '+s.score,10,30);
    }
    
    function handleInput(){
        if(s.over){s.score=0;s.over=false;s.started=true;return;}
        if(!s.started){s.started=true;return;}
        s.score++;
    }
    
    addEventListener('click',handleInput);
    addEventListener('touchstart',e=>{e.preventDefault();handleInput();});
    addEventListener('keydown',e=>{if(e.code=='Space')handleInput();});
    
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
""".trimIndent()

// HTML 转义工具
private fun String.escapeHtml(): String = this
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")

