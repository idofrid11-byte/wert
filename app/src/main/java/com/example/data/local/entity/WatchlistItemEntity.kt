package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MediaItem
import com.example.data.model.WatchlistItem

/**
 * Room database entity representing a movie or series in the user's local watchlist.
 */
@Entity(tableName = "watchlist_items")
data class WatchlistItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "media_id")
    val mediaId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "type")
    val type: String, // "movie", "series", "anime"

    @ColumnInfo(name = "poster_url")
    val posterUrl: String?,

    @ColumnInfo(name = "backdrop_url")
    val backdropUrl: String? = null,

    @ColumnInfo(name = "year")
    val year: String,

    @ColumnInfo(name = "rating")
    val rating: String,

    @ColumnInfo(name = "overview")
    val overview: String = "",

    @ColumnInfo(name = "genres")
    val genres: String = "", // Comma-separated list of genres (e.g. "Action, Sci-Fi")

    @ColumnInfo(name = "service_provider")
    val serviceProvider: String? = null, // e.g. "Netflix", "HBO Max", "Disney+"

    @ColumnInfo(name = "progress_percent")
    val progressPercent: Float = 0f,

    @ColumnInfo(name = "last_watched_position_ms")
    val lastWatchedPositionMs: Long = 0L,

    @ColumnInfo(name = "total_duration_ms")
    val totalDurationMs: Long = 0L,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Maps entity to legacy UI model [WatchlistItem].
 */
fun WatchlistItemEntity.toWatchlistItem(): WatchlistItem {
    return WatchlistItem(
        mediaId = mediaId,
        title = title,
        poster = posterUrl,
        type = type,
        year = year,
        rating = rating,
        progressPercent = progressPercent,
        lastWatchedPositionMs = lastWatchedPositionMs,
        totalDurationMs = totalDurationMs,
        updatedAt = updatedAt
    )
}

/**
 * Maps entity to full domain [MediaItem].
 */
fun WatchlistItemEntity.toMediaItem(): MediaItem {
    return MediaItem(
        id = mediaId,
        title = title,
        type = type,
        poster = posterUrl,
        backdrop = backdropUrl,
        year = year,
        rating = rating,
        description = overview,
        genres = if (genres.isBlank()) emptyList() else genres.split(",").map { it.trim() },
        serviceProvider = serviceProvider
    )
}

/**
 * Maps domain [MediaItem] to Room [WatchlistItemEntity].
 */
fun MediaItem.toWatchlistEntity(
    isFavorite: Boolean = false,
    progressPercent: Float = 0f,
    lastWatchedPositionMs: Long = 0L,
    totalDurationMs: Long = 0L
): WatchlistItemEntity {
    return WatchlistItemEntity(
        mediaId = id,
        title = title,
        type = type,
        posterUrl = poster,
        backdropUrl = backdrop,
        year = year,
        rating = rating,
        overview = description,
        genres = genres.joinToString(", "),
        serviceProvider = serviceProvider,
        progressPercent = progressPercent,
        lastWatchedPositionMs = lastWatchedPositionMs,
        totalDurationMs = totalDurationMs,
        isFavorite = isFavorite,
        addedAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Maps [WatchlistItem] to [WatchlistItemEntity].
 */
fun WatchlistItem.toEntity(
    backdropUrl: String? = null,
    overview: String = "",
    genres: String = "",
    serviceProvider: String? = null,
    isFavorite: Boolean = false
): WatchlistItemEntity {
    return WatchlistItemEntity(
        mediaId = mediaId,
        title = title,
        type = type,
        posterUrl = poster,
        backdropUrl = backdropUrl,
        year = year,
        rating = rating,
        overview = overview,
        genres = genres,
        serviceProvider = serviceProvider,
        progressPercent = progressPercent,
        lastWatchedPositionMs = lastWatchedPositionMs,
        totalDurationMs = totalDurationMs,
        isFavorite = isFavorite,
        addedAt = updatedAt,
        updatedAt = updatedAt
    )
}
