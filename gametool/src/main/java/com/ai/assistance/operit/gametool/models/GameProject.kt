package com.ai.assistance.operit.gametool.models

import kotlinx.serialization.Serializable

/**
 * 游戏项目核心数据模型
 */
@Serializable
data class GameProject(
    val id: String = generateProjectId(),
    val name: String,
    val type: GameType,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val templateId: String = "",
    val entryFile: String = "index.html",
    val assets: List<String> = emptyList(),
    val sourceFiles: List<SourceFile> = emptyList(),
    val settings: GameSettings = GameSettings()
)

@Serializable
enum class GameType(val displayName: String, val extension: String) {
    H5("H5 游戏", ".html"),
    COMPOSE("Compose 原生", ".kt"),
    GODOT("Godot", ".gd")
}

@Serializable
data class SourceFile(
    val path: String,
    val content: String,
    val type: FileType = FileType.AUTO
)

@Serializable
enum class FileType {
    AUTO, HTML, CSS, JS, KOTLIN, JSON, PNG, JPG, SVG, GDSCRIPT, TSCN
}

@Serializable
data class GameSettings(
    val resolution: Resolution = Resolution(375, 667),
    val orientation: Orientation = Orientation.PORTRAIT,
    val fps: Int = 60,
    val showFps: Boolean = false,
    val backgroundColor: String = "#1a1a2e",
    val enableTouch: Boolean = true,
    val enableKeyboard: Boolean = true
)

@Serializable
data class Resolution(val width: Int, val height: Int)

@Serializable
enum class Orientation { PORTRAIT, LANDSCAPE, AUTO }

fun generateProjectId(): String {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
    return (1..12).map { chars.random() }.joinToString("")
}
