package com.aozora.knowledgevault.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String,
    val content: String,
    val summary: String? = null,
    
    @TypeConverters(TagListConverter::class)
    val tags: List<String> = emptyList(),
    
    val source: String, // "imported", "rss", "manual"
    val sourceUrl: String? = null,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    val isStarred: Boolean = false,
    val isArchived: Boolean = false
)
