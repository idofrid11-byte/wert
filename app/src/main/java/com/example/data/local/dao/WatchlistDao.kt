package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.WatchlistItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing local watchlist entries of movies and series.
 */
@Dao
interface WatchlistDao {

    /**
     * Observes all items in the watchlist ordered by most recently updated.
     */
    @Query("SELECT * FROM watchlist_items ORDER BY updated_at DESC")
    fun getAllWatchlist(): Flow<List<WatchlistItemEntity>>

    /**
     * Observes items in the watchlist filtered by media type ("movie", "series", "anime").
     */
    @Query("SELECT * FROM watchlist_items WHERE type = :type ORDER BY updated_at DESC")
    fun getWatchlistByType(type: String): Flow<List<WatchlistItemEntity>>

    /**
     * Observes favorite items in the user's watchlist.
     */
    @Query("SELECT * FROM watchlist_items WHERE is_favorite = 1 ORDER BY updated_at DESC")
    fun getFavorites(): Flow<List<WatchlistItemEntity>>

    /**
     * Observes items in progress ("Continue Watching") with playback position greater than 0.
     */
    @Query("SELECT * FROM watchlist_items WHERE last_watched_position_ms > 0 AND progress_percent < 0.95 ORDER BY updated_at DESC")
    fun getContinueWatching(): Flow<List<WatchlistItemEntity>>

    /**
     * Observes completed items (progress >= 95%).
     */
    @Query("SELECT * FROM watchlist_items WHERE progress_percent >= 0.95 ORDER BY updated_at DESC")
    fun getCompletedItems(): Flow<List<WatchlistItemEntity>>

    /**
     * Observes a specific watchlist item by its unique media ID.
     */
    @Query("SELECT * FROM watchlist_items WHERE media_id = :mediaId LIMIT 1")
    fun getWatchlistItem(mediaId: String): Flow<WatchlistItemEntity?>

    /**
     * Synchronously fetches a specific watchlist item by media ID.
     */
    @Query("SELECT * FROM watchlist_items WHERE media_id = :mediaId LIMIT 1")
    suspend fun getWatchlistItemSync(mediaId: String): WatchlistItemEntity?

    /**
     * Observes whether a specific media item is present in the watchlist.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_items WHERE media_id = :mediaId)")
    fun observeIsWatchlisted(mediaId: String): Flow<Boolean>

    /**
     * Checks if a specific media item is present in the watchlist.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_items WHERE media_id = :mediaId)")
    suspend fun isWatchlisted(mediaId: String): Boolean

    /**
     * Returns the total count of items in the watchlist.
     */
    @Query("SELECT COUNT(*) FROM watchlist_items")
    fun getWatchlistCount(): Flow<Int>

    /**
     * Inserts or replaces an item in the watchlist.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: WatchlistItemEntity): Long

    /**
     * Bulk inserts or replaces a list of items (e.g. for initial seeding or imports).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WatchlistItemEntity>)

    /**
     * Updates an existing watchlist entity.
     */
    @Update
    suspend fun update(item: WatchlistItemEntity)

    /**
     * Updates the playback progress for a specific media item.
     */
    @Query("""
        UPDATE watchlist_items 
        SET last_watched_position_ms = :positionMs, 
            total_duration_ms = :durationMs, 
            progress_percent = :progressPercent, 
            updated_at = :updatedAt 
        WHERE media_id = :mediaId
    """)
    suspend fun updateProgress(
        mediaId: String,
        positionMs: Long,
        durationMs: Long,
        progressPercent: Float,
        updatedAt: Long = System.currentTimeMillis()
    )

    /**
     * Updates favorite status for a given media item.
     */
    @Query("UPDATE watchlist_items SET is_favorite = :isFavorite, updated_at = :updatedAt WHERE media_id = :mediaId")
    suspend fun updateFavorite(
        mediaId: String,
        isFavorite: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )

    /**
     * Deletes a specific item entity from the watchlist.
     */
    @Delete
    suspend fun delete(item: WatchlistItemEntity)

    /**
     * Deletes an item from the watchlist by its media ID.
     */
    @Query("DELETE FROM watchlist_items WHERE media_id = :mediaId")
    suspend fun deleteByMediaId(mediaId: String): Int

    /**
     * Deletes all items from the local watchlist.
     */
    @Query("DELETE FROM watchlist_items")
    suspend fun clearWatchlist()
}
