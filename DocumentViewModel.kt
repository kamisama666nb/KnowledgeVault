package com.aozora.knowledgevault.ui.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aozora.knowledgevault.data.database.DocumentEntity
import com.aozora.knowledgevault.data.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DocumentUiState(
    val document: DocumentEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val editTitle: String = "",
    val editContent: String = "",
    val aiAnswer: String? = null,
    val isAskingAI: Boolean = false
)

class DocumentViewModel(
    private val documentRepository: DocumentRepository,
    private val documentId: Long
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DocumentUiState(isLoading = true))
    val uiState: StateFlow<DocumentUiState> = _uiState.asStateFlow()
    
    init {
        loadDocument()
    }
    
    private fun loadDocument() {
        viewModelScope.launch {
            try {
                val doc = documentRepository.getDocumentById(documentId)
                _uiState.update {
                    it.copy(
                        document = doc,
                        isLoading = false,
                        editTitle = doc?.title ?: "",
                        editContent = doc?.content ?: ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
    
    fun startEditing() {
        val doc = _uiState.value.document ?: return
        _uiState.update {
            it.copy(
                isEditing = true,
                editTitle = doc.title,
                editContent = doc.content
            )
        }
    }
    
    fun cancelEditing() {
        _uiState.update { it.copy(isEditing = false, aiAnswer = null) }
    }
    
    fun updateEditTitle(title: String) {
        _uiState.update { it.copy(editTitle = title) }
    }
    
    fun updateEditContent(content: String) {
        _uiState.update { it.copy(editContent = content) }
    }
    
    fun saveDocument() {
        viewModelScope.launch {
            val doc = _uiState.value.document ?: return@launch
            
            try {
                val updated = doc.copy(
                    title = _uiState.value.editTitle,
                    content = _uiState.value.editContent,
                    updatedAt = System.currentTimeMillis()
                )
                
                documentRepository.updateDocument(updated)
                
                _uiState.update {
                    it.copy(
                        document = updated,
                        isEditing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun regenerateSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                documentRepository.regenerateSummary(documentId)
                loadDocument() // 重新加载文档
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
    
    fun askQuestion(question: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAskingAI = true, aiAnswer = null) }
            
            try {
                val answer = documentRepository.askQuestion(documentId, question)
                _uiState.update { 
                    it.copy(
                        aiAnswer = answer,
                        isAskingAI = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message,
                        isAskingAI = false
                    )
                }
            }
        }
    }
    
    fun toggleStar() {
        viewModelScope.launch {
            try {
                documentRepository.toggleStar(documentId)
                loadDocument()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
