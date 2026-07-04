package com.ai.assistance.operit.gametool.models

/**
 * 游戏开发专用 AI 模型配置
 *
 * 首次启动时提示用户从列表中选择并下载模型，不打包进 APK。
 * 所有模型均提供 HuggingFace 官方链接与国内 ModelScope 镜像。
 */
object GameModelConfig {

    /**
     * 01. 综合推荐 - Qwen2.5-Coder-3B-Instruct (GGUF Q4_K_M)
     * 3B 代码模型中综合能力最强，H5/JS/Kotlin/Compose 游戏代码生成最稳。
     */
    val QWEN_CODER_3B = ModelInfo(
        id = "qwen2.5-coder-3b-instruct-q4-k-m",
        name = "Qwen2.5-Coder-3B-Instruct-Q4_K_M",
        displayName = "Qwen2.5-Coder 3B",
        description = "阿里通义千问代码专用模型，3B参数量。H5/Kotlin/Compose游戏代码生成能力在手机端同体积模型中最强，支持128K上下文。",
        provider = "Alibaba",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "3B",
        fileSizeMb = 1843,
        minRamMb = 6144,
        url = "https://huggingface.co/Qwen/Qwen2.5-Coder-3B-Instruct-GGUF/resolve/main/qwen2.5-coder-3b-instruct-q4_k_m.gguf",
        mirrorUrl = "https://modelscope.cn/models/qwen/Qwen2.5-Coder-3B-Instruct-GGUF/resolve/master/qwen2.5-coder-3b-instruct-q4_k_m.gguf",
        recommended = true,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT,
            ModelCapability.CHAT,
            ModelCapability.REASONING
        ),
        codeGenerationScore = 9,
        gameDevScore = 9,
        phoneCompatibilityScore = 8,
        marketRating = 4.8f,
        suitableFor = listOf("H5/JS游戏", "Kotlin/Compose", "代码补全", "小游戏原型"),
        rank = 1
    )

    /**
     * 02. 高性能大杯 - Qwen2.5-Coder-7B-Instruct (GGUF Q4_K_M)
     * 代码能力顶尖，但体积和内存占用更高，建议 8GB+ 内存设备。
     */
    val QWEN_CODER_7B = ModelInfo(
        id = "qwen2.5-coder-7b-instruct-q4-k-m",
        name = "Qwen2.5-Coder-7B-Instruct-Q4_K_M",
        displayName = "Qwen2.5-Coder 7B",
        description = "阿里通义千问代码大模型，7B参数量，代码生成能力接近顶尖水平，支持128K上下文。适合复杂游戏逻辑与大型项目。",
        provider = "Alibaba",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "7B",
        fileSizeMb = 4680,
        minRamMb = 8192,
        url = "https://huggingface.co/Qwen/Qwen2.5-Coder-7B-Instruct-GGUF/resolve/main/qwen2.5-coder-7b-instruct-q4_k_m.gguf",
        mirrorUrl = "https://modelscope.cn/models/prithivMLmods/Qwen2.5-Coder-7B-GGUF/resolve/master/Qwen2.5-Coder-7B.Q4_K_M.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT,
            ModelCapability.CHAT,
            ModelCapability.REASONING
        ),
        codeGenerationScore = 10,
        gameDevScore = 10,
        phoneCompatibilityScore = 6,
        marketRating = 4.9f,
        suitableFor = listOf("复杂游戏逻辑", "大型项目", "长上下文", "高性能设备"),
        rank = 2
    )

    /**
     * 03. ARM 优化版 - Qwen2.5-Coder-3B Q4_0_4_4
     * 专为 ARM 手机芯片优化，在骁龙/天玑/麒麟上速度更快，体积略小。
     */
    val QWEN_CODER_3B_ARM = ModelInfo(
        id = "qwen2.5-coder-3b-instruct-q4-0-4-4",
        name = "Qwen2.5-Coder-3B-Instruct-Q4_0_4_4",
        displayName = "Qwen2.5-Coder 3B ARM版",
        description = "ARM架构优化量化版本，在天玑/骁龙/麒麟手机上推理速度比标准Q4_K_M更快，体积略小，适合追求响应速度的用户。",
        provider = "Alibaba",
        format = ModelFormat.GGUF,
        quantization = "Q4_0_4_4",
        paramSize = "3B",
        fileSizeMb = 1820,
        minRamMb = 6144,
        url = "https://huggingface.co/Qwen/Qwen2.5-Coder-3B-Instruct-GGUF/resolve/main/qwen2.5-coder-3b-instruct-q4_0_4_4.gguf",
        mirrorUrl = "https://modelscope.cn/models/qwen/Qwen2.5-Coder-3B-Instruct-GGUF/resolve/master/qwen2.5-coder-3b-instruct-q4_0_4_4.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT,
            ModelCapability.CHAT
        ),
        codeGenerationScore = 9,
        gameDevScore = 9,
        phoneCompatibilityScore = 9,
        marketRating = 4.7f,
        suitableFor = listOf("ARM手机优化", "快速推理", "H5/JS游戏", "实时预览"),
        rank = 3
    )

    /**
     * 04. 轻量入门 - Qwen2.5-Coder-1.5B
     * 约 1.0 GB，4GB 内存手机也能跑，适合做简单游戏原型。
     */
    val QWEN_CODER_1_5B = ModelInfo(
        id = "qwen2.5-coder-1.5b-instruct-q4-k-m",
        name = "Qwen2.5-Coder-1.5B-Instruct-Q4_K_M",
        displayName = "Qwen2.5-Coder 1.5B",
        description = "轻量代码模型，约1.0GB，对低端手机友好。适合简单游戏原型、代码补全和快速体验，不适合复杂项目。",
        provider = "Alibaba",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "1.5B",
        fileSizeMb = 1024,
        minRamMb = 4096,
        url = "https://huggingface.co/Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF/resolve/main/qwen2.5-coder-1.5b-instruct-q4_k_m.gguf",
        mirrorUrl = "https://modelscope.cn/models/qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF/resolve/master/qwen2.5-coder-1.5b-instruct-q4_k_m.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT
        ),
        codeGenerationScore = 7,
        gameDevScore = 7,
        phoneCompatibilityScore = 10,
        marketRating = 4.5f,
        suitableFor = listOf("低端手机", "简单原型", "入门体验", "小游戏脚本"),
        rank = 4
    )

    /**
     * 05. 代码专家 - DeepSeek-Coder-V2-Lite-Instruct
     * 16B 总参数但 MoE 架构，激活参数约 2.4B，代码能力极强。
     */
    val DEEPSEEK_CODER_V2_LITE = ModelInfo(
        id = "deepseek-coder-v2-lite-instruct-q4-k-m",
        name = "DeepSeek-Coder-V2-Lite-Instruct-Q4_K_M",
        displayName = "DeepSeek-Coder-V2-Lite",
        description = "深度求索代码专家模型，MoE架构（16B总参数/2.4B激活），代码生成能力极强，H5与算法类游戏表现优秀。",
        provider = "DeepSeek",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "16B(MoE)",
        fileSizeMb = 9560,
        minRamMb = 12288,
        url = "https://huggingface.co/deepseek-ai/DeepSeek-Coder-V2-Lite-Instruct-GGUF/resolve/main/DeepSeek-Coder-V2-Lite-Instruct-Q4_K_M.gguf",
        mirrorUrl = "https://modelscope.cn/models/deepseek-ai/DeepSeek-Coder-V2-Lite-Instruct-GGUF/resolve/master/DeepSeek-Coder-V2-Lite-Instruct-Q4_K_M.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT,
            ModelCapability.REASONING
        ),
        codeGenerationScore = 10,
        gameDevScore = 9,
        phoneCompatibilityScore = 5,
        marketRating = 4.8f,
        suitableFor = listOf("算法类游戏", "复杂代码", "代码审查", "大内存设备"),
        rank = 5
    )

    /**
     * 06. 通用均衡 - Llama 3.2 3B Instruct
     * Meta 出品，通用能力强，代码专项稍弱于 Qwen-Coder。
     */
    val LLAMA_3_2_3B = ModelInfo(
        id = "llama-3.2-3b-instruct-q4-k-m",
        name = "Llama-3.2-3B-Instruct-Q4_K_M",
        displayName = "Llama 3.2 3B",
        description = "Meta官方通用模型，多语言与指令跟随能力强，代码能力次于Qwen-Coder，但生态成熟、社区资源多。",
        provider = "Meta",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "3B",
        fileSizeMb = 2020,
        minRamMb = 6144,
        url = "https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
        mirrorUrl = "https://modelscope.cn/models/llama-community/Llama-3.2-3B-Instruct-GGUF/resolve/master/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.CHAT,
            ModelCapability.REASONING
        ),
        codeGenerationScore = 7,
        gameDevScore = 6,
        phoneCompatibilityScore = 8,
        marketRating = 4.6f,
        suitableFor = listOf("通用对话", "多语言", "轻量代码", "社区资源丰富"),
        rank = 6
    )

    /**
     * 07. 微软小钢炮 - Phi-3.5-mini Instruct
     * 3.8B 参数，微软出品，推理与代码能力均衡，体积小。
     */
    val PHI_3_5_MINI = ModelInfo(
        id = "phi-3.5-mini-instruct-q4-k-m",
        name = "Phi-3.5-mini-Instruct-Q4_K_M",
        displayName = "Phi-3.5 mini",
        description = "微软Phi系列小型模型，3.8B参数，推理与代码能力均衡，体积小、速度快，适合中端手机。",
        provider = "Microsoft",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "3.8B",
        fileSizeMb = 2360,
        minRamMb = 6144,
        url = "https://huggingface.co/bartowski/Phi-3.5-mini-instruct-GGUF/resolve/main/Phi-3.5-mini-instruct-Q4_K_M.gguf",
        mirrorUrl = "https://modelscope.cn/models/llm-research/Phi-3.5-mini-instruct-GGUF/resolve/master/Phi-3.5-mini-instruct-Q4_K_M.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT,
            ModelCapability.REASONING
        ),
        codeGenerationScore = 8,
        gameDevScore = 7,
        phoneCompatibilityScore = 8,
        marketRating = 4.6f,
        suitableFor = listOf("中端手机", "均衡性能", "快速原型", "教育学习"),
        rank = 7
    )

    /**
     * 08. 谷歌轻量 - Gemma 2 2B Instruct
     * Google 出品，2B 参数，体积小巧，适合低端设备。
     */
    val GEMMA_2_2B = ModelInfo(
        id = "gemma-2-2b-instruct-q4-k-m",
        name = "Gemma-2-2B-Instruct-Q4_K_M",
        displayName = "Gemma 2 2B",
        description = "Google Gemma 2系列轻量模型，2B参数，安全对齐好，体积小，适合低端设备和基础游戏脚本生成。",
        provider = "Google",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "2B",
        fileSizeMb = 1580,
        minRamMb = 4096,
        url = "https://huggingface.co/bartowski/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-Q4_K_M.gguf",
        mirrorUrl = "https://modelscope.cn/models/llm-research/gemma-2-2b-it-GGUF/resolve/master/gemma-2-2b-it-Q4_K_M.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.CHAT,
            ModelCapability.REASONING
        ),
        codeGenerationScore = 6,
        gameDevScore = 6,
        phoneCompatibilityScore = 9,
        marketRating = 4.4f,
        suitableFor = listOf("低端设备", "基础脚本", "安全对话", "快速体验"),
        rank = 8
    )

    /**
     * 09. 专注代码 - Stable Code Instruct 3B
     * Stability AI 代码专用小模型，补全与短脚本能力强。
     */
    val STABLE_CODE_3B = ModelInfo(
        id = "stable-code-instruct-3b-q4-k-m",
        name = "Stable-Code-Instruct-3B-Q4_K_M",
        displayName = "Stable Code 3B",
        description = "Stability AI代码专用模型，3B参数，专注代码补全与短脚本生成，对H5小游戏和片段代码友好。",
        provider = "Stability AI",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "3B",
        fileSizeMb = 1780,
        minRamMb = 6144,
        url = "https://huggingface.co/stabilityai/stable-code-instruct-3b-GGUF/resolve/main/stable-code-instruct-3b-Q4_K_M.gguf",
        mirrorUrl = "https://modelscope.cn/models/stabilityai/stable-code-instruct-3b-GGUF/resolve/master/stable-code-instruct-3b-Q4_K_M.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.GAME_SCRIPT
        ),
        codeGenerationScore = 7,
        gameDevScore = 7,
        phoneCompatibilityScore = 8,
        marketRating = 4.3f,
        suitableFor = listOf("代码补全", "短脚本", "H5小游戏", "片段生成"),
        rank = 9
    )

    /**
     * 10. 国产全能 - InternLM2.5 7B Chat
     * 书生·浦语系列，中文与长文本能力强，代码能力中等。
     */
    val INTERNLM2_5_7B = ModelInfo(
        id = "internlm2.5-7b-chat-q4-k-m",
        name = "InternLM2.5-7B-Chat-Q4_K_M",
        displayName = "InternLM2.5 7B",
        description = "上海人工智能实验室书生·浦语模型，7B参数，中文理解和长文本能力强，代码能力中等，适合中文用户。",
        provider = "Shanghai AI Lab",
        format = ModelFormat.GGUF,
        quantization = "Q4_K_M",
        paramSize = "7B",
        fileSizeMb = 4520,
        minRamMb = 8192,
        url = "https://huggingface.co/internlm/internlm2_5-7b-chat-GGUF/resolve/main/internlm2_5-7b-chat-q4_k_m.gguf",
        mirrorUrl = "https://modelscope.cn/models/internlm/internlm2_5-7b-chat-GGUF/resolve/master/internlm2_5-7b-chat-q4_k_m.gguf",
        recommended = false,
        capabilities = listOf(
            ModelCapability.CODE_GENERATION,
            ModelCapability.CHAT,
            ModelCapability.REASONING
        ),
        codeGenerationScore = 7,
        gameDevScore = 6,
        phoneCompatibilityScore = 6,
        marketRating = 4.5f,
        suitableFor = listOf("中文用户", "长文本", "通用对话", "中轻度代码"),
        rank = 10
    )

    val ALL_MODELS = listOf(
        QWEN_CODER_3B,
        QWEN_CODER_7B,
        QWEN_CODER_3B_ARM,
        QWEN_CODER_1_5B,
        DEEPSEEK_CODER_V2_LITE,
        LLAMA_3_2_3B,
        PHI_3_5_MINI,
        GEMMA_2_2B,
        STABLE_CODE_3B,
        INTERNLM2_5_7B
    )

    fun getRecommendedModel(): ModelInfo = QWEN_CODER_3B

    fun getModelForDevice(totalRamMb: Long): ModelInfo {
        return when {
            totalRamMb >= 8192 -> QWEN_CODER_7B
            totalRamMb >= 6144 -> QWEN_CODER_3B
            totalRamMb >= 4096 -> QWEN_CODER_1_5B
            else -> QWEN_CODER_1_5B
        }
    }

    fun getArmOptimizedModel(): ModelInfo = QWEN_CODER_3B_ARM
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
    val capabilities: List<ModelCapability> = emptyList(),
    val codeGenerationScore: Int = 0,
    val gameDevScore: Int = 0,
    val phoneCompatibilityScore: Int = 0,
    val marketRating: Float = 0f,
    val suitableFor: List<String> = emptyList(),
    val rank: Int = 0
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
