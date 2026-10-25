package com.example.data.model

data class UserAccount(
    val id: String,
    val email: String,
    val username: String,
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isPremium: Boolean = true,
    val deviceName: String = "Android Device"
)

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
    val episodes: List<Episode> = emptyList(),
    val serviceProvider: String? = null // "Netflix", "HBO Max", "Disney+", "Apple TV+", "Prime Video", "Crunchyroll"
)

data class StreamingService(
    val id: String,
    val name: String,
    val badgeShort: String,
    val brandColorHex: Long,
    val slogan: String
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
    val speechClarity: Boolean = true,
    val equalizerPreset: String = "Cinema", // "Flat", "Cinema", "Vocal Boost", "Night Mode", "Bass Punch", "Acoustic"
    val stereoWideningPercent: Int = 60, // 0 to 100%
    val dynamicRangeCompression: String = "Medium", // "Off", "Light", "Medium", "Heavy"
    val pitchCorrectionEnabled: Boolean = true,
    val preAmpGainDb: Int = 0, // -12dB to +12dB
    val outputRouting: String = "Auto (Speaker/Bluetooth)", // "Auto", "Phone Speaker", "Wired/USB-C", "Bluetooth Low-Latency", "Optical/HDMI Passthrough"
    val nightModeVolumeLeveling: Boolean = true, // Auto levels loud action & quiet dialogues
    val audioPassthrough: String = "Auto", // "Auto", "PCM Stereo", "Dolby Digital (AC3)", "Dolby Digital Plus (E-AC3)", "DTS-HD / Atmos Bitstream"
    val preferredHebrewAudio: Boolean = false, // Auto-prefer Hebrew dubbing when available
    val audioBalance: Float = 0f // -1.0f (Full Left) to +1.0f (Full Right)
)

data class SubtitlePlaybackSettings(
    val selectedTrackId: String = "he_sub", // default to Hebrew subtitles "he_sub", "en_sub", etc.
    val secondaryTrackId: String = "none",
    val subtitleDelayMs: Int = 0, // -2000ms to +2000ms
    val textSizeSp: Int = 18,
    val textColorHex: Long = 0xFFFFFFFF,
    val backgroundStyle: String = "box", // "box", "shadow", "none", "solid"
    val verticalPosition: String = "bottom", // "bottom", "top"
    val autoSelectSubtitles: Boolean = true,
    val strokeThickness: String = "medium", // "none", "thin", "medium", "heavy"
    val strokeColorHex: Long = 0xFF000000,
    val fontStyle: String = "sans_serif", // "sans_serif", "serif", "monospace", "condensed"
    val boxOpacityPercent: Int = 75, // 0 to 100%
    val bottomSafeInsetDp: Int = 24, // 0, 16, 24, 36, 48
    val forceUppercase: Boolean = false,
    val activeSubProvider: String = "OpenSubtitles v3", // "OpenSubtitles v3", "Subdl", "Addic7ed", "BSPlayer"
    val preferredHebrewSubtitles: Boolean = true, // Auto-prefer Hebrew subtitles
    val dualSubtitlesEnabled: Boolean = false, // Dual subtitles simultaneous display
    val subtitleEncoding: String = "UTF-8", // "UTF-8", "Windows-1255 (Hebrew)", "ISO-8859-8"
    val cjkHebrewRtlOptimization: Boolean = true, // RTL Hebrew alignment and punctuation fix
    val textShadowBlur: String = "Heavy" // "None", "Subtle", "Heavy", "Outline"
)

data class CastMember(
    val id: String,
    val name: String,
    val role: String,
    val avatarUrl: String? = null,
    val bio: String = ""
)

data class OfflineDownload(
    val id: String,
    val mediaId: String,
    val title: String,
    val poster: String?,
    val quality: String,
    val sizeText: String,
    val progressPercent: Float, // 0.0 to 1.0
    val isCompleted: Boolean,
    val downloadSpeed: String = "14.2 MB/s",
    val localFilePath: String? = null
)

data class AddonHealth(
    val addonId: String,
    val name: String,
    val pingMs: Int,
    val isOnline: Boolean,
    val statusText: String
)

data class CustomCollection(
    val id: String,
    val name: String,
    val description: String,
    val itemIds: List<String> = emptyList(),
    val coverPoster: String? = null
)

enum class AspectRatioMode {
    FIT,
    FILL_CROP,
    STRETCH
}

data class UpcomingEpisode(
    val id: String,
    val mediaId: String,
    val seriesTitle: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val episodeTitle: String,
    val airDate: String,
    val dayOfWeek: String, // "Monday", "Tuesday", "Wednesday", etc.
    val thumbnail: String? = null,
    val poster: String? = null,
    val overview: String = "",
    val isWatched: Boolean = false,
    val hasReminder: Boolean = false
)

data class WatchStats(
    val totalWatchHours: Int = 186,
    val totalWatchMinutes: Int = 45,
    val moviesWatchedCount: Int = 42,
    val seriesWatchedCount: Int = 14,
    val episodesBingedCount: Int = 238,
    val bingeStreakDays: Int = 9,
    val topGenres: List<Pair<String, Float>> = listOf(
        "Sci-Fi" to 0.38f,
        "Action" to 0.28f,
        "Anime" to 0.18f,
        "Drama" to 0.10f,
        "Thriller" to 0.06f
    ),
    val traktUsername: String = "cinephile_alex",
    val traktConnected: Boolean = true,
    val traktScrobbleEnabled: Boolean = true,
    val aniListConnected: Boolean = true
)

data class CommunityReview(
    val id: String,
    val mediaId: String,
    val userName: String,
    val userAvatar: String? = null,
    val rating: Float, // 1 to 10
    val date: String,
    val headline: String,
    val content: String,
    val isSpoiler: Boolean = false,
    val helpfulCount: Int = 0,
    val tags: List<String> = emptyList()
)

data class CriticScores(
    val imdbScore: String = "8.8",
    val imdbVotes: String = "2.1M",
    val rottenTomatoesScore: Int = 94,
    val rottenTomatoesAudience: Int = 96,
    val metacriticScore: Int = 88
)

data class DebridAccount(
    val provider: String = "Real-Debrid",
    val apiKey: String = "RD-Secured-Token-•••••••••••••",
    val isConnected: Boolean = true,
    val accountTier: String = "VIP Premium",
    val daysRemaining: Int = 248,
    val fidelityPoints: Int = 1420,
    val selectedCdnServer: String = "Cloudflare CDN (Global Low-Latency)",
    val cachedOnlyMode: Boolean = true
)

data class CloudTorrent(
    val id: String,
    val name: String,
    val size: String,
    val progressPercent: Float,
    val status: String,
    val streamUrl: String
)
