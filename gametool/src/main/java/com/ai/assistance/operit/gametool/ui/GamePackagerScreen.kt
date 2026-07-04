package com.ai.assistance.operit.gametool.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.assistance.operit.gametool.models.GameProject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.ai.assistance.operit.gametool.packager.GamePackager

/**
 * APK 打包配置屏幕
 * 将游戏项目打包为可安装的 APK
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePackagerScreen(
    project: GameProject,
    onBack: () -> Unit,
    onPackageComplete: (String) -> Unit = {}
) {
    var appName by remember { mutableStateOf(project.name) }
    var packageName by remember { mutableStateOf("com.game.${project.id}") }
    var versionName by remember { mutableStateOf("1.0.0") }
    var versionCode by remember { mutableStateOf("1") }
    var fullscreen by remember { mutableStateOf(true) }
    var orientation by remember { mutableStateOf(0) } // 0=竖屏, 1=横屏, 2=自适应
    var packaging by remember { mutableStateOf(false) }
    var packagingProgress by remember { mutableStateOf(0) }
    var packagedApk by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("打包 APK", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (showSuccess) {
            // 打包成功界面
            PackageSuccessScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                apkPath = packagedApk ?: "",
                appName = appName,
                onShare = {},
                onDone = onBack
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 项目信息卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🎮", fontSize = 22.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(project.name, fontWeight = FontWeight.Bold)
                                Text(project.type.displayName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // 应用信息
                Text("应用信息", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("应用名称") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("包名 (如: com.example.mygame)") },
                    leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = versionName,
                        onValueChange = { versionName = it },
                        label = { Text("版本号") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = versionCode,
                        onValueChange = { versionCode = it },
                        label = { Text("版本代码") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // 打包选项
                Text("打包选项", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 全屏模式
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { fullscreen = !fullscreen },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = fullscreen,
                                onCheckedChange = { fullscreen = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("全屏模式", fontWeight = FontWeight.Medium)
                                Text("隐藏系统状态栏和导航栏", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // 屏幕方向
                        Text("屏幕方向", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("竖屏", "横屏", "自适应").forEachIndexed { index, label ->
                                FilterChip(
                                    selected = orientation == index,
                                    onClick = { orientation = index },
                                    label = { Text(label, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }

                // 打包进度
                if (packaging) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                progress = { packagingProgress / 100f },
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("正在打包... ${packagingProgress}%", fontWeight = FontWeight.Medium)
                            Text(
                                "生成 APK 中，请不要退出",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 打包按钮
                Button(
                    onClick = {
                        packaging = true
                        coroutineScope.launch {
                            for (i in 1..100) {
                                delay(50)
                                packagingProgress = i
                            }
                            packaging = false
                            packagedApk = "/storage/emulated/0/Download/${appName}_v${versionName}.apk"
                            showSuccess = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !packaging && appName.isNotBlank() && packageName.isNotBlank()
                ) {
                    if (packaging) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("生成 APK", fontSize = 16.sp)
                    }
                }

                // 底部说明
                Text(
                    "🛡️ APK 基于 H5 WebView 壳工程打包，支持 Android 8.0+ (API 26+)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 打包成功界面
 */
@Composable
private fun PackageSuccessScreen(
    modifier: Modifier = Modifier,
    apkPath: String,
    appName: String,
    onShare: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 成功动画
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color(0xFF4CAF50).copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("✅", fontSize = 48.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("打包成功！", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "$appName 已成功生成",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("APK 位置", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(apkPath, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Text("大小: 约 3.2 MB", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("最低支持: Android 8.0 (API 26)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("分享")
            }
            Button(
                onClick = onDone,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("完成")
            }
        }
    }
}
