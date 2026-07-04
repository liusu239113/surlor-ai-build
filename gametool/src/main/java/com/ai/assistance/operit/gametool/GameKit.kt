package com.ai.assistance.operit.gametool

import android.content.Context
import com.ai.assistance.operit.gametool.models.GameProject
import com.ai.assistance.operit.gametool.models.GameType
import com.ai.assistance.operit.gametool.models.GameModelConfig

/**
 * GameKit 主入口
 * 
 * 这个类作为 GameKit 模块的对外接口，
 * 供 app 模块的包管理系统和导航系统调用。
 */
object GameKit {

    /**
     * 模块 ID
     */
    const val MODULE_ID = "com.surlor.gamekit"
    const val MODULE_VERSION = "1.0.0"
    const val APP_NAME = "Surlor AI"

    /**
     * 初始化 GameKit 模块
     * 注册工具包、模型配置、导航路由等
     */
    fun initialize(context: Context) {
        // 1. 注册游戏开发工具包
        registerGameToolPackage()

        // 2. 预配置 AI 模型
        configureDefaultModel()

        // 3. 注册导航路由
        registerNavigationRoutes()

        // 4. 注册游戏模板
        registerTemplates()
    }

    /**
     * 注册游戏开发工具包
     * 通过 Operit 的 ToolPkg 系统注册
     */
    private fun registerGameToolPackage() {
        // 会根据 Operit 的 ToolPkgManager API 注册
        // 包括: 游戏生成工具、打包工具、预览工具等
    }

    /**
     * 配置默认 AI 模型
     * 设置游戏开发专用的系统提示词和模型参数
     */
    private fun configureDefaultModel() {
        val recommended = GameModelConfig.getRecommendedModel()
        // 配置模型下载地址和推理参数
        // - 模型: ${recommended.displayName}
        // - 格式: ${recommended.format}
        // - URL: ${recommended.url}
        // - 系统提示词: 设置为游戏开发专用
    }

    /**
     * 注册导航路由
     */
    private fun registerNavigationRoutes() {
        // 注册:
        // - gamekit:main -> 主屏幕
        // - gamekit:editor -> 游戏编辑器
        // - gamekit:packager -> APK打包器
    }

    /**
     * 注册游戏模板
     */
    private fun registerTemplates() {
        // 注册所有 GameTemplateManager 中的模板
    }

    /**
     * 获取 GameKit 版本信息
     */
    fun getVersionInfo(): String = "Surlor AI GameKit v$MODULE_VERSION"

    /**
     * 获取功能清单
     */
    fun getFeatureList(): List<String> = listOf(
        "🎮 AI 对话生成 H5 游戏",
        "🔄 实时预览 (无需构建)",
        "📦 一键打包 APK",
        "📐 多游戏模板",
        "🧠 内置本地 AI 模型",
        "✏️ 在线代码编辑器",
        "🎨 游戏 UI 调整",
        "📱 触摸 + 键盘支持"
    )
}

/**
 * GameKit 的 ToolPkg 注册脚本
 * 用于动态注册到 Operit 的包管理系统
 */
object GameKitRegistration {

    /**
     * 注册 GameKit 为可安装的工具包
     * 会被 Operit 的 JsEngine 调用
     */
    fun register() = """
registerToolPkg(function() {
    return {
        id: 'com.surlor.gamekit',
        name: 'GameKit 游戏开发工作室',
        description: 'Surlor AI 游戏开发套件 - AI驱动的手机端游戏开发工具，支持H5游戏生成、实时预览和APK打包',
        version: '${GameKit.MODULE_VERSION}',
        
        // 导航入口
        navigationEntries: [
            {
                id: 'gamekit_main',
                route: 'gamekit:main',
                label: 'GameKit',
                icon: 'SportsEsports',
                surface: 'main_sidebar_plugins'
            }
        ],
        
        // UI 路由
        uiRoutes: [
            {
                id: 'gamekit_main',
                route: 'gamekit:main',
                runtime: 'compose_dsl',
                screen: 'GameKitMainScreen'
            },
            {
                id: 'gamekit_editor',
                route: 'gamekit:editor',
                runtime: 'compose_dsl',
                screen: 'GameEditorScreen'
            },
            {
                id: 'gamekit_packager',
                route: 'gamekit:packager',
                runtime: 'compose_dsl',
                screen: 'GamePackagerScreen'
            }
        ],
        
        // 内置工具
        tools: [
            {
                name: 'generate_game',
                description: '根据描述生成游戏代码',
                parameters: [
                    {name:'description',type:'string',required:true,description:'游戏描述'},
                    {name:'type',type:'string',required:false,description:'游戏类型: h5/compose/godot'}
                ]
            },
            {
                name: 'preview_game',
                description: '实时预览游戏',
                parameters: [
                    {name:'code',type:'string',required:true,description:'游戏HTML代码'}
                ]
            },
            {
                name: 'package_apk',
                description: '打包游戏为APK',
                parameters: [
                    {name:'appName',type:'string',required:true,description:'应用名称'},
                    {name:'packageName',type:'string',required:true,description:'包名'}
                ]
            }
        ],

        // 工作流模板
        workflowTemplates: [
            {
                id: 'generate_h5_game',
                name: '生成 H5 游戏',
                description: '通过AI对话生成并预览H5游戏',
                steps: ['对话需求', '生成代码', '实时预览', '迭代修改', '打包APK']
            }
        ]
    };
});
""".trimIndent()
}
