# KnowledgeVault - 个人知识库助手

一个功能强大的Android个人知识管理应用，支持文档管理、AI增强和RSS订阅。

## 功能特性

### ✨ 核心功能

- **📚 知识库管理**
  - 支持文本、Markdown文档导入
  - 全文搜索和智能检索
  - 文档分类和标签管理
  - 收藏和归档功能

- **🤖 AI增强**
  - 自动生成文档摘要
  - 智能提取关键词标签
  - 基于文档的智能问答
  - 支持多种AI Provider(OpenAI、Deepseek、Qwen等)

- **📰 RSS订阅**
  - RSS Feed订阅管理
  - 自动抓取和更新
  - AI智能筛选和摘要
  - 一键保存到知识库

- **🎨 现代化UI**
  - Material Design 3设计
  - 支持深色模式
  - 流畅的Compose动画

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM + Repository Pattern
- **数据库**: Room
- **网络**: Retrofit + OkHttp
- **异步**: Kotlin Coroutines + Flow
- **依赖注入**: 手动依赖注入

## 项目结构

```
com.aozora.knowledgevault/
├── data/
│   ├── ai/              # AI Provider接口和实现
│   ├── database/        # Room数据库实体和DAO
│   ├── repository/      # 数据仓库层
│   └── rss/             # RSS解析服务
├── ui/
│   ├── home/            # 主页(文档列表)
│   ├── document/        # 文档详情
│   ├── rss/             # RSS管理
│   ├── settings/        # 设置
│   └── theme/           # Material主题
└── MainActivity.kt      # 主Activity和导航
```

## 快速开始

### 前置要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK API 26+

### 构建步骤

1. 克隆项目
```bash
git clone <repo-url>
cd KnowledgeVault
```

2. 在Android Studio中打开项目

3. 同步Gradle依赖

4. 运行到设备或模拟器

### 配置AI服务

1. 打开应用，进入"设置"页面
2. 填写你的AI API配置:
   - **API Key**: 你的API密钥
   - **Base URL**: API地址 (如 `https://api.openai.com/v1`)
   - **Model**: 模型名称 (如 `gpt-3.5-turbo`)

支持的AI服务商:
- OpenAI (GPT-3.5/4)
- Deepseek
- Qwen (通义千问)
- 任何兼容OpenAI格式的API

## 使用指南

### 添加文档

1. 点击主页右下角的"+"按钮
2. 输入标题和内容
3. 保存后AI会自动生成摘要和标签

### 订阅RSS

1. 进入"RSS订阅"页面
2. 点击"+"添加RSS源
3. 输入RSS地址和分类
4. 可以立即抓取或等待自动更新

### 智能问答

1. 打开任意文档详情
2. 点击顶部的"向AI提问"图标
3. 输入问题，AI会基于文档内容回答

## 开发计划

- [ ] 支持更多文档格式(PDF、EPUB)
- [ ] 向量搜索和语义检索
- [ ] 文档自动分类
- [ ] 批量导入功能
- [ ] 数据同步和备份
- [ ] WorkManager后台任务
- [ ] Widget桌面小部件

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request!

## 联系方式

- 作者: Aozora
- 项目主页: [GitHub](https://github.com/yourusername/KnowledgeVault)
