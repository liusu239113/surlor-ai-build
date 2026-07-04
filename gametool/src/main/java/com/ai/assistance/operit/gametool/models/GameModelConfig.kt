package com.ai.assistance.operit.gametool.models

/**
 * 游戏开发专用 AI 模型配置
 *
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  🏆 推荐模型: Llama 3.2 3B (GGUF Q4_K_M)                   ║
 * ║  下载地址 (2.02GB):                                         ║
 * ║  https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-    ║
 * ║      GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf   ║
 * ║                                                            ║
 * ║  镜像 (国内快):                                             ║
 * ║  https://modelscope.cn/models/llama-community/              ║
 * ║      Llama-3.2-3B-Instruct-GGUF/resolve/master/            ║
 * ║      Llama-3.2-3B-Instruct-Q4_K_M.gguf                     ║
 * ║                                                            ║
 * ║  ARM手机优化版 (Q4_0_4_4, 1.92GB):                          ║
 * ║  .../Llama-3.2-3B-Instruct-Q4_0_4_4.gguf                  ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * - 参数量: 3B (Q4_K_M 量化后约 2GB)
 * - 内存需求: 6GB+ RAM (手机端)
 * - 代码生成能力: 顶级 (手机端模型排名前3)
 * - 通用能力: 优秀 (聊天、推理、代码全能)
 * - 格式: GGUF (llama.cpp)
 *
 * 备选方案:
 * - Granite 4.0 H 1B (代码专用，体积更小，~780MB)
 * - Qwen3 1.7B (推理能力强，~1.1GB)
 */
object GameModelConfig {

    /**
     * 主要模型 - Llama 3.2 3B Instruct (GGUF Q4_K_M)
     * 最佳全能型，代码生成能力出色，适合作为游戏开发助手的默认模型
     */
    val PRIMARY_MODEL = ModelInfo(
        id = "llama-3.2-3b-instruct-q4",
        name = "Llama 3.2 3B Q4_K_M",
        displayName = "Llama 3.2 3B 🏆 (推荐)",
        description = "Meta 出品全能模型，3B参数量，代码生成和推理能力在手机端模型中最强。Q4_K_M量化仅2GB，6GB以上手机流畅运行，最佳游戏开发助手",
        provider = "Meta",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "3B",
        fileSizeMb = 2068,
        minRamMb = 6144,
        url = "https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
        mirrorUrl = "https://modelscope.cn/models/llama-community/Llama-3.2-3B-Instruct-GGUF/resolve/master/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
        recommended = true,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT,
            ModelCapability.CHAT,
            ModelCapability.REASONING
        )
    )

    /**
     * ARM优化版 - Llama 3.2 3B (GGUF Q4_0_4_4)
     * 专为ARM手机芯片优化，速度更快，体积略小
     */
    val ARM_OPTIMIZED_MODEL = ModelInfo(
        id = "llama-3.2-3b-instruct-q4-arm",
        name = "Llama 3.2 3B Q4_0_4_4",
        displayName = "Llama 3.2 3B (ARM优化)",
        description = "专为ARM架构手机芯片优化(Q4_0_4_4)，在骁龙/天玑/麒麟上速度更快，仅1.92GB",
        provider = "Meta",
        format = ModelFormat.GGUF,
        quantization = "Q4_0_4_4",
        paramSize = "3B",
        fileSizeMb = 1966,
        minRamMb = 6144,
        url = "https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_0_4_4.gguf",
        mirrorUrl = "https://modelscope.cn/models/llama-community/Llama-3.2-3B-Instruct-GGUF/resolve/master/Llama-3.2-3B-Instruct-Q4_0_4_4.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT,
            ModelCapability.CHAT
        )
    )

    /**
     * 轻量备选 - Granite 4.0 H 1B
     * IBM 出品，专门为代码生成优化，体积小速度快
     */
    val LIGHT_MODEL = ModelInfo(
        id = "granite-4.0-h-1b-gguf",
        name = "Granite 4.0 1B",
        displayName = "Granite Code 1B (轻量)",
        description = "IBM 代码专用模型，1B参数量，代码生成速度极快，适合低端手机",
        provider = "IBM",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "1B",
        fileSizeMb = 780,
        minRamMb = 2048,
        url = "https://huggingface.co/ibm-granite/granite-4.0-h-1b-GGUF/resolve/main/granite-4.0-h-1b-Q4_K_M.gguf",
        mirrorUrl = "https://modelscope.cn/models/IBM/granite-4.0-h-1b-GGUF/resolve/master/granite-4.0-h-1b-Q4_K_M.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT
        )
    )

    /**
     * 推理增强备选 - Qwen3 1.7B
     * 阿里巴巴出品，推理能力强，代码能力均衡
     */
    val REASONING_MODEL = ModelInfo(
        id = "qwen3-1.7b-gguf",
        name = "Qwen3 1.7B",
        displayName = "Qwen3 1.7B (推理)",
        description = "阿里通义千问3代，1.7B参数量，推理能力突出，适合需要逻辑分析的游戏开发场景",
        provider = "Alibaba",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "1.7B",
        fileSizeMb = 1100,
        minRamMb = 4096,
        url = "https://huggingface.co/Qwen/Qwen3-1.7B-GGUF/resolve/main/qwen3-1.7b-q4_k_m.gguf",
        mirrorUrl = "https://modelscope.cn/models/qwen/Qwen3-1.7B-GGUF/resolve/master/qwen3-1.7b-q4_k_m.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.REASONING,
            ModelCapability.CHAT
        )
    )

    // 所有可用模型
    val ALL_MODELS = listOf(PRIMARY_MODEL, ARM_OPTIMIZED_MODEL, LIGHT_MODEL, REASONING_MODEL)

    /**
     * 获取推荐模型
     */
    fun getRecommendedModel(): ModelInfo = PRIMARY_MODEL

    /**
     * 根据设备内存推荐模型
     */
    fun getModelForDevice(totalRamMb: Long): ModelInfo {
        return when {
            totalRamMb >= 6144 -> PRIMARY_MODEL  // 8GB+ 设备，推荐完整版
            totalRamMb >= 4096 -> REASONING_MODEL // 6GB 设备
            else -> LIGHT_MODEL                    // 4GB 及以下设备
        }
    }

    /**
     * 获取 ARM 优化版（骁龙/天玑/麒麟推荐）
     */
    fun getArmOptimizedModel(): ModelInfo = ARM_OPTIMIZED_MODEL
}

/**
 * 模型信息
 */
data class ModelInfo(
    val id: String,
    val name: String,
    val displayName: String,
    val description: String,
    val provider: String,
    val format: ModelFormat,
    val quantization: String,
    val paramSize: String,
    val fileSizeMb: Long,
    val minRamMb: Long,
    val url: String,
    val mirrorUrl: String,
    val recommended: Boolean = false,
    val capabilities: List<ModelCapability> = emptyList()
)

enum class ModelFormat {
    GGUF,
    MNN
}

enum class ModelCapability {
    CODE_GENERATION,
    GAME_SCRIPT,
    CHAT,
    REASONING,
    VISION
}
