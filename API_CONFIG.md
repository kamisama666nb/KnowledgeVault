# AI API 配置示例

## OpenAI

```
API Key: sk-proj-xxxxxxxxxxxxxxxxxxxx
Base URL: https://api.openai.com/v1
Model: gpt-3.5-turbo
```

或使用GPT-4:
```
Model: gpt-4
```

## Deepseek

```
API Key: sk-xxxxxxxxxxxxxxxxxxxx
Base URL: https://api.deepseek.com/v1
Model: deepseek-chat
```

## Qwen (通义千问)

```
API Key: sk-xxxxxxxxxxxxxxxxxxxx
Base URL: https://dashscope.aliyuncs.com/compatible-mode/v1
Model: qwen-turbo
```

或使用更强的模型:
```
Model: qwen-plus
Model: qwen-max
```

## 自定义API

只要兼容OpenAI的接口格式，都可以使用:

```
API Key: 你的密钥
Base URL: https://your-api.com/v1
Model: 你的模型名称
```

## 注意事项

1. **API Key安全**: 
   - API Key存储在本地，不会上传
   - 但请妥善保管，不要泄露

2. **网络要求**:
   - 需要能够访问对应的API地址
   - 部分服务可能需要VPN

3. **费用**:
   - 使用AI功能会消耗API调用次数
   - 请留意各服务商的计费规则

4. **模型选择**:
   - gpt-3.5-turbo: 快速、便宜，适合摘要和标签
   - gpt-4: 更智能，但更贵
   - deepseek-chat: 性价比高
   - qwen系列: 国内访问快，中文理解好

## 测试配置

保存配置后，可以:
1. 添加一个新文档，看AI是否自动生成摘要
2. 在文档详情页使用"向AI提问"功能
3. 如果失败，检查:
   - API Key是否正确
   - Base URL是否可访问
   - 模型名称是否正确
