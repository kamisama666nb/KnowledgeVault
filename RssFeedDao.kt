package com.aozora.knowledgevault.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RssFeedDao {
    @Query("SELECT * FROM rss_feeds WHERE isActive = 1 ORDER BY title ASC")
    fun getAllActiveFeeds(): Flow<List<RssFeedEntity>>
    
    @Query("SELECT * FROM rss_feeds ORDER BY title ASC")
    fun getAllFeeds(): Flow<List<RssFeedEntity>>
    
    @Query("SELECT * FROM rss_feeds WHERE id = :id")
    suspend fun getFeedById(id: Long): RssFeedEntity?
    
    @Query("SELECT * FROM rss_feeds WHERE url = :url LIMIT 1")
    suspend fun getFeedByUrl(url: String): RssFeedEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feed: RssFeedEntity): Long
    
    @Update
    suspend fun updateFeed(feed: RssFeedEntity)
    
    @Delete
    suspend fun deleteFeed(feed: RssFeedEntity)
    
    @Query("DELETE FROM rss_feeds WHERE id = :id")
    suspend fun deleteFeedById(id: Long)
}
