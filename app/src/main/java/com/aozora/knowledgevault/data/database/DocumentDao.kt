package com.aozora.knowledgevault.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>
    
    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): DocumentEntity?
    
    @Query("SELECT * FROM documents WHERE isStarred = 1 AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getStarredDocuments(): Flow<List<DocumentEntity>>
    
    @Query("SELECT * FROM documents WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedDocuments(): Flow<List<DocumentEntity>>
    
    @Query("""
        SELECT * FROM documents 
        WHERE (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        AND isArchived = 0
        ORDER BY updatedAt DESC
    """)
    fun searchDocuments(query: String): Flow<List<DocumentEntity>>
    
    @Query("SELECT * FROM documents")
    suspend fun getAllDocumentsForTags(): List<DocumentEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long
    
    @Update
    suspend fun updateDocument(document: DocumentEntity)
    
    @Delete
    suspend fun deleteDocument(document: DocumentEntity)
    
    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)
}
