package com.aozora.knowledgevault.data.ai

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * OpenAI兼容的API Provider
 * 支持OpenAI、Deepseek、Qwen等使用OpenAI格式的API
 */
class OpenAICompatibleProvider(
    private val config: AIConfig
) : AIProvider {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    private val baseUrl = config.baseUrl ?: "https://api.openai.com/v1"
    private val defaultModel = config.model ?: "gpt-3.5-turbo"
    
    override suspend fun chat(
        messages: List<Message>,
        systemPrompt: String?,
        temperature: Float
    ): String = withContext(Dispatchers.IO) {
        val allMessages = mutableListOf<Message>()
        
        if (systemPrompt != null) {
            allMessages.add(Message("system", systemPrompt))
        }
        allMessages.addAll(messages)
        
        val requestBody = ChatCompletionRequest(
            model = defaultModel,
            messages = allMessages.map { 
                ChatMessage(role = it.role, content = it.content) 
            },
            temperature = temperature
        )
        
        val request = Request.Builder()
            .url("$baseUrl/chat/completions")
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .addHeader("Content-Type", "application/json")
            .post(gson.toJson(requestBody).toRequestBody("application/json".toMediaType()))
            .build()
        
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response")
        
        if (!response.isSuccessful) {
            throw Exception("API Error: ${response.code} - $responseBody")
        }
        
        val chatResponse = gson.fromJson(responseBody, ChatCompletionResponse::class.java)
        chatResponse.choices.firstOrNull()?.message?.content 
            ?: throw Exception("No response from AI")
    }
    
    override suspend fun summarize(text: String, maxLength: Int): String {
        val prompt = """
            请为以下文本生成一个简洁的摘要，不超过${maxLength}字：
            
            $text
            
            摘要：
        """.trimIndent()
        
        return chat(listOf(Message("user", prompt)))
    }
    
    override suspend fun extractTags(text: String, maxTags: Int): List<String> {
        val prompt = """
            请为以下文本提取最多${maxTags}个关键词标签。
            只返回标签，用逗号分隔，不要有其他解释。
            
            文本：
            $text
            
            标签：
        """.trimIndent()
        
        val response = chat(listOf(Message("user", prompt)))
        return response.split(",", "，")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(maxTags)
    }
    
    override suspend fun answerQuestion(context: String, question: String): String {
        val systemPrompt = """
            你是一个知识库助手。基于提供的文档内容回答用户的问题。
            如果文档中没有相关信息，请明确告知用户。
        """.trimIndent()
        
        val userPrompt = """
            文档内容：
            $context
            
            问题：$question
            
            请基于上述文档回答问题。
        """.trimIndent()
        
        return chat(
            messages = listOf(Message("user", userPrompt)),
            systemPrompt = systemPrompt
        )
    }
}

// API数据类
private data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Float = 0.7f
)

private data class ChatMessage(
    val role: String,
    val content: String
)

private data class ChatCompletionResponse(
    val choices: List<Choice>
)

private data class Choice(
    val message: ChatMessage,
    val index: Int,
    @SerializedName("finish_reason")
    val finishReason: String
)
