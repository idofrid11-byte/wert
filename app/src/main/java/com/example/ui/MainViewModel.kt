package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Addon
import com.example.data.model.AddonHealth
import com.example.data.model.AspectRatioMode
import com.example.data.model.AudioPlaybackSettings
import com.example.data.model.AudioTrack
import com.example.data.model.CastMember
import com.example.data.model.CloudTorrent
import com.example.data.model.CommunityReview
import com.example.data.model.CriticScores
import com.example.data.model.CustomCollection
import com.example.data.model.DebridAccount
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaStream
import com.example.data.model.OfflineDownload
import com.example.data.model.StreamingService
import com.example.data.model.SubtitlePlaybackSettings
import com.example.data.model.SubtitleTrack
import com.example.data.model.UpcomingEpisode
import com.example.data.model.UserAccount
import com.example.data.model.WatchStats
import com.example.data.model.WatchlistItem
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    DISCOVER,
    CALENDAR,
    LIBRARY,
    ADDONS,
    SETTINGS
}

data class PlayerState(
    val isOpen: Boolean = false,
    val media: MediaItem? = null,
    val episode: Episode? = null,
    val stream: MediaStream? = null,
    val availableStreams: List<MediaStream> = emptyList(),
    val availableAudioTracks: List<AudioTrack> = emptyList(),
    val availableSubtitleTracks: List<SubtitleTrack> = emptyList(),
    val isPlaying: Boolean = true,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isBuffering: Boolean = false,
    val aspectRatioMode: AspectRatioMode = AspectRatioMode.FIT,
    val sleepTimerRemainingSec: Int? = null,
    val isTrailerMode: Boolean = false
)

