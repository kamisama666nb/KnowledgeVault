package com.aozora.knowledgevault.ui.rss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aozora.knowledgevault.data.database.RssFeedEntity
import com.aozora.knowledgevault.data.repository.FetchFeedResult
import com.aozora.knowledgevault.data.repository.RssFeedRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RssUiState(
    val feeds: List<RssFeedEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isFetching: Boolean = false,
    val fetchProgress: String? = null
)

class RssViewModel(
    private val rssFeedRepository: RssFeedRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RssUiState(isLoading = true))
    val uiState: StateFlow<RssUiState> = _uiState.asStateFlow()
    
    init {
        observeFeeds()
    }
    
    private fun observeFeeds() {
        viewModelScope.launch {
            rssFeedRepository.getAllFeeds()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { feeds ->
                    _uiState.update { it.copy(feeds = feeds, isLoading = false) }
                }
        }
    }
    
    fun addFeed(url: String, category: String = "未分类") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = rssFeedRepository.addFeed(url, category)
            
            result.onSuccess {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = "RSS订阅添加成功"
                    )
                }
            }.onFailure { e ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "添加失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun deleteFeed(feedId: Long) {
        viewModelScope.launch {
            try {
                rssFeedRepository.deleteFeed(feedId)
                _uiState.update { it.copy(successMessage = "已删除订阅") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun toggleFeedActive(feed: RssFeedEntity) {
        viewModelScope.launch {
            try {
                rssFeedRepository.updateFeed(feed.copy(isActive = !feed.isActive))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun fetchFeed(feedId: Long) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isFetching = true,
                    fetchProgress = "正在抓取..."
                )
            }
            
            val result = rssFeedRepository.fetchFeed(feedId, saveToKnowledge = true)
            
            when (result) {
                is FetchFeedResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isFetching = false,
                            fetchProgress = null,
                            successMessage = "成功抓取 ${result.articleCount} 篇文章，保存了 ${result.savedCount} 篇到知识库"
                        )
                    }
                }
                is FetchFeedResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isFetching = false,
                            fetchProgress = null,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
    
    fun fetchAllFeeds() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isFetching = true,
                    fetchProgress = "正在抓取所有订阅..."
                )
            }
            
            val feeds = _uiState.value.feeds.filter { it.isActive }
            var successCount = 0
            var failCount = 0
            
            feeds.forEach { feed ->
                _uiState.update { 
                    it.copy(fetchProgress = "抓取: ${feed.title}")
                }
                
                val result = rssFeedRepository.fetchFeed(feed.id)
                when (result) {
                    is FetchFeedResult.Success -> successCount++
                    is FetchFeedResult.Error -> failCount++
                }
            }
            
            _uiState.update { 
                it.copy(
                    isFetching = false,
                    fetchProgress = null,
                    successMessage = "完成! 成功: $successCount, 失败: $failCount"
                )
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
