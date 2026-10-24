package com.example.data.model

data class MediaItem(
    val id: String,
    val title: String,
    val type: String, // "movie", "series", "anime"
    val poster: String?,
    val backdrop: String?,
    val year: String,
    val rating: String,
    val description: String,
    val genres: List<String> = emptyList(),
    val cast: List<String> = emptyList(),
    val director: List<String> = emptyList(),
    val runtime: String = "",
    val trailerUrl: String? = null,
    val episodes: List<Episode> = emptyList()
)

data class Episode(
    val id: String,
    val season: Int,
    val number: Int,
    val title: String,
    val overview: String,
    val thumbnail: String? = null
)

data class MediaStream(
    val id: String,
    val name: String,
    val title: String,
    val quality: String, // "4K", "1080p", "720p", "HD"
    val streamUrl: String,
    val format: String = "MP4",
    val addonName: String,
    val seeders: Int? = null,
    val sizeText: String = ""
)

data class Addon(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val iconUrl: String,
    val manifestUrl: String,
    val types: List<String>,
    val isInstalled: Boolean = true,
    val isOfficial: Boolean = false,
    val author: String = "Community"
)

data class WatchlistItem(
    val mediaId: String,
    val title: String,
    val poster: String?,
    val type: String,
    val year: String,
    val rating: String,
    val progressPercent: Float = 0f,
    val lastWatchedPositionMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis()
)

data class CatalogCategory(
    val id: String,
    val name: String,
    val items: List<MediaItem>
)

data class AudioTrack(
    val id: String,
    val name: String,
    val language: String,
    val channels: String = "5.1",
    val codec: String = "Dolby Digital"
)

data class SubtitleTrack(
    val id: String,
    val name: String,
    val language: String,
    val format: String = "SRT",
    val provider: String = "OpenSubtitles"
)

data class AudioPlaybackSettings(
    val selectedTrackId: String = "en_orig",
    val audioBoostLevel: Int = 0, // 0 = 100% normal, 1 = +50%, 2 = +100%, 3 = Dialogue Boost (Night Mode)
    val audioDelayMs: Int = 0, // -500ms to +500ms
    val playbackSpeed: Float = 1.0f,
    val surroundSoundEnabled: Boolean = true,
    val speechClarity: Boolean = true
)

data class SubtitlePlaybackSettings(
    val selectedTrackId: String = "en_sub", // "none", "en_sub", "es_sub", "fr_sub", etc.
    val secondaryTrackId: String = "none",
    val subtitleDelayMs: Int = 0, // -2000ms to +2000ms
    val textSizeSp: Int = 18,
    val textColorHex: Long = 0xFFFFFFFF,
    val backgroundStyle: String = "box", // "box", "shadow", "none", "solid"
    val verticalPosition: String = "bottom", // "bottom", "top"
    val autoSelectSubtitles: Boolean = true
)
