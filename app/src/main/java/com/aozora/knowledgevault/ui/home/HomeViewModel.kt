package com.aozora.knowledgevault.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aozora.knowledgevault.data.database.DocumentEntity
import com.aozora.knowledgevault.data.repository.DocumentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val documents: List<DocumentEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val filterMode: FilterMode = FilterMode.ALL
)

enum class FilterMode {
    ALL, STARRED, ARCHIVED
}

class HomeViewModel(
    private val documentRepository: DocumentRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    private val _filterMode = MutableStateFlow(FilterMode.ALL)
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        observeDocuments()
    }
    
    private fun observeDocuments() {
        viewModelScope.launch {
            combine(
                _searchQuery,
                _filterMode
            ) { query, filter ->
                Pair(query, filter)
            }.flatMapLatest { (query, filter) ->
                when {
                    query.isNotBlank() -> documentRepository.searchDocuments(query)
                    filter == FilterMode.STARRED -> documentRepository.getStarredDocuments()
                    filter == FilterMode.ARCHIVED -> documentRepository.getArchivedDocuments()
                    else -> documentRepository.getAllDocuments()
                }
            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect { documents ->
                _uiState.update { 
                    it.copy(
                        documents = documents,
                        isLoading = false,
                        searchQuery = _searchQuery.value,
                        filterMode = _filterMode.value
                    )
                }
            }
        }
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    
    fun onFilterChanged(mode: FilterMode) {
        _filterMode.value = mode
    }
    
    fun toggleStar(documentId: Long) {
        viewModelScope.launch {
            try {
                documentRepository.toggleStar(documentId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun deleteDocument(documentId: Long) {
        viewModelScope.launch {
            try {
                documentRepository.deleteDocument(documentId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun toggleArchive(documentId: Long) {
        viewModelScope.launch {
            try {
                documentRepository.toggleArchive(documentId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
