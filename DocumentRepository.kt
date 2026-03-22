package com.aozora.knowledgevault.data.repository

import com.aozora.knowledgevault.data.ai.AIProvider
import com.aozora.knowledgevault.data.ai.Message
import com.aozora.knowledgevault.data.database.DocumentDao
import com.aozora.knowledgevault.data.database.DocumentEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文档仓库
 * 处理文档的CRUD和AI增强功能
 */
class DocumentRepository(
    private val documentDao: DocumentDao,
    private val aiProvider: AIProvider?
) {
    
    fun getAllDocuments(): Flow<List<DocumentEntity>> = documentDao.getAllDocuments()
    
    fun getStarredDocuments(): Flow<List<DocumentEntity>> = documentDao.getStarredDocuments()
    
    fun getArchivedDocuments(): Flow<List<DocumentEntity>> = documentDao.getArchivedDocuments()
    
    fun searchDocuments(query: String): Flow<List<DocumentEntity>> = documentDao.searchDocuments(query)
    
    suspend fun getDocumentById(id: Long): DocumentEntity? = documentDao.getDocumentById(id)
    
    suspend fun getAllTags(): List<String> {
        val allTagLists = documentDao.getAllTags()
        return allTagLists.flatten().distinct().sorted()
    }
    
    /**
     * 添加文档(带AI增强)
     */
    suspend fun addDocument(
        title: String,
        content: String,
        source: String = "manual",
        sourceUrl: String? = null,
        autoGenerateSummary: Boolean = true,
        autoGenerateTags: Boolean = true
    ): Long {
        var summary: String? = null
        var tags: List<String> = emptyList()
        
        // 使用AI生成摘要和标签
        if (aiProvider != null) {
            try {
                if (autoGenerateSummary && content.length > 100) {
                    summary = aiProvider.summarize(content, maxLength = 200)
                }
                
                if (autoGenerateTags) {
                    tags = aiProvider.extractTags("$title\n$content", maxTags = 5)
                }
            } catch (e: Exception) {
                // AI失败不影响文档保存
                e.printStackTrace()
            }
        }
        
        val document = DocumentEntity(
            title = title,
            content = content,
            summary = summary,
            tags = tags,
            source = source,
            sourceUrl = sourceUrl
        )
        
        return documentDao.insertDocument(document)
    }
    
    /**
     * 更新文档
     */
    suspend fun updateDocument(document: DocumentEntity) {
        documentDao.updateDocument(document.copy(updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * 删除文档
     */
    suspend fun deleteDocument(id: Long) {
        documentDao.deleteDocumentById(id)
    }
    
    /**
     * 切换收藏状态
     */
    suspend fun toggleStar(id: Long) {
        val doc = documentDao.getDocumentById(id) ?: return
        documentDao.updateDocument(doc.copy(isStarred = !doc.isStarred))
    }
    
    /**
     * 归档/取消归档
     */
    suspend fun toggleArchive(id: Long) {
        val doc = documentDao.getDocumentById(id) ?: return
        documentDao.updateDocument(doc.copy(isArchived = !doc.isArchived))
    }
    
    /**
     * 重新生成摘要
     */
    suspend fun regenerateSummary(id: Long) {
        if (aiProvider == null) return
        
        val doc = documentDao.getDocumentById(id) ?: return
        try {
            val summary = aiProvider.summarize(doc.content, maxLength = 200)
            documentDao.updateDocument(doc.copy(summary = summary, updatedAt = System.currentTimeMillis()))
        } catch (e: Exception) {
            throw Exception("生成摘要失败: ${e.message}")
        }
    }
    
    /**
     * 基于文档回答问题
     */
    suspend fun askQuestion(documentId: Long, question: String): String {
        if (aiProvider == null) throw Exception("AI服务未配置")
        
        val doc = documentDao.getDocumentById(documentId) ?: throw Exception("文档不存在")
        
        return aiProvider.answerQuestion(
            context = "${doc.title}\n\n${doc.content}",
            question = question
        )
    }
    
    /**
     * 批量导入文档
     */
    suspend fun importDocuments(documents: List<DocumentEntity>) {
        documents.forEach { doc ->
            documentDao.insertDocument(doc)
        }
    }
}
