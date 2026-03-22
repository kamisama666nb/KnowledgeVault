package com.aozora.knowledgevault.data.repository

import com.aozora.knowledgevault.data.ai.AIProvider
import com.aozora.knowledgevault.data.database.DocumentEntity
import com.aozora.knowledgevault.data.database.RssFeedDao
import com.aozora.knowledgevault.data.database.RssFeedEntity
import com.aozora.knowledgevault.data.rss.FeedResult
import com.aozora.knowledgevault.data.rss.RssService
import kotlinx.coroutines.flow.Flow

/**
 * RSS Feed仓库
 */
class RssFeedRepository(
    private val rssFeedDao: RssFeedDao,
    private val rssService: RssService,
    private val documentRepository: DocumentRepository,
    private val aiProvider: AIProvider?
) {
    
    fun getAllFeeds(): Flow<List<RssFeedEntity>> = rssFeedDao.getAllFeeds()
    
    fun getAllActiveFeeds(): Flow<List<RssFeedEntity>> = rssFeedDao.getAllActiveFeeds()
    
    suspend fun getFeedById(id: Long): RssFeedEntity? = rssFeedDao.getFeedById(id)
    
    /**
     * 添加RSS订阅
     */
    suspend fun addFeed(url: String, category: String = "未分类"): Result<Long> {
        return try {
            // 先尝试解析RSS，验证URL有效性
            val result = rssService.parseFeed(url)
            
            when (result) {
                is FeedResult.Success -> {
                    val feed = RssFeedEntity(
                        title = result.feedTitle,
                        url = url,
                        category = category
                    )
                    val id = rssFeedDao.insertFeed(feed)
                    Result.success(id)
                }
                is FeedResult.Error -> {
                    Result.failure(Exception(result.message))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 更新Feed
     */
    suspend fun updateFeed(feed: RssFeedEntity) {
        rssFeedDao.updateFeed(feed)
    }
    
    /**
     * 删除Feed
     */
    suspend fun deleteFeed(id: Long) {
        rssFeedDao.deleteFeedById(id)
    }
    
    /**
     * 抓取单个Feed的新内容
     */
    suspend fun fetchFeed(feedId: Long, saveToKnowledge: Boolean = true): FetchFeedResult {
        val feed = rssFeedDao.getFeedById(feedId) ?: return FetchFeedResult.Error("Feed不存在")
        
        return try {
            val result = rssService.parseFeed(feed.url)
            
            when (result) {
                is FeedResult.Success -> {
                    // 更新最后抓取时间
                    rssFeedDao.updateFeed(
                        feed.copy(
                            lastFetchedAt = System.currentTimeMillis(),
                            lastError = null
                        )
                    )
                    
                    // 保存文章到知识库
                    val savedCount = if (saveToKnowledge) {
                        saveArticlesToKnowledge(result.articles.take(10), feed.title)
                    } else {
                        0
                    }
                    
                    FetchFeedResult.Success(
                        feedTitle = result.feedTitle,
                        articleCount = result.articles.size,
                        savedCount = savedCount
                    )
                }
                is FeedResult.Error -> {
                    rssFeedDao.updateFeed(
                        feed.copy(lastError = result.message)
                    )
                    FetchFeedResult.Error(result.message)
                }
            }
        } catch (e: Exception) {
            rssFeedDao.updateFeed(
                feed.copy(lastError = e.message)
            )
            FetchFeedResult.Error(e.message ?: "未知错误")
        }
    }
    
    /**
     * 抓取所有活跃Feed
     */
    suspend fun fetchAllFeeds(): Map<Long, FetchFeedResult> {
        val feeds = rssFeedDao.getAllActiveFeeds()
        val results = mutableMapOf<Long, FetchFeedResult>()
        
        // 这里简单实现，实际应该用Flow或限制并发
        // TODO: 使用协程并发优化
        return results
    }
    
    /**
     * 将RSS文章保存到知识库
     */
    private suspend fun saveArticlesToKnowledge(
        articles: List<com.aozora.knowledgevault.data.rss.Article>,
        feedTitle: String
    ): Int {
        var savedCount = 0
        
        articles.forEach { article ->
            try {
                // 使用AI提炼摘要(如果内容太长)
                val summary = if (aiProvider != null && article.content.length > 500) {
                    try {
                        aiProvider.summarize(article.content, maxLength = 150)
                    } catch (e: Exception) {
                        article.description.take(150)
                    }
                } else {
                    article.description.take(150)
                }
                
                val document = DocumentEntity(
                    title = article.title,
                    content = article.content,
                    summary = summary,
                    tags = listOf(feedTitle, "RSS"),
                    source = "rss",
                    sourceUrl = article.link
                )
                
                documentRepository.addDocument(
                    title = document.title,
                    content = document.content,
                    source = "rss",
                    sourceUrl = article.link,
                    autoGenerateSummary = false, // 已经生成了
                    autoGenerateTags = true
                )
                
                savedCount++
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        return savedCount
    }
}

sealed class FetchFeedResult {
    data class Success(
        val feedTitle: String,
        val articleCount: Int,
        val savedCount: Int
    ) : FetchFeedResult()
    
    data class Error(val message: String) : FetchFeedResult()
}
