package com.aozora.knowledgevault.data.rss

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * RSS Feed解析器
 */
class RssService {
    private val parser = RssParser()
    
    /**
     * 获取RSS Feed内容
     */
    suspend fun fetchFeed(url: String): RssChannel = withContext(Dispatchers.IO) {
        parser.getRssChannel(url)
    }
    
    /**
     * 解析RSS为简化的数据结构
     */
    suspend fun parseFeed(url: String): FeedResult = withContext(Dispatchers.IO) {
        try {
            val channel = fetchFeed(url)
            val articles = channel.items.map { item ->
                Article(
                    title = item.title ?: "无标题",
                    link = item.link ?: "",
                    description = item.description ?: "",
                    content = item.content ?: item.description ?: "",
                    pubDate = item.pubDate ?: "",
                    author = item.author ?: channel.title ?: "未知"
                )
            }
            
            FeedResult.Success(
                feedTitle = channel.title ?: "未知Feed",
                feedDescription = channel.description ?: "",
                articles = articles
            )
        } catch (e: Exception) {
            FeedResult.Error(e.message ?: "解析失败")
        }
    }
}

/**
 * RSS文章数据
 */
data class Article(
    val title: String,
    val link: String,
    val description: String,
    val content: String,
    val pubDate: String,
    val author: String
)

/**
 * RSS解析结果
 */
sealed class FeedResult {
    data class Success(
        val feedTitle: String,
        val feedDescription: String,
        val articles: List<Article>
    ) : FeedResult()
    
    data class Error(val message: String) : FeedResult()
}
