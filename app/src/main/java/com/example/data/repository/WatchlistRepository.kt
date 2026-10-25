package com.example.data.repository

import com.example.data.local.dao.WatchlistDao
import com.example.data.local.entity.WatchlistItemEntity
import com.example.data.local.entity.toWatchlistEntity
import com.example.data.model.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Repository adhering to Clean Architecture that abstracts local Room database operations
 * for the user's movie and series watchlist.
 */
class WatchlistRepository(
    private val watchlistDao: WatchlistDao
) {
    /**
     * Flow emitting all watchlist entries ordered by update timestamp descending.
     */
    val allWatchlist: Flow<List<WatchlistItemEntity>> = watchlistDao.getAllWatchlist()

    /**
     * Flow emitting items currently in progress for the "Continue Watching" section.
     */
    val continueWatching: Flow<List<WatchlistItemEntity>> = watchlistDao.getContinueWatching()

    /**
     * Flow emitting items marked as favorite.
     */
    val favorites: Flow<List<WatchlistItemEntity>> = watchlistDao.getFavorites()

    /**
     * Observes items of a specific media type ("movie", "series", "anime").
     */
    fun getWatchlistByType(type: String): Flow<List<WatchlistItemEntity>> {
        return watchlistDao.getWatchlistByType(type)
    }

    /**
     * Observes whether a media item is currently in the local watchlist.
     */
    fun observeIsWatchlisted(mediaId: String): Flow<Boolean> {
        return watchlistDao.observeIsWatchlisted(mediaId)
    }

    /**
     * Returns true if the media item exists in the watchlist.
     */
    suspend fun isWatchlisted(mediaId: String): Boolean {
        return watchlistDao.isWatchlisted(mediaId)
    }

    /**
     * Toggles a media item in the watchlist. If present, removes it; if absent, inserts it.
     */
    suspend fun toggleWatchlist(item: MediaItem) {
        val existing = watchlistDao.getWatchlistItemSync(item.id)
        if (existing != null) {
            watchlistDao.deleteByMediaId(item.id)
        } else {
            watchlistDao.insertOrUpdate(item.toWatchlistEntity())
        }
    }

    /**
     * Inserts or updates an item in the watchlist.
     */
    suspend fun insertOrUpdate(item: WatchlistItemEntity): Long {
        return watchlistDao.insertOrUpdate(item)
    }

    /**
     * Updates the playback progress and calculate completion percentage.
     */
    suspend fun updateProgress(mediaId: String, currentPositionMs: Long, totalDurationMs: Long) {
        val percent = if (totalDurationMs > 0) {
            (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        val existing = watchlistDao.getWatchlistItemSync(mediaId)
        if (existing != null) {
            watchlistDao.updateProgress(
                mediaId = mediaId,
                positionMs = currentPositionMs,
                durationMs = totalDurationMs,
                progressPercent = percent,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    /**
     * Toggles the favorite flag on an item.
     */
    suspend fun toggleFavorite(mediaId: String) {
        val existing = watchlistDao.getWatchlistItemSync(mediaId) ?: return
        watchlistDao.updateFavorite(mediaId, !existing.isFavorite)
    }

    /**
     * Removes an item by media ID.
     */
    suspend fun removeByMediaId(mediaId: String): Int {
        return watchlistDao.deleteByMediaId(mediaId)
    }

    /**
     * Clears all watchlist records.
     */
    suspend fun clearWatchlist() {
        watchlistDao.clearWatchlist()
    }

    /**
     * Seeds initial curated items if the database is currently empty.
     */
    suspend fun seedInitialDataIfEmpty(initialItems: List<WatchlistItemEntity>) {
        val count = watchlistDao.getWatchlistCount().first()
        if (count == 0 && initialItems.isNotEmpty()) {
            watchlistDao.insertAll(initialItems)
        }
    }
}
