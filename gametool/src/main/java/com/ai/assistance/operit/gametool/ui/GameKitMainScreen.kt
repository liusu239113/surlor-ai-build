package com.ai.assistance.operit.gametool.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.assistance.operit.gametool.models.GameProject
import com.ai.assistance.operit.gametool.models.GameType
import com.ai.assistance.operit.gametool.models.GameModelConfig
import com.ai.assistance.operit.gametool.templates.GameTemplateManager

/**
 * GameKit 主屏幕
 * 游戏开发工具的总入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameKitMainScreen(
    onCreateProject: (GameType) -> Unit = {},
    onOpenProject: (GameProject) -> Unit = {},
    onAiGenerate: () -> Unit = {},
    recentProjects: List<GameProject> = emptyList()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Surlor AI 开发工作室", fontWeight = FontWeight.Bold)
                        Text("AI 驱动的游戏开发工具", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // === AI 生成游戏入口 ===
            item {
                AiGenerateCard(onClick = onAiGenerate)
            }

            // === 快速创建 ===
            item {
                Text("快速创建", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            
            item {
                QuickCreateGrid(onCreateProject = onCreateProject)
            }

            // === 游戏模板 ===
            item {
                Text("游戏模板", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            
            item {
                GameTemplatesSection(
                    templates = GameTemplateManager.getTemplates()
                        .filter { it.category != com.ai.assistance.operit.gametool.templates.GameTemplateManager.TemplateCategory.EMPTY }
                        .take(8),
                    onSelectTemplate = { template ->
                        val project = GameTemplateManager.createProjectFromTemplate(template.id)
                        if (project != null) onOpenProject(project)
                    }
                )
            }

            // === 最近项目 ===
            if (recentProjects.isNotEmpty()) {
                item {
                    Text("最近项目", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                items(recentProjects.take(5)) { project ->
                    RecentProjectCard(
                        project = project,
                        onClick = { onOpenProject(project) }
                    )
                }
            }

            // === 内置模型信息 ===
            item {
                ModelInfoCard()
            }

            // === 底部间距 ===
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * AI 对话生成游戏 - 大入口卡片
 */
@Composable
private fun AiGenerateCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧图标
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 右侧文字
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "🎮 用 AI 对话生成游戏",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "描述你想做的游戏，AI 自动生成代码并实时预览",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            // 箭头
            Icon(
                Icons.Default.Gamepad,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * 快速创建网格
 */
@Composable
private fun QuickCreateGrid(
    onCreateProject: (GameType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickCreateItem(
            modifier = Modifier.weight(1f),
            emoji = "🌐",
            title = "H5 游戏",
            subtitle = "即写即预览",
            onClick = { onCreateProject(GameType.H5) }
        )
        QuickCreateItem(
            modifier = Modifier.weight(1f),
            emoji = "📱",
            title = "Compose 原生",
            subtitle = "Kotlin + 原生性能",
            onClick = { onCreateProject(GameType.COMPOSE) }
        )
        QuickCreateItem(
            modifier = Modifier.weight(1f),
            emoji = "🎮",
            title = "Godot",
            subtitle = "专业游戏引擎",
            onClick = { onCreateProject(GameType.GODOT) }
        )
    }
}

@Composable
private fun QuickCreateItem(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 游戏模板横向列表
 */
@Composable
private fun GameTemplatesSection(
    templates: List<com.ai.assistance.operit.gametool.templates.GameTemplateManager.GameTemplate>,
    onSelectTemplate: (com.ai.assistance.operit.gametool.templates.GameTemplateManager.GameTemplate) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(templates) { template ->
            TemplateCard(template = template, onClick = { onSelectTemplate(template) })
        }
    }
}

@Composable
private fun TemplateCard(
    template: com.ai.assistance.operit.gametool.templates.GameTemplateManager.GameTemplate,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(template.icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                template.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                template.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 最近项目卡片
 */
@Composable
private fun RecentProjectCard(
    project: GameProject,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        when (project.type) {
                            GameType.H5 -> "🌐"
                            GameType.COMPOSE -> "📱"
                            GameType.GODOT -> "🎮"
                        },
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name, fontWeight = FontWeight.Medium)
                Text(
                    project.type.displayName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 内置模型信息卡片
 */
@Composable
private fun ModelInfoCard() {
    val model = GameModelConfig.getRecommendedModel()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("内置代码模型", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "推荐: ${model.displayName} (${model.paramSize} · ${model.quantization})",
                fontSize = 13.sp
            )
            Text(
                "大小: ${model.fileSizeMb / 1024.0}GB · 最低内存: ${model.minRamMb / 1024}GB+",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
