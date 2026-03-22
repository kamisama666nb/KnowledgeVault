package com.aozora.knowledgevault.data.ai

/**
 * AI Provider统一接口
 * 支持多种AI服务商(OpenAI, Claude, Qwen等)
 */
interface AIProvider {
    /**
     * 对话补全
     * @param messages 对话历史
     * @param systemPrompt 系统提示词
     * @return AI回复
     */
    suspend fun chat(
        messages: List<Message>,
        systemPrompt: String? = null,
        temperature: Float = 0.7f
    ): String
    
    /**
     * 生成摘要
     * @param text 要摘要的文本
     * @param maxLength 最大长度(字符数)
     * @return 摘要文本
     */
    suspend fun summarize(text: String, maxLength: Int = 200): String
    
    /**
     * 提取关键词/标签
     * @param text 要分析的文本
     * @param maxTags 最多返回多少个标签
     * @return 标签列表
     */
    suspend fun extractTags(text: String, maxTags: Int = 5): List<String>
    
    /**
     * 问答
     * @param context 上下文文档
     * @param question 用户问题
     * @return 回答
     */
    suspend fun answerQuestion(context: String, question: String): String
}

/**
 * 消息数据类
 */
data class Message(
    val role: String, // "user" or "assistant" or "system"
    val content: String
)

/**
 * AI配置
 */
data class AIConfig(
    val apiKey: String,
    val baseUrl: String? = null,
    val model: String? = null
)
