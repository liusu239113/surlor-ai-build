# 🎮 Surlor AI — 手机上的 AI 游戏开发工作室

> 基于 Operit AI 开源项目改造，专为手机端打造的 AI 驱动游戏开发工具。
> 核心能力：**AI对话生成游戏 → 实时预览 → 一键打包APK**
> 软件名称：**Surlor AI**

---

## ✨ 功能特色

### 🎯 AI 对话生成游戏
- 用自然语言描述你想做的游戏
- AI 理解需求并生成完整的 H5 游戏代码
- 内置最佳手机端模型 **Llama 3.2 3B (Q4_K_M)**
- 支持迭代修改：继续对话即可调整游戏逻辑、画面、难度

### 🔄 实时预览（无需构建）
- 生成即预览，无编译等待
- 基于 WebView 的 H5 游戏引擎
- 触摸/鼠标双输入支持
- 自动适配手机屏幕 (375x667 ~ 414x896)

### 📦 一键打包 APK
- H5 游戏 → 独立 Android APK
- 可自定义：应用名、包名、图标、版本号
- 支持全屏模式、屏幕方向设置
- 自动签名，即装即用

### 📐 12+ 游戏模板
- 🦖 小恐龙快跑、🐦 Flappy Bird、🐍 贪吃蛇、🧱 俄罗斯方块
- 👆 点击放置、🏓 乒乓球、🚀 太空射击、🏃 平台跳跃
- 🧩 消除游戏、🎯 射击游戏 ...
- 所有模板开箱即玩，可直接修改

### 🧠 内置最佳 AI 模型
| 模型 | 大小 | 内存需求 | 适用场景 |
|------|------|---------|---------|
| **Llama 3.2 3B Q4** 🏆 | ~2GB | 8GB+ | 全能代码生成+聊天，**推荐** |
| Qwen3 1.7B Q4 | ~1.1GB | 6GB+ | 推理+中文，中等配置 |
| Granite 4.0 H 1B Q4 | ~780MB | 4GB+ | 轻量代码专用，低端手机 |

### ✏️ 内置代码编辑器
- 语法高亮 (JS/Kotlin/Dart/HTML)
- 代码补全
- 实时修改即时生效

---

## 🔧 技术架构

```
Operit GameKit
├── gametool/              # 游戏开发核心模块 (新增)
│   ├── engine/            # 游戏生成引擎
│   │   └── GameGenerator.kt    # AI 对话游戏生成
│   ├── preview/           # 实时预览系统
│   │   └── GamePreviewEngine.kt # WebView 预览引擎
│   ├── packager/          # APK 打包系统
│   │   └── GamePackager.kt     # H5→APK 打包器
│   ├── templates/         # 游戏模板管理
│   │   └── GameTemplateManager.kt # 12+ 模板
│   ├── models/            # 数据模型
│   │   ├── GameProject.kt      # 项目模型
│   │   └── GameModelConfig.kt  # AI 模型配置
│   └── ui/                # 界面
│       ├── GameKitMainScreen.kt    # 主屏幕
│       ├── GameEditorScreen.kt     # 编辑器+预览+对话
│       └── GamePackagerScreen.kt   # APK 打包界面
├── app/                   # 原有 App 主模块
│   └── subpack/           # APK 逆向工程系统
├── quickjs/               # JavaScript 引擎
├── llama/                 # llama.cpp GGUF 推理
├── mnn/                   # MNN 推理引擎
├── dragonbones/           # 2D 骨骼动画
├── terminal/              # 终端模拟器
└── examples/              # 示例脚本
    ├── dino_runner/       # 小恐龙游戏示例
    └── apktool/           # APK 工具示例
```

---

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/AAswordman/Operit.git operit-gamekit
cd operit-gamekit
```

### 2. 下载 AI 模型
在 App 内进入 **设置 > 模型管理 > 下载推荐模型**
- 推荐：**Llama 3.2 3B Q4_K_M** (~2GB)
- 轻量：**Granite 4.0 H 1B** (~780MB)

### 3. 创建第一个游戏
1. 打开 App → 进入 **GameKit 开发工作室**
2. 点击 **"🎮 用 AI 对话生成游戏"**
3. 输入描述：*"做一个 Flappy Bird 风格的游戏，小鸟上下飞，穿过管道计分"*
4. 等待 AI 生成 → 预览区立刻显示可玩的游戏
5. 继续对话修改：*"把小鸟换成飞机"* 或 *"加快管道速度"*

### 4. 打包 APK
1. 游戏满意后，点击顶部打包按钮 📦
2. 填写应用名、包名
3. 点击 **"生成 APK"**
4. 得到的 APK 可直接分享或安装到任意 Android 设备

---

## 🎮 支持的开发模式

### 模式一：H5 游戏（推荐，即写即预览）
```
HTML + CSS + Canvas 2D JavaScript
→ AI 生成完整单文件 HTML
→ WebView 实时渲染
→ 可打包为独立 APK
```

### 模式二：Compose 原生（需构建）
```
Kotlin + Jetpack Compose
→ AI 生成 Kotlin 源码
→ 在 Compose 预览中显示
→ 需编译构建 APK
```

### 模式三：Godot（预留）
```
GDScript
→ 生成脚本和场景定义
→ 需要 Godot 引擎运行
```

---

## 📱 最低要求

- **Android 8.0+ (API 26)**
- **推荐 8GB+ RAM**（4GB 可用轻量模型）
- **存储空间**: 至少 2GB（含模型下载）

---

## 🔄 与 Operit 的关系

本项目是 [Operit AI](https://github.com/AAswordman/Operit) 的一个 fork/改造版，保留了 Operit 的所有核心能力（本地AI推理、MCP协议、终端、文件管理等），在此基础上专门为游戏开发场景进行了增强。

**保留的原生能力**：
- ✅ 本地 AI 模型推理 (llama.cpp + MNN)
- ✅ QuickJS 脚本引擎
- ✅ Compose DSL 动态 UI
- ✅ APK 逆向/重打包
- ✅ 终端环境 (Ubuntu 24)
- ✅ 工具包/插件系统

**新增的游戏开发能力**：
- ✅ AI 对话游戏生成引擎
- ✅ 实时 H5 游戏预览
- ✅ 一键 APK 打包
- ✅ 12+ 游戏模板
- ✅ 游戏专用模型配置
- ✅ 可视化 UI 编辑器

---

## 📄 许可证

基于 Operit AI 开源项目 (AGPL-3.0)，衍生代码同样遵循 AGPL-3.0。
