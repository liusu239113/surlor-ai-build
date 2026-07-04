package com.ai.assistance.operit.ui.features.modelselection

import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.assistance.operit.gametool.download.ModelDownloadManager
import com.ai.assistance.operit.gametool.models.GameModelConfig
import com.ai.assistance.operit.gametool.models.ModelInfo
import com.ai.assistance.operit.ui.theme.AccentBlue
import com.ai.assistance.operit.ui.theme.AccentOrange
import com.ai.assistance.operit.ui.theme.GameSurface
import com.ai.assistance.operit.ui.theme.GameSurfaceDark
import com.ai.assistance.operit.ui.theme.GameSurfaceLight
import com.ai.assistance.operit.ui.theme.SuccessGreen
import com.ai.assistance.operit.ui.theme.TerminalGreen
import com.ai.assistance.operit.ui.theme.WarningAmber
import kotlinx.coroutines.launch

/**
 * 首次启动模型选择界面
 *
 * 展示约 10 个可选本地模型，包含排名、评分、体积、内存和适用场景。
 * 用户可以选择下载某个模型，或跳过使用云端 API。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectionScreen(
    models: List<ModelInfo> = GameModelConfig.ALL_MODELS,
    onModelDownloaded: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val totalRamMb = remember { getDeviceTotalRamMb(context) }

    var selectedModelId by remember { mutableStateOf<String?>(null) }
    var downloadProgress by remember { mutableStateOf<ModelDownloadManager.DownloadProgress?>(null) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    var completedModelId by remember { mutableStateOf<String?>(null) }

    val isDownloading = downloadProgress?.status == ModelDownloadManager.DownloadProgress.Status.DOWNLOADING
    val isPending = downloadProgress?.status == ModelDownloadManager.DownloadProgress.Status.PENDING

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Surlor AI 模型中心",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "选择本地 AI 模型 · 也可以跳过使用 API",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GameSurfaceDark
                )
            )
        },
        containerColor = GameSurfaceDark,
        bottomBar = {
            Surface(
                color = GameSurface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (downloadProgress != null && downloadError == null) {
                        DownloadStatusBar(
                            progress = downloadProgress!!,
                            modelName = models.find { it.id == selectedModelId }?.displayName ?: ""
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (downloadError != null) {
                        Text(
                            text = downloadError.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = {
                                ModelDownloadManager.setModelSelectionCompleted(context, true)
                                onSkip()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isDownloading && !isPending
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("跳过，使用 API")
                        }

                        Button(
                            onClick = {
                                val target = selectedModelId?.let { id ->
                                    models.find { it.id == id }
                                } ?: GameModelConfig.getRecommendedModel()
                                selectedModelId = target.id
                                downloadError = null
                                scope.launch {
                                    ModelDownloadManager.downloadModel(
                                        context = context,
                                        model = target,
                                        useMirror = true,
                                        onProgress = { progress ->
                                            downloadProgress = progress
                                        }
                                    ).fold(
                                        onSuccess = {
                                            completedModelId = target.id
                                            ModelDownloadManager.setModelSelectionCompleted(context, true)
                                            onModelDownloaded()
                                        },
                                        onFailure = { error ->
                                            downloadError = "下载失败: ${error.message ?: "未知错误"}"
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            enabled = !isDownloading && !isPending && completedModelId == null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentOrange,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (completedModelId != null) "已下载" else "下载选中模型",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GameSurfaceDark),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderSection(totalRamMb = totalRamMb)
            }

            item {
                Text(
                    text = "模型排行榜 (${models.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "排名根据代码生成、游戏开发适配、手机端性能和社区评分综合得出",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            items(models, key = { it.id }) { model ->
                ModelCard(
                    model = model,
                    isSelected = model.id == selectedModelId,
                    isRecommended = model.recommended,
                    isCompleted = model.id == completedModelId,
                    deviceRamMb = totalRamMb,
                    onClick = {
                        if (!isDownloading && !isPending) {
                            selectedModelId = model.id
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection(totalRamMb: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GameSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = AccentOrange,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "首次启动配置",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "选择一个开源模型下载到本地，模型不打包在 APK 中",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DeviceInfoItem(
                    icon = Icons.Default.Memory,
                    label = "设备内存",
                    value = "${totalRamMb / 1024}GB"
                )
                DeviceInfoItem(
                    icon = Icons.Default.Speed,
                    label = "建议模型",
                    value = GameModelConfig.getModelForDevice(totalRamMb).displayName
                )
            }
        }
    }
}

@Composable
private fun DeviceInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ModelCard(
    model: ModelInfo,
    isSelected: Boolean,
    isRecommended: Boolean,
    isCompleted: Boolean,
    deviceRamMb: Long,
    onClick: () -> Unit
) {
    val isCompatible = deviceRamMb >= model.minRamMb
    val borderColor = when {
        isCompleted -> SuccessGreen
        isSelected -> AccentOrange
        isRecommended -> AccentOrange.copy(alpha = 0.5f)
        else -> Color.White.copy(alpha = 0.08f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = GameSurface
        ),
        border = BorderStroke(
            width = if (isSelected || isCompleted) 2.dp else 1.dp,
            color = borderColor
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 排名徽章
                RankBadge(rank = model.rank, recommended = isRecommended)
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = model.displayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (isRecommended) {
                            Spacer(modifier = Modifier.width(6.dp))
                            RecommendedTag()
                        }
                    }
                    Text(
                        text = "${model.provider} · ${model.paramSize} · ${model.quantization}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = AccentOrange,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = model.description,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 评分条
            ScoreRow(label = "代码生成", score = model.codeGenerationScore, color = AccentBlue)
            Spacer(modifier = Modifier.height(4.dp))
            ScoreRow(label = "游戏开发", score = model.gameDevScore, color = AccentOrange)
            Spacer(modifier = Modifier.height(4.dp))
            ScoreRow(label = "手机适配", score = model.phoneCompatibilityScore, color = TerminalGreen)
            Spacer(modifier = Modifier.height(4.dp))
            ScoreRow(
                label = "市场评分",
                score = (model.marketRating * 2).toInt(),
                color = WarningAmber,
                suffix = " ${model.marketRating}/5.0"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 体积与内存
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecItem(
                    icon = Icons.Default.Storage,
                    label = "模型大小",
                    value = formatSizeMb(model.fileSizeMb)
                )
                SpecItem(
                    icon = Icons.Default.Memory,
                    label = "最低内存",
                    value = "${model.minRamMb / 1024}GB"
                )
                if (!isCompatible) {
                    Text(
                        text = "内存不足",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 适用场景
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                model.suitableFor.forEach { tag ->
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = GameSurfaceLight,
                            labelColor = Color.White.copy(alpha = 0.9f)
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RankBadge(rank: Int, recommended: Boolean) {
    val color = if (recommended) AccentOrange else AccentBlue
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#${rank}",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun RecommendedTag() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = AccentOrange
    ) {
        Text(
            text = "推荐",
            color = Color.Black,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ScoreRow(
    label: String,
    score: Int,
    color: Color,
    suffix: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            modifier = Modifier.width(56.dp)
        )
        LinearProgressIndicator(
            progress = { score / 10f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f),
            drawStopIndicator = {}
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$score/10$suffix",
            color = color,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun SpecItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: ",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DownloadStatusBar(
    progress: ModelDownloadManager.DownloadProgress,
    modelName: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = GameSurfaceLight
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                when (progress.status) {
                    ModelDownloadManager.DownloadProgress.Status.PENDING ->
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = AccentOrange,
                            strokeWidth = 2.dp
                        )
                    ModelDownloadManager.DownloadProgress.Status.DOWNLOADING ->
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = AccentOrange,
                            strokeWidth = 2.dp
                        )
                    ModelDownloadManager.DownloadProgress.Status.PAUSED ->
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null,
                            tint = WarningAmber
                        )
                    ModelDownloadManager.DownloadProgress.Status.FAILED ->
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    ModelDownloadManager.DownloadProgress.Status.COMPLETED ->
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen
                        )
                    else -> Unit
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${modelName} · ${progress.status.label}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${formatBytes(progress.downloadedBytes)} / ${formatBytes(progress.totalBytes)}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "${progress.percent}%",
                    color = AccentOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.percent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when (progress.status) {
                    ModelDownloadManager.DownloadProgress.Status.COMPLETED -> SuccessGreen
                    ModelDownloadManager.DownloadProgress.Status.FAILED -> MaterialTheme.colorScheme.error
                    else -> AccentOrange
                },
                trackColor = Color.White.copy(alpha = 0.1f),
                drawStopIndicator = {}
            )
        }
    }
}

private val ModelDownloadManager.DownloadProgress.Status.label: String
    get() = when (this) {
        ModelDownloadManager.DownloadProgress.Status.IDLE -> "等待中"
        ModelDownloadManager.DownloadProgress.Status.PENDING -> "连接中"
        ModelDownloadManager.DownloadProgress.Status.DOWNLOADING -> "下载中"
        ModelDownloadManager.DownloadProgress.Status.PAUSED -> "已暂停"
        ModelDownloadManager.DownloadProgress.Status.COMPLETED -> "已完成"
        ModelDownloadManager.DownloadProgress.Status.FAILED -> "失败"
    }

private fun getDeviceTotalRamMb(context: Context): Long {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager?.getMemoryInfo(memoryInfo)
    return memoryInfo.totalMem / (1024 * 1024)
}

private fun formatSizeMb(sizeMb: Long): String {
    return if (sizeMb >= 1024) {
        String.format("%.2fGB", sizeMb / 1024.0)
    } else {
        "${sizeMb}MB"
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        bytes >= 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
        bytes >= 1024 -> String.format("%.2f KB", bytes / 1024.0)
        else -> "$bytes B"
    }
}
