package com.example.data.repository

import com.example.data.model.Addon
import com.example.data.model.CatalogCategory
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaStream
import com.example.data.model.WatchlistItem
import com.example.data.remote.StremioApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaRepository(private val api: StremioApi = StremioApi()) {

    // Curated high-fidelity fallback items for instant start
    private val curatedMovies = listOf(
        MediaItem(
            id = "tt1375666",
            title = "Inception",
            type = "movie",
            poster = "https://images.metahub.space/poster/medium/tt1375666/img",
            backdrop = "https://images.metahub.space/background/medium/tt1375666/img",
            year = "2010",
            rating = "8.8",
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
            genres = listOf("Action", "Sci-Fi", "Adventure"),
            cast = listOf("Leonardo DiCaprio", "Joseph Gordon-Levitt", "Elliot Page", "Tom Hardy"),
            director = listOf("Christopher Nolan"),
            runtime = "148 min"
        ),
        MediaItem(
            id = "tt15239678",
            title = "Dune: Part Two",
            type = "movie",
            poster = "https://images.metahub.space/poster/medium/tt15239678/img",
            backdrop = "https://images.metahub.space/background/medium/tt15239678/img",
            year = "2024",
            rating = "8.6",
            description = "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.",
            genres = listOf("Action", "Adventure", "Sci-Fi"),
            cast = listOf("Timothée Chalamet", "Zendaya", "Rebecca Ferguson", "Javier Bardem"),
            director = listOf("Denis Villeneuve"),
            runtime = "166 min"
        ),
        MediaItem(
            id = "tt15398776",
            title = "Oppenheimer",
            type = "movie",
            poster = "https://images.metahub.space/poster/medium/tt15398776/img",
            backdrop = "https://images.metahub.space/background/medium/tt15398776/img",
            year = "2023",
            rating = "8.9",
            description = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.",
            genres = listOf("Biography", "Drama", "History"),
            cast = listOf("Cillian Murphy", "Emily Blunt", "Matt Damon", "Robert Downey Jr."),
            director = listOf("Christopher Nolan"),
            runtime = "180 min"
        ),
        MediaItem(
            id = "tt0816692",
            title = "Interstellar",
            type = "movie",
            poster = "https://images.metahub.space/poster/medium/tt0816692/img",
            backdrop = "https://images.metahub.space/background/medium/tt0816692/img",
            year = "2014",
            rating = "8.7",
            description = "When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft along with a team of researchers to find a new planet for humans.",
            genres = listOf("Adventure", "Drama", "Sci-Fi"),
            cast = listOf("Matthew McConaughey", "Anne Hathaway", "Jessica Chastain"),
            director = listOf("Christopher Nolan"),
            runtime = "169 min"
        ),
        MediaItem(
            id = "tt0468569",
            title = "The Dark Knight",
            type = "movie",
            poster = "https://images.metahub.space/poster/medium/tt0468569/img",
            backdrop = "https://images.metahub.space/background/medium/tt0468569/img",
            year = "2008",
            rating = "9.0",
            description = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            genres = listOf("Action", "Crime", "Drama"),
            cast = listOf("Christian Bale", "Heath Ledger", "Aaron Eckhart", "Michael Caine"),
            director = listOf("Christopher Nolan"),
            runtime = "152 min"
        )
    )

    private val curatedSeries = listOf(
        MediaItem(
            id = "tt0903747",
            title = "Breaking Bad",
            type = "series",
            poster = "https://images.metahub.space/poster/medium/tt0903747/img",
            backdrop = "https://images.metahub.space/background/medium/tt0903747/img",
            year = "2008-2013",
            rating = "9.5",
            description = "A chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine with a former student in order to secure his family's future.",
            genres = listOf("Crime", "Drama", "Thriller"),
            cast = listOf("Bryan Cranston", "Aaron Paul", "Anna Gunn", "Bob Odenkirk"),
            director = listOf("Vince Gilligan"),
            runtime = "49 min",
            episodes = listOf(
                Episode("tt0903747:1:1", 1, 1, "Pilot", "Walter White, a struggling high school chemistry teacher, is diagnosed with Stage III cancer."),
                Episode("tt0903747:1:2", 1, 2, "Cat's in the Bag...", "Walt and Jesse attempt to dispose of two bodies in the RV."),
                Episode("tt0903747:1:3", 1, 3, "...And the Bag's in the River", "Walt wrestles with whether or not to kill Krazy-8."),
                Episode("tt0903747:1:4", 1, 4, "Cancer Man", "Walt tells the rest of the family about his cancer diagnosis."),
                Episode("tt0903747:1:5", 1, 5, "Gray Matter", "Walt is offered financial help from his old business partners.")
            )
        ),
        MediaItem(
            id = "tt11126994",
            title = "Arcane",
            type = "series",
            poster = "https://images.metahub.space/poster/medium/tt11126994/img",
            backdrop = "https://images.metahub.space/background/medium/tt11126994/img",
            year = "2021-2024",
            rating = "9.0",
            description = "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            genres = listOf("Animation", "Action", "Sci-Fi"),
            cast = listOf("Hailee Steinfeld", "Ella Purnell", "Kevin Alejandro"),
            director = listOf("Pascal Charrue", "Arnaud Delord"),
            runtime = "42 min",
            episodes = listOf(
                Episode("tt11126994:1:1", 1, 1, "Welcome to the Playground", "Orphan sisters Vi and Powder lead a crew into the upper city."),
                Episode("tt11126994:1:2", 1, 2, "Some Mysteries Are Better Left Unsolved", "Jayce risks everything to pursue prohibited magic."),
                Episode("tt11126994:1:3", 1, 3, "The Base Violence Necessary for Change", "An old rivalry comes to a devastating head.")
            )
        ),
        MediaItem(
            id = "tt4574334",
            title = "Stranger Things",
            type = "series",
            poster = "https://images.metahub.space/poster/medium/tt4574334/img",
            backdrop = "https://images.metahub.space/background/medium/tt4574334/img",
            year = "2016-2025",
            rating = "8.7",
            description = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl.",
            genres = listOf("Drama", "Fantasy", "Horror"),
            cast = listOf("Millie Bobby Brown", "Finn Wolfhard", "Winona Ryder", "David Harbour"),
            director = listOf("The Duffer Brothers"),
            runtime = "51 min",
            episodes = listOf(
                Episode("tt4574334:1:1", 1, 1, "Chapter One: The Vanishing of Will Byers", "On his way home from a friend's house, young Will sees something terrifying."),
                Episode("tt4574334:1:2", 1, 2, "Chapter Two: The Weirdo on Maple Street", "Lucas, Mike and Dustin try to talk to the girl they found in the woods.")
            )
        ),
        MediaItem(
            id = "tt3581920",
            title = "The Last of Us",
            type = "series",
            poster = "https://images.metahub.space/poster/medium/tt3581920/img",
            backdrop = "https://images.metahub.space/background/medium/tt3581920/img",
            year = "2023-2025",
            rating = "8.8",
            description = "After a global pandemic destroys civilization, a hardened survivor takes charge of a 14-year-old girl who may be humanity's last hope.",
            genres = listOf("Action", "Adventure", "Drama"),
            cast = listOf("Pedro Pascal", "Bella Ramsey", "Gabriel Luna"),
            director = listOf("Craig Mazin", "Neil Druckmann"),
            runtime = "58 min",
            episodes = listOf(
                Episode("tt3581920:1:1", 1, 1, "When You're Lost in the Darkness", "Twenty years after a fungal outbreak ravages the planet, survivors struggle to hold on."),
                Episode("tt3581920:1:2", 1, 2, "Infected", "Joel, Tess, and Ellie navigate through a deserted and flooded Boston.")
            )
        )
    )

    private val curatedAnime = listOf(
        MediaItem(
            id = "tt2560140",
            title = "Attack on Titan",
            type = "anime",
            poster = "https://images.metahub.space/poster/medium/tt2560140/img",
            backdrop = "https://images.metahub.space/background/medium/tt2560140/img",
            year = "2013-2023",
            rating = "9.1",
            description = "After his hometown is destroyed and his mother is killed, young Eren Jaeger vows to cleanse the earth of the giant humanoid Titans that have brought humanity to the brink of extinction.",
            genres = listOf("Animation", "Action", "Adventure", "Fantasy"),
            cast = listOf("Yuki Kaji", "Yui Ishikawa", "Marina Inoue"),
            director = listOf("Tetsuro Araki"),
            runtime = "24 min"
        ),
        MediaItem(
            id = "tt13622776",
            title = "Jujutsu Kaisen",
            type = "anime",
            poster = "https://images.metahub.space/poster/medium/tt13622776/img",
            backdrop = "https://images.metahub.space/background/medium/tt13622776/img",
            year = "2020-2024",
            rating = "8.6",
            description = "A boy swallows a cursed talisman - the finger of a demon - and becomes cursed himself. He enters a shaman's school to be able to locate the demon's other body parts and thus exorcise himself.",
            genres = listOf("Animation", "Action", "Supernatural"),
            cast = listOf("Junya Enoki", "Yuma Uchida", "Asami Seto"),
            director = listOf("Sunghoo Park"),
            runtime = "23 min"
        )
    )

    // Addons State
    private val _addons = MutableStateFlow(
        listOf(
            Addon(
                id = "org.stremio.cinemeta",
                name = "Cinemeta (Official)",
                version = "3.0.12",
                description = "Official movie and TV series directory powered by IMDb, TheMovieDatabase, and TVMaze.",
                iconUrl = "https://v3-cinemeta.strem.io/logo.png",
                manifestUrl = "https://v3-cinemeta.strem.io/manifest.json",
                types = listOf("movie", "series"),
                isInstalled = true,
                isOfficial = true,
                author = "Stremio Team"
            ),
            Addon(
                id = "org.stremio.torrentio",
                name = "Torrentio Lite",
                version = "1.0.8",
                description = "Scrapes streams with quality filters (4K, 1080p, 720p) and multi-language audio tracks.",
                iconUrl = "https://torrentio.strem.fun/images/logo.png",
                manifestUrl = "https://torrentio.strem.fun/manifest.json",
                types = listOf("movie", "series", "anime"),
                isInstalled = true,
                isOfficial = false,
                author = "TheCommunity"
            ),
            Addon(
                id = "org.stremio.opensubtitles",
                name = "OpenSubtitles v3",
                version = "3.2.0",
                description = "Multi-language synchronized subtitle provider with auto-sync and offset adjustment.",
                iconUrl = "https://static.opensubtitles.com/favicon.ico",
                manifestUrl = "https://opensubtitles.strem.io/manifest.json",
                types = listOf("movie", "series"),
                isInstalled = true,
                isOfficial = true,
                author = "OpenSubtitles"
            ),
            Addon(
                id = "org.stremio.openstream",
                name = "Public Domain & OpenStream",
                version = "2.1.0",
                description = "Direct open-source high-definition video streams and public domain feature films.",
                iconUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
                manifestUrl = "https://openstream.strem.io/manifest.json",
                types = listOf("movie", "series"),
                isInstalled = true,
                isOfficial = true,
                author = "Aether Team"
            ),
            Addon(
                id = "org.stremio.cyberflix",
                name = "CyberFlix Catalog",
                version = "1.4.2",
                description = "Aggregated catalogs tracking trending releases across Netflix, HBO Max, Disney+, and Apple TV+.",
                iconUrl = "https://cyberflix.strem.fun/logo.png",
                manifestUrl = "https://cyberflix.strem.fun/manifest.json",
                types = listOf("movie", "series"),
                isInstalled = false,
                isOfficial = false,
                author = "CyberFlix"
            ),
            Addon(
                id = "org.stremio.animekitsu",
                name = "Anime Kitsu",
                version = "2.0.4",
                description = "Comprehensive Japanese anime catalog with episodes, seasons, and Japanese voice track streams.",
                iconUrl = "https://anime-kitsu.strem.fun/logo.png",
                manifestUrl = "https://anime-kitsu.strem.fun/manifest.json",
                types = listOf("anime", "series"),
                isInstalled = false,
                isOfficial = false,
                author = "AnimeKitsu"
            ),
            Addon(
                id = "org.stremio.watchhub",
                name = "WatchHub",
                version = "1.1.0",
                description = "Official streaming platform availability lookup for Netflix, Prime Video, Hulu, and Paramount+.",
                iconUrl = "https://watchhub.strem.io/logo.png",
                manifestUrl = "https://watchhub.strem.io/manifest.json",
                types = listOf("movie", "series"),
                isInstalled = false,
                isOfficial = true,
                author = "Stremio Team"
            )
        )
    )
    val addons: StateFlow<List<Addon>> = _addons.asStateFlow()

    // Watchlist & Continue Watching State
    private val _watchlist = MutableStateFlow(
        listOf(
            WatchlistItem(
                mediaId = "tt1375666",
                title = "Inception",
                poster = "https://images.metahub.space/poster/medium/tt1375666/img",
                type = "movie",
                year = "2010",
                rating = "8.8",
                progressPercent = 0.45f,
                lastWatchedPositionMs = 3996000L, // ~66 min
                totalDurationMs = 8880000L
            ),
            WatchlistItem(
                mediaId = "tt15239678",
                title = "Dune: Part Two",
                poster = "https://images.metahub.space/poster/medium/tt15239678/img",
                type = "movie",
                year = "2024",
                rating = "8.6",
                progressPercent = 0.12f,
                lastWatchedPositionMs = 1195000L,
                totalDurationMs = 9960000L
            ),
            WatchlistItem(
                mediaId = "tt11126994",
                title = "Arcane",
                poster = "https://images.metahub.space/poster/medium/tt11126994/img",
                type = "series",
                year = "2021",
                rating = "9.0",
                progressPercent = 0.85f,
                lastWatchedPositionMs = 2142000L,
                totalDurationMs = 2520000L
            )
        )
    )
    val watchlist: StateFlow<List<WatchlistItem>> = _watchlist.asStateFlow()

    suspend fun getTrending(): List<MediaItem> {
        val remote = api.getCatalog("movie", "top")
        return if (remote.isNotEmpty()) remote.take(10) else curatedMovies
    }

    suspend fun getPopularMovies(): List<MediaItem> {
        val remote = api.getCatalog("movie", "top")
        return if (remote.isNotEmpty()) remote else curatedMovies
    }

    suspend fun getPopularSeries(): List<MediaItem> {
        val remote = api.getCatalog("series", "top")
        return if (remote.isNotEmpty()) remote else curatedSeries
    }

    suspend fun getAnimeCatalog(): List<MediaItem> {
        return curatedAnime
    }

    suspend fun search(query: String): List<MediaItem> {
        if (query.isBlank()) return emptyList()
        val movieResults = api.searchCatalog("movie", query)
        val seriesResults = api.searchCatalog("series", query)
        val combined = (movieResults + seriesResults).distinctBy { it.id }
        if (combined.isNotEmpty()) return combined

        // Fallback search in curated items
        val allCurated = curatedMovies + curatedSeries + curatedAnime
        return allCurated.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.genres.any { g -> g.contains(query, ignoreCase = true) }
        }
    }

    suspend fun getMediaDetails(type: String, id: String): MediaItem {
        val remote = api.getMeta(type, id)
        if (remote != null) {
            // If series and remote has no episodes, populate episodes
            if (remote.type == "series" && remote.episodes.isEmpty()) {
                val dummyEpisodes = (1..8).map { epNum ->
                    Episode(
                        id = "$id:1:$epNum",
                        season = 1,
                        number = epNum,
                        title = "Episode $epNum",
                        overview = "The consequences of recent events unfold as rival factions vie for control."
                    )
                }
                return remote.copy(episodes = dummyEpisodes)
            }
            return remote
        }
        val found = (curatedMovies + curatedSeries + curatedAnime).find { it.id == id }
        return found ?: MediaItem(
            id = id,
            title = "Featured Media",
            type = type,
            poster = null,
            backdrop = null,
            year = "2024",
            rating = "8.4",
            description = "High resolution stream source ready for playback via installed community addons."
        )
    }

    // Direct, highly reliable video streams (Blender open movies & public streams for guaranteed playback)
    fun getStreamsForMedia(media: MediaItem, episodeNum: Int = 1): List<MediaStream> {
        val sampleStreamUrls = listOf(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        )
        val directUrl = sampleStreamUrls[Math.abs(media.id.hashCode()) % sampleStreamUrls.size]
        val secondaryUrl = sampleStreamUrls[(Math.abs(media.id.hashCode()) + 1) % sampleStreamUrls.size]

        return listOf(
            MediaStream(
                id = "${media.id}_4k_torrentio",
                name = "Torrentio [RD]",
                title = "4K • HDR • HEVC • Remux • Atmos 7.1",
                quality = "4K",
                streamUrl = directUrl,
                format = "MP4",
                addonName = "Torrentio Lite",
                seeders = 342,
                sizeText = "14.2 GB"
            ),
            MediaStream(
                id = "${media.id}_1080p_torrentio",
                name = "Torrentio [P2P]",
                title = "1080p • x264 • DDP5.1 • English Multi-Sub",
                quality = "1080p",
                streamUrl = secondaryUrl,
                format = "MP4",
                addonName = "Torrentio Lite",
                seeders = 188,
                sizeText = "2.8 GB"
            ),
            MediaStream(
                id = "${media.id}_1080p_openstream",
                name = "OpenStream HD",
                title = "1080p • Direct High-Speed CDN • AAC 2.0",
                quality = "1080p",
                streamUrl = directUrl,
                format = "MP4",
                addonName = "Public Domain & OpenStream",
                seeders = null,
                sizeText = "1.9 GB"
            ),
            MediaStream(
                id = "${media.id}_720p_fast",
                name = "Aether Cloud Fast",
                title = "720p • Mobile Optimized • Instant Start",
                quality = "720p",
                streamUrl = secondaryUrl,
                format = "MP4",
                addonName = "CyberFlix Catalog",
                seeders = null,
                sizeText = "950 MB"
            )
        )
    }

    fun toggleWatchlist(item: MediaItem) {
        val current = _watchlist.value.toMutableList()
        val index = current.indexOfFirst { it.mediaId == item.id }
        if (index >= 0) {
            current.removeAt(index)
        } else {
            current.add(
                0,
                WatchlistItem(
                    mediaId = item.id,
                    title = item.title,
                    poster = item.poster,
                    type = item.type,
                    year = item.year,
                    rating = item.rating,
                    progressPercent = 0.05f
                )
            )
        }
        _watchlist.value = current
    }

    fun updateProgress(mediaId: String, currentPosMs: Long, totalDurationMs: Long) {
        val current = _watchlist.value.toMutableList()
        val percent = if (totalDurationMs > 0) (currentPosMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f) else 0f
        val index = current.indexOfFirst { it.mediaId == mediaId }
        if (index >= 0) {
            current[index] = current[index].copy(
                progressPercent = percent,
                lastWatchedPositionMs = currentPosMs,
                totalDurationMs = totalDurationMs,
                updatedAt = System.currentTimeMillis()
            )
            _watchlist.value = current
        }
    }

    fun isWatchlisted(mediaId: String): Boolean {
        return _watchlist.value.any { it.mediaId == mediaId }
    }

    fun toggleAddonInstalled(addonId: String) {
        val current = _addons.value.map {
            if (it.id == addonId) it.copy(isInstalled = !it.isInstalled) else it
        }
        _addons.value = current
    }

    fun addCustomAddon(manifestUrl: String, name: String, desc: String) {
        val newAddon = Addon(
            id = "custom.${System.currentTimeMillis()}",
            name = if (name.isNotBlank()) name else "Custom Community Add-on",
            version = "1.0.0",
            description = if (desc.isNotBlank()) desc else "Custom manifest at $manifestUrl",
            iconUrl = "https://images.metahub.space/logo/medium/default/img",
            manifestUrl = manifestUrl,
            types = listOf("movie", "series"),
            isInstalled = true,
            isOfficial = false,
            author = "User Installed"
        )
        _addons.value = listOf(newAddon) + _addons.value
    }

    // Audio and Subtitle Global State
    private val _audioSettings = MutableStateFlow(com.example.data.model.AudioPlaybackSettings())
    val audioSettings: StateFlow<com.example.data.model.AudioPlaybackSettings> = _audioSettings.asStateFlow()

    private val _subtitleSettings = MutableStateFlow(com.example.data.model.SubtitlePlaybackSettings())
    val subtitleSettings: StateFlow<com.example.data.model.SubtitlePlaybackSettings> = _subtitleSettings.asStateFlow()

    fun updateAudioSettings(newSettings: com.example.data.model.AudioPlaybackSettings) {
        _audioSettings.value = newSettings
    }

    fun updateSubtitleSettings(newSettings: com.example.data.model.SubtitlePlaybackSettings) {
        _subtitleSettings.value = newSettings
    }

    fun getAudioTracksForMedia(media: MediaItem): List<com.example.data.model.AudioTrack> {
        return listOf(
            com.example.data.model.AudioTrack("en_orig", "English [Original]", "en", "Dolby 5.1", "AC3"),
            com.example.data.model.AudioTrack("en_stereo", "English [Descriptive Audio]", "en", "Stereo 2.0", "AAC"),
            com.example.data.model.AudioTrack("es_dub", "Spanish (Latinoamérica)", "es", "Dolby 5.1", "AC3"),
            com.example.data.model.AudioTrack("fr_dub", "French (Français)", "fr", "Stereo 2.0", "AAC"),
            com.example.data.model.AudioTrack("de_dub", "German (Deutsch)", "de", "Dolby 5.1", "AC3"),
            com.example.data.model.AudioTrack("ja_orig", "Japanese (日本語)", "ja", "Stereo 2.0", "AAC"),
            com.example.data.model.AudioTrack("commentary", "Director & Crew Commentary", "en", "Stereo 2.0", "AAC")
        )
    }

    fun getSubtitleTracksForMedia(media: MediaItem): List<com.example.data.model.SubtitleTrack> {
        return listOf(
            com.example.data.model.SubtitleTrack("none", "Off (No Subtitles)", "none", ""),
            com.example.data.model.SubtitleTrack("en_sub", "English [CC & Full]", "en", "SRT", "OpenSubtitles v3"),
            com.example.data.model.SubtitleTrack("es_sub", "Spanish (Español)", "es", "SRT", "OpenSubtitles v3"),
            com.example.data.model.SubtitleTrack("fr_sub", "French (Français)", "fr", "SRT", "OpenSubtitles v3"),
            com.example.data.model.SubtitleTrack("de_sub", "German (Deutsch)", "de", "SRT", "OpenSubtitles v3"),
            com.example.data.model.SubtitleTrack("ja_sub", "Japanese (日本語)", "ja", "VTT", "AnimeKitsu Sub"),
            com.example.data.model.SubtitleTrack("pt_sub", "Portuguese (Português)", "pt", "SRT", "OpenSubtitles v3"),
            com.example.data.model.SubtitleTrack("ar_sub", "Arabic (العربية)", "ar", "SRT", "OpenSubtitles v3")
        )
    }
}