class MainViewModel(
    private val repository: MediaRepository = MediaRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(AppTab.DISCOVER)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedStreamingService = MutableStateFlow("all")
    val selectedStreamingService: StateFlow<String> = _selectedStreamingService.asStateFlow()

    val streamingServices: List<StreamingService> = repository.getStreamingServices()

    fun selectStreamingService(serviceId: String) {
        _selectedStreamingService.value = serviceId
    }

    fun getMediaForStreamingService(serviceId: String): List<MediaItem> {
        return repository.getMediaForStreamingService(serviceId)
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val searchResults: StateFlow<List<MediaItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchHistory = MutableStateFlow(listOf("Stranger Things", "Dune 2", "Arcane", "HBO Max", "4K HDR"))
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private var searchJob: Job? = null

    private val _trendingList = MutableStateFlow<List<MediaItem>>(emptyList())
    val trendingList: StateFlow<List<MediaItem>> = _trendingList.asStateFlow()

    private val _popularMovies = MutableStateFlow<List<MediaItem>>(emptyList())
    val popularMovies: StateFlow<List<MediaItem>> = _popularMovies.asStateFlow()

    private val _popularSeries = MutableStateFlow<List<MediaItem>>(emptyList())
    val popularSeries: StateFlow<List<MediaItem>> = _popularSeries.asStateFlow()

    private val _animeList = MutableStateFlow<List<MediaItem>>(emptyList())
    val animeList: StateFlow<List<MediaItem>> = _animeList.asStateFlow()

    private val _featuredItem = MutableStateFlow<MediaItem?>(null)
    val featuredItem: StateFlow<MediaItem?> = _featuredItem.asStateFlow()

    private val _selectedMedia = MutableStateFlow<MediaItem?>(null)
    val selectedMedia: StateFlow<MediaItem?> = _selectedMedia.asStateFlow()

    private val _selectedMediaStreams = MutableStateFlow<List<MediaStream>>(emptyList())
    val selectedMediaStreams: StateFlow<List<MediaStream>> = _selectedMediaStreams.asStateFlow()

    private val _similarMedia = MutableStateFlow<List<MediaItem>>(emptyList())
    val similarMedia: StateFlow<List<MediaItem>> = _similarMedia.asStateFlow()

    private val _castProfiles = MutableStateFlow<List<CastMember>>(emptyList())
    val castProfiles: StateFlow<List<CastMember>> = _castProfiles.asStateFlow()

    private val _selectedCastMember = MutableStateFlow<CastMember?>(null)
    val selectedCastMember: StateFlow<CastMember?> = _selectedCastMember.asStateFlow()

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _showAddCustomAddonDialog = MutableStateFlow(false)
    val showAddCustomAddonDialog: StateFlow<Boolean> = _showAddCustomAddonDialog.asStateFlow()

    private val _showCreateCollectionDialog = MutableStateFlow(false)
    val showCreateCollectionDialog: StateFlow<Boolean> = _showCreateCollectionDialog.asStateFlow()

    private var sleepTimerJob: Job? = null

    val addons: StateFlow<List<Addon>> = repository.addons.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val watchlist: StateFlow<List<WatchlistItem>> = repository.watchlist.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val downloads: StateFlow<List<OfflineDownload>> = repository.downloads.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val collections: StateFlow<List<CustomCollection>> = repository.collections.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val audioSettings: StateFlow<AudioPlaybackSettings> = repository.audioSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AudioPlaybackSettings()
    )

    val subtitleSettings: StateFlow<SubtitlePlaybackSettings> = repository.subtitleSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SubtitlePlaybackSettings()
    )

    // User Authentication
    val currentUser: StateFlow<UserAccount?> = repository.currentUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // Calendar & Up Next
    val upcomingEpisodes: StateFlow<List<UpcomingEpisode>> = repository.upcomingEpisodes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Watch Stats & Analytics
    val watchStats: StateFlow<WatchStats> = repository.watchStats.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        WatchStats()
    )

    // Community Reviews
    val communityReviews: StateFlow<List<CommunityReview>> = repository.communityReviews.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Debrid Account
    val debridAccount: StateFlow<DebridAccount> = repository.debridAccount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DebridAccount()
    )

    // Cloud Torrents
    val cloudTorrents: StateFlow<List<CloudTorrent>> = repository.cloudTorrents.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Parental Controls
    val kidsModeEnabled: StateFlow<Boolean> = repository.kidsModeEnabled.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val parentalPin: StateFlow<String> = repository.parentalPin.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "1234"
    )

    private val _addonHealth = MutableStateFlow(repository.checkAddonHealth())
    val addonHealth: StateFlow<List<AddonHealth>> = _addonHealth.asStateFlow()

    fun refreshAddonHealth() {
        _addonHealth.value = repository.checkAddonHealth().map {
            it.copy(pingMs = (it.pingMs + (-6..9).random()).coerceAtLeast(18))
        }
    }

    init {
        loadCatalogs()
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun onSearchQueryChanged(query: String, immediate: Boolean = false) {
        _searchQuery.value = query
        if (query.isBlank()) {
            searchJob?.cancel()
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        _isSearching.value = true
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (!immediate) {
                delay(300L) // 300ms debounce
            }
            val results = repository.search(query)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun submitSearch(query: String) {
        addToSearchHistory(query)
        onSearchQueryChanged(query, immediate = true)
    }

    fun addToSearchHistory(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotBlank()) {
            _searchHistory.value = listOf(trimmed) + _searchHistory.value.filter { !it.equals(trimmed, ignoreCase = true) }.take(9)
        }
    }

    fun removeSearchHistoryItem(query: String) {
        _searchHistory.value = _searchHistory.value.filter { it != query }
    }

    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
    }

    fun clearSearch() {
        onSearchQueryChanged("", immediate = true)
    }

    fun loadCatalogs() {
        viewModelScope.launch {
            val trending = repository.getTrending()
            _trendingList.value = trending
            if (_featuredItem.value == null && trending.isNotEmpty()) {
                _featuredItem.value = trending.first()
            }

            val movies = repository.getPopularMovies()
            _popularMovies.value = movies

            val series = repository.getPopularSeries()
            _popularSeries.value = series

            val anime = repository.getAnimeCatalog()
            _animeList.value = anime
        }
    }

    fun openMediaDetail(item: MediaItem) {
        _selectedMedia.value = item
        _selectedMediaStreams.value = repository.getStreamsForMedia(item)
        _similarMedia.value = repository.getSimilarMedia(item)
        _castProfiles.value = repository.getCastProfilesForMedia(item)

        viewModelScope.launch {
            val fullDetails = repository.getMediaDetails(item.type, item.id)
            _selectedMedia.value = fullDetails
            _selectedMediaStreams.value = repository.getStreamsForMedia(fullDetails)
            _similarMedia.value = repository.getSimilarMedia(fullDetails)
            _castProfiles.value = repository.getCastProfilesForMedia(fullDetails)
        }
    }

    fun closeMediaDetail() {
        _selectedMedia.value = null
        _selectedMediaStreams.value = emptyList()
        _similarMedia.value = emptyList()
        _castProfiles.value = emptyList()
        _selectedCastMember.value = null
    }

    fun openCastProfile(member: CastMember) {
        _selectedCastMember.value = member
    }

    fun closeCastProfile() {
        _selectedCastMember.value = null
    }

    fun toggleWatchlist(item: MediaItem) {
        repository.toggleWatchlist(item)
    }

    fun isWatchlisted(mediaId: String): Boolean {
        return repository.isWatchlisted(mediaId)
    }

    fun playStream(media: MediaItem, stream: MediaStream, episode: Episode? = null, isTrailer: Boolean = false) {
        val streams = repository.getStreamsForMedia(media, episode?.number ?: 1)
        val audioTracks = repository.getAudioTracksForMedia(media)
        val subtitleTracks = repository.getSubtitleTracksForMedia(media)
        _playerState.value = PlayerState(
            isOpen = true,
            media = media,
            episode = episode,
            stream = stream,
            availableStreams = streams,
            availableAudioTracks = audioTracks,
            availableSubtitleTracks = subtitleTracks,
            isPlaying = true,
            isTrailerMode = isTrailer
        )
    }

    fun playMediaDefault(media: MediaItem, episode: Episode? = null) {
        val streams = repository.getStreamsForMedia(media, episode?.number ?: 1)
        val defaultStream = streams.firstOrNull() ?: return
        playStream(media, defaultStream, episode)
    }

    fun playTrailer(media: MediaItem) {
        val streams = repository.getStreamsForMedia(media)
        val trailerStream = streams.lastOrNull()?.copy(title = "Official Teaser Trailer (HD)") ?: return
        playStream(media, trailerStream, isTrailer = true)
    }

    fun playNextEpisode() {
        val current = _playerState.value
        val media = current.media ?: return
        val currentEp = current.episode ?: return
        val nextEp = media.episodes.find { it.number == currentEp.number + 1 }
        if (nextEp != null) {
            playMediaDefault(media, nextEp)
        }
    }

    fun switchStream(stream: MediaStream) {
        val current = _playerState.value
        _playerState.value = current.copy(stream = stream)
    }

    fun setAspectRatio(mode: AspectRatioMode) {
        _playerState.value = _playerState.value.copy(aspectRatioMode = mode)
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            _playerState.value = _playerState.value.copy(sleepTimerRemainingSec = null)
            return
        }

        val totalSec = minutes * 60
        _playerState.value = _playerState.value.copy(sleepTimerRemainingSec = totalSec)

        sleepTimerJob = viewModelScope.launch {
            var remaining = totalSec
            while (remaining > 0) {
                delay(1000)
                remaining--
                _playerState.value = _playerState.value.copy(sleepTimerRemainingSec = remaining)
            }
            // Timer expired: pause playback
            _playerState.value = _playerState.value.copy(isPlaying = false, sleepTimerRemainingSec = null)
        }
    }

    fun closePlayer() {
        sleepTimerJob?.cancel()
        val current = _playerState.value
        if (current.media != null && current.durationMs > 0 && !current.isTrailerMode) {
            repository.updateProgress(current.media.id, current.currentPositionMs, current.durationMs)
        }
        _playerState.value = PlayerState(isOpen = false)
    }

    fun updatePlayerPlayback(isPlaying: Boolean, positionMs: Long, durationMs: Long, isBuffering: Boolean) {
        _playerState.value = _playerState.value.copy(
            isPlaying = isPlaying,
            currentPositionMs = positionMs,
            durationMs = durationMs,
            isBuffering = isBuffering
        )
    }

    // Audio Settings
    fun setAudioTrack(trackId: String) = repository.updateAudioSettings(audioSettings.value.copy(selectedTrackId = trackId))
    fun setAudioBoost(level: Int) = repository.updateAudioSettings(audioSettings.value.copy(audioBoostLevel = level))
    fun setAudioDelay(delayMs: Int) = repository.updateAudioSettings(audioSettings.value.copy(audioDelayMs = delayMs))
    fun setPlaybackSpeed(speed: Float) = repository.updateAudioSettings(audioSettings.value.copy(playbackSpeed = speed))
    fun toggleSurroundSound(enabled: Boolean) = repository.updateAudioSettings(audioSettings.value.copy(surroundSoundEnabled = enabled))
    fun toggleSpeechClarity(enabled: Boolean) = repository.updateAudioSettings(audioSettings.value.copy(speechClarity = enabled))
    fun setEqualizerPreset(preset: String) = repository.updateAudioSettings(audioSettings.value.copy(equalizerPreset = preset))
    fun setStereoWidening(percent: Int) = repository.updateAudioSettings(audioSettings.value.copy(stereoWideningPercent = percent))
    fun setDynamicRangeCompression(drc: String) = repository.updateAudioSettings(audioSettings.value.copy(dynamicRangeCompression = drc))
    fun setOutputRouting(routing: String) = repository.updateAudioSettings(audioSettings.value.copy(outputRouting = routing))
    fun setPreAmpGain(gainDb: Int) = repository.updateAudioSettings(audioSettings.value.copy(preAmpGainDb = gainDb))
    fun togglePitchCorrection(enabled: Boolean) = repository.updateAudioSettings(audioSettings.value.copy(pitchCorrectionEnabled = enabled))
    fun toggleNightModeVolumeLeveling(enabled: Boolean) = repository.updateAudioSettings(audioSettings.value.copy(nightModeVolumeLeveling = enabled))
    fun setAudioPassthrough(passthrough: String) = repository.updateAudioSettings(audioSettings.value.copy(audioPassthrough = passthrough))
    fun togglePreferredHebrewAudio(enabled: Boolean) = repository.updateAudioSettings(audioSettings.value.copy(preferredHebrewAudio = enabled))
    fun setAudioBalance(balance: Float) = repository.updateAudioSettings(audioSettings.value.copy(audioBalance = balance))

    // Subtitle Settings
    fun setSubtitleTrack(trackId: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(selectedTrackId = trackId))
    fun setSecondarySubtitleTrack(trackId: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(secondaryTrackId = trackId))
    fun setSubtitleDelay(delayMs: Int) = repository.updateSubtitleSettings(subtitleSettings.value.copy(subtitleDelayMs = delayMs))
    fun setSubtitleSize(sizeSp: Int) = repository.updateSubtitleSettings(subtitleSettings.value.copy(textSizeSp = sizeSp))
    fun setSubtitleColor(colorHex: Long) = repository.updateSubtitleSettings(subtitleSettings.value.copy(textColorHex = colorHex))
    fun setSubtitleBackground(style: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(backgroundStyle = style))
    fun setSubtitlePosition(pos: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(verticalPosition = pos))
    fun toggleAutoSelectSubtitles(enabled: Boolean) = repository.updateSubtitleSettings(subtitleSettings.value.copy(autoSelectSubtitles = enabled))
    fun setSubtitleStroke(stroke: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(strokeThickness = stroke))
    fun setSubtitleFontStyle(fontStyle: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(fontStyle = fontStyle))
    fun setSubtitleBoxOpacity(opacityPercent: Int) = repository.updateSubtitleSettings(subtitleSettings.value.copy(boxOpacityPercent = opacityPercent))
    fun setSubtitleSafeInset(insetDp: Int) = repository.updateSubtitleSettings(subtitleSettings.value.copy(bottomSafeInsetDp = insetDp))
    fun toggleForceUppercase(force: Boolean) = repository.updateSubtitleSettings(subtitleSettings.value.copy(forceUppercase = force))
    fun setSubtitleProvider(provider: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(activeSubProvider = provider))
    fun togglePreferredHebrewSubtitles(enabled: Boolean) = repository.updateSubtitleSettings(subtitleSettings.value.copy(preferredHebrewSubtitles = enabled))
    fun toggleDualSubtitles(enabled: Boolean) = repository.updateSubtitleSettings(subtitleSettings.value.copy(dualSubtitlesEnabled = enabled))
    fun setSubtitleEncoding(encoding: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(subtitleEncoding = encoding))
    fun toggleHebrewRtlOptimization(enabled: Boolean) = repository.updateSubtitleSettings(subtitleSettings.value.copy(cjkHebrewRtlOptimization = enabled))
    fun setSubtitleShadowBlur(blur: String) = repository.updateSubtitleSettings(subtitleSettings.value.copy(textShadowBlur = blur))

    // Offline Downloads
    fun startDownload(media: MediaItem, stream: MediaStream) {
        repository.startDownload(media, stream)
    }
    fun deleteDownload(downloadId: String) {
        repository.deleteDownload(downloadId)
    }

    // Custom Collections
    fun showCreateCollectionDialog(show: Boolean) {
        _showCreateCollectionDialog.value = show
    }
    fun createCollection(name: String, desc: String) {
        repository.createCollection(name, desc)
        _showCreateCollectionDialog.value = false
    }
    fun toggleItemInCollection(collectionId: String, mediaId: String) {
        repository.toggleItemInCollection(collectionId, mediaId)
    }

    // Addons
    fun toggleAddonInstalled(addonId: String) = repository.toggleAddonInstalled(addonId)
    fun showAddCustomAddonDialog(show: Boolean) { _showAddCustomAddonDialog.value = show }
    fun addCustomAddon(manifestUrl: String, name: String, desc: String) {
        repository.addCustomAddon(manifestUrl, name, desc)
        _showAddCustomAddonDialog.value = false
    }

    // ==========================================
    // AUTHENTICATION (MANDATORY GATE)
    // ==========================================
    fun createAccount(email: String, password: String, username: String): Boolean {
        return repository.registerUser(email, password, username)
    }

    fun login(email: String, password: String): Boolean {
        return repository.loginUser(email, password)
    }

    fun logout() {
        repository.logoutUser()
    }

    fun changePassword(newPassword: String): Boolean {
        return repository.changePassword(newPassword)
    }

    // ==========================================
    // CALENDAR & UP NEXT
    // ==========================================
    fun toggleEpisodeWatched(episodeId: String) = repository.toggleEpisodeWatched(episodeId)
    fun toggleEpisodeReminder(episodeId: String) = repository.toggleEpisodeReminder(episodeId)

    // ==========================================
    // STATS & TRAKT
    // ==========================================
    fun toggleTraktScrobble(enabled: Boolean) = repository.toggleTraktScrobble(enabled)
    fun syncTraktCloud() = repository.syncTraktCloud()

    // ==========================================
    // REVIEWS & CRITIC SCORES
    // ==========================================
    fun getCriticScores(mediaId: String): CriticScores = repository.getCriticScoresForMedia(mediaId)
    fun getReviewsForMedia(mediaId: String): List<CommunityReview> = repository.getReviewsForMedia(mediaId)
    fun addReview(mediaId: String, rating: Float, headline: String, content: String, isSpoiler: Boolean, tags: List<String>) {
        repository.addCommunityReview(mediaId, rating, headline, content, isSpoiler, tags)
    }

    // ==========================================
    // DEBRID PROVIDER & CLOUD TORRENTS
    // ==========================================
    fun updateDebridAccount(updated: DebridAccount) = repository.updateDebridAccount(updated)
    fun addMagnetToDebrid(magnetUri: String, name: String = "") = repository.addMagnetToDebrid(magnetUri, name)

    // ==========================================
    // PARENTAL CONTROLS
    // ==========================================
    fun toggleKidsMode(enabled: Boolean) = repository.toggleKidsMode(enabled)
    fun setParentalPin(pin: String) = repository.setParentalPin(pin)
}
