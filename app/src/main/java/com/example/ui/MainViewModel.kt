package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Addon
import com.example.data.model.AudioPlaybackSettings
import com.example.data.model.AudioTrack
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaStream
import com.example.data.model.SubtitlePlaybackSettings
import com.example.data.model.SubtitleTrack
import com.example.data.model.WatchlistItem
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    DISCOVER,
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
    val isBuffering: Boolean = false
)

class MainViewModel(
    private val repository: MediaRepository = MediaRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(AppTab.DISCOVER)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val searchResults: StateFlow<List<MediaItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

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

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _showAddCustomAddonDialog = MutableStateFlow(false)
    val showAddCustomAddonDialog: StateFlow<Boolean> = _showAddCustomAddonDialog.asStateFlow()

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

    init {
        loadCatalogs()
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        viewModelScope.launch {
            _isSearching.value = true
            val results = repository.search(query)
            _searchResults.value = results
            _isSearching.value = false
        }
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
        viewModelScope.launch {
            val fullDetails = repository.getMediaDetails(item.type, item.id)
            _selectedMedia.value = fullDetails
            _selectedMediaStreams.value = repository.getStreamsForMedia(fullDetails)
        }
    }

    fun closeMediaDetail() {
        _selectedMedia.value = null
        _selectedMediaStreams.value = emptyList()
    }

    fun toggleWatchlist(item: MediaItem) {
        repository.toggleWatchlist(item)
    }

    fun isWatchlisted(mediaId: String): Boolean {
        return repository.isWatchlisted(mediaId)
    }

    fun playStream(media: MediaItem, stream: MediaStream, episode: Episode? = null) {
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
            isPlaying = true
        )
    }

    fun playMediaDefault(media: MediaItem, episode: Episode? = null) {
        val streams = repository.getStreamsForMedia(media, episode?.number ?: 1)
        val defaultStream = streams.firstOrNull() ?: return
        playStream(media, defaultStream, episode)
    }

    fun switchStream(stream: MediaStream) {
        val current = _playerState.value
        _playerState.value = current.copy(stream = stream)
    }

    fun closePlayer() {
        val current = _playerState.value
        if (current.media != null && current.durationMs > 0) {
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

    // Audio Settings Actions
    fun setAudioTrack(trackId: String) {
        repository.updateAudioSettings(audioSettings.value.copy(selectedTrackId = trackId))
    }

    fun setAudioBoost(level: Int) {
        repository.updateAudioSettings(audioSettings.value.copy(audioBoostLevel = level))
    }

    fun setAudioDelay(delayMs: Int) {
        repository.updateAudioSettings(audioSettings.value.copy(audioDelayMs = delayMs))
    }

    fun setPlaybackSpeed(speed: Float) {
        repository.updateAudioSettings(audioSettings.value.copy(playbackSpeed = speed))
    }

    fun toggleSurroundSound(enabled: Boolean) {
        repository.updateAudioSettings(audioSettings.value.copy(surroundSoundEnabled = enabled))
    }

    fun toggleSpeechClarity(enabled: Boolean) {
        repository.updateAudioSettings(audioSettings.value.copy(speechClarity = enabled))
    }

    // Subtitle Settings Actions
    fun setSubtitleTrack(trackId: String) {
        repository.updateSubtitleSettings(subtitleSettings.value.copy(selectedTrackId = trackId))
    }

    fun setSecondarySubtitleTrack(trackId: String) {
        repository.updateSubtitleSettings(subtitleSettings.value.copy(secondaryTrackId = trackId))
    }

    fun setSubtitleDelay(delayMs: Int) {
        repository.updateSubtitleSettings(subtitleSettings.value.copy(subtitleDelayMs = delayMs))
    }

    fun setSubtitleSize(sizeSp: Int) {
        repository.updateSubtitleSettings(subtitleSettings.value.copy(textSizeSp = sizeSp))
    }

    fun setSubtitleColor(colorHex: Long) {
        repository.updateSubtitleSettings(subtitleSettings.value.copy(textColorHex = colorHex))
    }

    fun setSubtitleBackground(style: String) {
        repository.updateSubtitleSettings(subtitleSettings.value.copy(backgroundStyle = style))
    }

    fun setSubtitlePosition(pos: String) {
        repository.updateSubtitleSettings(subtitleSettings.value.copy(verticalPosition = pos))
    }

    fun toggleAutoSelectSubtitles(enabled: Boolean) {
        repository.updateSubtitleSettings(subtitleSettings.value.copy(autoSelectSubtitles = enabled))
    }

    fun toggleAddonInstalled(addonId: String) {
        repository.toggleAddonInstalled(addonId)
    }

    fun showAddCustomAddonDialog(show: Boolean) {
        _showAddCustomAddonDialog.value = show
    }

    fun addCustomAddon(manifestUrl: String, name: String, desc: String) {
        repository.addCustomAddon(manifestUrl, name, desc)
        _showAddCustomAddonDialog.value = false
    }
}
