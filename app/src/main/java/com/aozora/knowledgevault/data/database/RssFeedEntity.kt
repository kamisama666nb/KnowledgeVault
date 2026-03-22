package com.aozora.knowledgevault.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rss_feeds")
data class RssFeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String,
    val url: String,
    val category: String = "未分类",
    
    val lastFetchedAt: Long? = null,
    val lastError: String? = null,
    
    val isActive: Boolean = true,
    val fetchInterval: Long = 3600000, // 1小时，毫秒
    
    val createdAt: Long = System.currentTimeMillis()
)
