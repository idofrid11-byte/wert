package com.example.data.repository

import com.example.data.model.Addon
import com.example.data.model.CatalogCategory
import com.example.data.model.CloudTorrent
import com.example.data.model.CommunityReview
import com.example.data.model.CriticScores
import com.example.data.model.DebridAccount
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaStream
import com.example.data.model.StreamingService
import com.example.data.model.UpcomingEpisode
import com.example.data.model.UserAccount
import com.example.data.model.WatchStats
import com.example.data.model.WatchlistItem
import com.example.data.remote.StremioApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaRepository(private val api: StremioApi = StremioApi()) {

    // ==========================================
    // USER AUTHENTICATION & ACCOUNT (MANDATORY GATE)
    // ==========================================
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val registeredUsers = mutableMapOf<String, Pair<String, UserAccount>>(
        "alex@aetherstream.io" to Pair("Password123!", UserAccount("user_alex", "alex@aetherstream.io", "CinephileAlex"))
    )

    fun registerUser(email: String, password: String, username: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val cleanUsername = if (username.isNotBlank()) username.trim() else cleanEmail.substringBefore("@")
        val newUser = UserAccount(
            id = "user_${System.currentTimeMillis()}",
            email = cleanEmail,
            username = cleanUsername
        )
        registeredUsers[cleanEmail] = Pair(password, newUser)
        _currentUser.value = newUser
        return true
    }

    fun loginUser(email: String, password: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val record = registeredUsers[cleanEmail]
        if (record != null && record.first == password) {
            _currentUser.value = record.second
            return true
        }
        if (cleanEmail.contains("@") && password.length >= 6) {
            val user = UserAccount(
                id = "user_${cleanEmail.hashCode()}",
                email = cleanEmail,
                username = cleanEmail.substringBefore("@")
            )
            registeredUsers[cleanEmail] = Pair(password, user)
            _currentUser.value = user
            return true
        }
        return false
    }

    fun logoutUser() {
        _currentUser.value = null
    }

    fun changePassword(newPassword: String): Boolean {
        val user = _currentUser.value ?: return false
        registeredUsers[user.email] = Pair(newPassword, user)
        return true
    }

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
            runtime = "148 min",
            serviceProvider = "Netflix"
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
            runtime = "166 min",
            serviceProvider = "HBO Max"
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
            runtime = "180 min",
            serviceProvider = "Apple TV+"
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
            runtime = "169 min",
            serviceProvider = "Disney+"
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
            runtime = "152 min",
            serviceProvider = "HBO Max"
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
            serviceProvider = "Prime Video",
            episodes = listOf(
                Episode("tt0903747:1:1", 1, 1, "Pilot", "Walter White, a struggling high school chemistry teacher, is diagnosed with Stage III cancer."),
                Episode("tt0903747:1:2", 1, 2, "Cat's in the Bag...", "Walt and Jesse attempt to dispose of two bodies in the RV.")
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
            serviceProvider = "Netflix",
            episodes = listOf(
                Episode("tt11126994:1:1", 1, 1, "Welcome to the Playground", "Orphan sisters Vi and Powder lead a crew into the upper city."),
                Episode("tt11126994:1:2", 1, 2, "Some Mysteries Are Better Left Unsolved", "Jayce risks everything to pursue prohibited magic.")
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
            serviceProvider = "Netflix",
            episodes = listOf(
                Episode("tt4574334:1:1", 1, 1, "Chapter One: The Vanishing of Will Byers", "On his way home from a friend's house, young Will sees something terrifying.")
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
            serviceProvider = "HBO Max",
            episodes = listOf(
                Episode("tt3581920:1:1", 1, 1, "When You're Lost in the Darkness", "Twenty years after a fungal outbreak ravages the planet, survivors struggle to hold on.")
            )
        ),
        MediaItem(
            id = "tt8111088",
            title = "The Mandalorian",
            type = "series",
            poster = "https://images.metahub.space/poster/medium/tt8111088/img",
            backdrop = "https://images.metahub.space/background/medium/tt8111088/img",
            year = "2019-2024",
            rating = "8.6",
            description = "The travels of a lone bounty hunter in the outer reaches of the galaxy, far from the authority of the New Republic.",
            genres = listOf("Action", "Adventure", "Sci-Fi"),
            cast = listOf("Pedro Pascal", "Carl Weathers", "Giancarlo Esposito"),
            director = listOf("Jon Favreau"),
            runtime = "40 min",
            serviceProvider = "Disney+",
            episodes = listOf(
                Episode("tt8111088:1:1", 1, 1, "Chapter 1: The Mandalorian", "A Mandalorian bounty hunter tracks a target in a well-guarded enclave.")
            )
        ),
        MediaItem(
            id = "tt11280740",
            title = "Severance",
            type = "series",
            poster = "https://images.metahub.space/poster/medium/tt11280740/img",
            backdrop = "https://images.metahub.space/background/medium/tt11280740/img",
            year = "2022-2025",
            rating = "8.7",
            description = "Mark leads a team of office workers whose memories have been surgically divided between their work and personal lives.",
            genres = listOf("Drama", "Mystery", "Sci-Fi"),
            cast = listOf("Adam Scott", "Zach Cherry", "Patricia Arquette"),
            director = listOf("Ben Stiller"),
            runtime = "53 min",
            serviceProvider = "Apple TV+",
            episodes = listOf(
                Episode("tt11280740:1:1", 1, 1, "Good News About Hell", "Mark Scout is promoted to department head after his colleague is mysteriously terminated.")
            )
        ),
        MediaItem(
            id = "tt1190634",
            title = "The Boys",
            type = "series",
            poster = "https://images.metahub.space/poster/medium/tt1190634/img",
            backdrop = "https://images.metahub.space/background/medium/tt1190634/img",
            year = "2019-2024",
            rating = "8.7",
            description = "A group of vigilantes set out to take down corrupt superheroes who abuse their superpowers.",
            genres = listOf("Action", "Comedy", "Crime"),
            cast = listOf("Karl Urban", "Jack Quaid", "Antony Starr"),
            director = listOf("Eric Kripke"),
            runtime = "60 min",
            serviceProvider = "Prime Video",
            episodes = listOf(
                Episode("tt1190634:1:1", 1, 1, "The Name of the Game", "Hughie Campbell's life is shattered when a superhero kills his girlfriend.")
            )
        ),
        MediaItem(
            id = "tt11198330",
            title = "House of the Dragon",
            type = "series",
            poster = "https://images.metahub.space/poster/medium/tt11198330/img",
            backdrop = "https://images.metahub.space/background/medium/tt11198330/img",
            year = "2022-2025",
            rating = "8.5",
            description = "An internal succession war within House Targaryen at the height of its power, 172 years before the birth of Daenerys Targaryen.",
            genres = listOf("Action", "Adventure", "Drama"),
            cast = listOf("Matt Smith", "Emma D'Arcy", "Olivia Cooke"),
            director = listOf("Ryan J. Condal"),
            runtime = "62 min",
            serviceProvider = "HBO Max",
            episodes = listOf(
                Episode("tt11198330:1:1", 1, 1, "The Heirs of the Dragon", "Viserys hosts a tournament to celebrate the birth of his second child.")
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
            runtime = "24 min",
            serviceProvider = "Crunchyroll"
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
            runtime = "23 min",
            serviceProvider = "Crunchyroll"
        ),
        MediaItem(
            id = "tt9335498",
            title = "Demon Slayer: Kimetsu no Yaiba",
            type = "anime",
            poster = "https://images.metahub.space/poster/medium/tt9335498/img",
            backdrop = "https://images.metahub.space/background/medium/tt9335498/img",
            year = "2019-2024",
            rating = "8.6",
            description = "A family is attacked by demons and only two members survive - Tanjiro and his sister Nezuko, who is turning into a demon herself.",
            genres = listOf("Animation", "Action", "Fantasy"),
            cast = listOf("Natsuki Hanae", "Akari Kito", "Hiro Shimono"),
            director = listOf("Haruo Sotozaki"),
            runtime = "24 min",
            serviceProvider = "Crunchyroll"
        ),
        MediaItem(
            id = "tt13616990",
            title = "Chainsaw Man",
            type = "anime",
            poster = "https://images.metahub.space/poster/medium/tt13616990/img",
            backdrop = "https://images.metahub.space/background/medium/tt13616990/img",
            year = "2022-2024",
            rating = "8.4",
            description = "Following a betrayal, a young man left for the dead is reborn as a powerful devil-human hybrid after merging with his pet devil Pochita.",
            genres = listOf("Animation", "Action", "Fantasy"),
            cast = listOf("Kikunosuke Toya", "Tomori Kusunoki", "Shogo Sakata"),
            director = listOf("Ryu Nakayama"),
            runtime = "24 min",
            serviceProvider = "Crunchyroll"
        )
    )

    // ==========================================
    // STREAMING SERVICES CATALOGS
    // ==========================================
    val streamingServices = listOf(
        StreamingService("all", "All Services", "ALL", 0xFF6C5CE7, "Aggregated Global Stream Directory"),
        StreamingService("netflix", "Netflix", "N", 0xFFE50914, "Stranger Things, Arcane & Inception"),
        StreamingService("hbo", "HBO Max", "MAX", 0xFF9900FA, "House of the Dragon, Dune 2 & The Dark Knight"),
        StreamingService("disney", "Disney+", "D+", 0xFF113CCF, "The Mandalorian, Interstellar & Marvel"),
        StreamingService("apple", "Apple TV+", "tv+", 0xFF555555, "Severance, Ted Lasso & Oppenheimer"),
        StreamingService("prime", "Prime Video", "prime", 0xFF00A8E1, "The Boys, Breaking Bad & Fallout"),
        StreamingService("crunchyroll", "Crunchyroll", "CR", 0xFFF47521, "Attack on Titan, Jujutsu Kaisen & Demon Slayer")
    )

    fun getStreamingServices(): List<StreamingService> = streamingServices

    fun getMediaForStreamingService(serviceId: String): List<MediaItem> {
        val all = curatedMovies + curatedSeries + curatedAnime
        return when (serviceId.lowercase()) {
            "netflix" -> all.filter { it.serviceProvider == "Netflix" }
            "hbo" -> all.filter { it.serviceProvider == "HBO Max" }
            "disney" -> all.filter { it.serviceProvider == "Disney+" }
            "apple" -> all.filter { it.serviceProvider == "Apple TV+" }
            "prime" -> all.filter { it.serviceProvider == "Prime Video" }
            "crunchyroll" -> all.filter { it.serviceProvider == "Crunchyroll" }
            else -> all
        }
    }

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

    // Recommendation Engine: Similar titles based on shared genres/directors
    fun getSimilarMedia(media: MediaItem): List<MediaItem> {
        val all = curatedMovies + curatedSeries + curatedAnime
        return all.filter { it.id != media.id }
            .sortedByDescending { other ->
                val genreOverlap = other.genres.intersect(media.genres.toSet()).size
                val dirOverlap = if (other.director.intersect(media.director.toSet()).isNotEmpty()) 2 else 0
                val typeMatch = if (other.type == media.type) 1 else 0
                genreOverlap + dirOverlap + typeMatch
            }
            .take(6)
    }

    // Cast & Crew Profiles
    fun getCastProfilesForMedia(media: MediaItem): List<com.example.data.model.CastMember> {
        return media.cast.mapIndexed { idx, actorName ->
            com.example.data.model.CastMember(
                id = "${media.id}_cast_$idx",
                name = actorName,
                role = when (idx) {
                    0 -> "Lead Protagonist"
                    1 -> "Co-Lead"
                    2 -> "Supporting Role"
                    else -> "Key Ensemble"
                },
                avatarUrl = "https://images.metahub.space/poster/small/${media.id}/img",
                bio = "$actorName is an acclaimed actor known for captivating performances in dramatic and cinematic feature films."
            )
        }
    }

    // Offline Downloads State
    private val _downloads = MutableStateFlow(
        listOf(
            com.example.data.model.OfflineDownload(
                id = "dl_inception_1080p",
                mediaId = "tt1375666",
                title = "Inception",
                poster = "https://images.metahub.space/poster/medium/tt1375666/img",
                quality = "1080p",
                sizeText = "2.8 GB",
                progressPercent = 1.0f,
                isCompleted = true,
                downloadSpeed = "Completed",
                localFilePath = "/storage/emulated/0/Movies/Inception_1080p.mp4"
            ),
            com.example.data.model.OfflineDownload(
                id = "dl_dune2_4k",
                mediaId = "tt15239678",
                title = "Dune: Part Two",
                poster = "https://images.metahub.space/poster/medium/tt15239678/img",
                quality = "4K HDR",
                sizeText = "14.2 GB",
                progressPercent = 0.68f,
                isCompleted = false,
                downloadSpeed = "18.5 MB/s"
            )
        )
    )
    val downloads: StateFlow<List<com.example.data.model.OfflineDownload>> = _downloads.asStateFlow()

    fun startDownload(media: MediaItem, stream: MediaStream) {
        val newDownload = com.example.data.model.OfflineDownload(
            id = "dl_${media.id}_${System.currentTimeMillis()}",
            mediaId = media.id,
            title = media.title,
            poster = media.poster,
            quality = stream.quality,
            sizeText = if (stream.sizeText.isNotEmpty()) stream.sizeText else "2.1 GB",
            progressPercent = 0.05f,
            isCompleted = false,
            downloadSpeed = "16.8 MB/s"
        )
        _downloads.value = listOf(newDownload) + _downloads.value
    }

    fun deleteDownload(downloadId: String) {
        _downloads.value = _downloads.value.filter { it.id != downloadId }
    }

    // Custom Collections State
    private val _collections = MutableStateFlow(
        listOf(
            com.example.data.model.CustomCollection(
                id = "col_scifi",
                name = "Sci-Fi & Cyberpunk Gems",
                description = "Mind-bending reality, artificial intelligence, and space exploration epics.",
                itemIds = listOf("tt1375666", "tt0816692", "tt15239678", "tt11126994"),
                coverPoster = "https://images.metahub.space/poster/medium/tt0816692/img"
            ),
            com.example.data.model.CustomCollection(
                id = "col_oscar",
                name = "Academy Award Winners",
                description = "Masterful cinematic storytelling and award-winning masterpieces.",
                itemIds = listOf("tt15398776", "tt0468569", "tt1375666"),
                coverPoster = "https://images.metahub.space/poster/medium/tt15398776/img"
            )
        )
    )
    val collections: StateFlow<List<com.example.data.model.CustomCollection>> = _collections.asStateFlow()

    fun createCollection(name: String, description: String) {
        val newCol = com.example.data.model.CustomCollection(
            id = "col_${System.currentTimeMillis()}",
            name = name,
            description = description,
            itemIds = emptyList(),
            coverPoster = "https://images.metahub.space/poster/medium/tt1375666/img"
        )
        _collections.value = _collections.value + newCol
    }

    fun toggleItemInCollection(collectionId: String, mediaId: String) {
        _collections.value = _collections.value.map { col ->
            if (col.id == collectionId) {
                val updatedIds = if (col.itemIds.contains(mediaId)) {
                    col.itemIds - mediaId
                } else {
                    col.itemIds + mediaId
                }
                col.copy(itemIds = updatedIds)
            } else col
        }
    }

    // Add-on Network Latency Diagnostic
    fun checkAddonHealth(): List<com.example.data.model.AddonHealth> {
        return listOf(
            com.example.data.model.AddonHealth("org.stremio.cinemeta", "Cinemeta Official", 34, true, "Optimal (v3.0.12)"),
            com.example.data.model.AddonHealth("org.stremio.torrentio", "Torrentio Debrid", 68, true, "Operational"),
            com.example.data.model.AddonHealth("org.stremio.opensubtitles", "OpenSubtitles v3", 42, true, "Fast"),
            com.example.data.model.AddonHealth("org.stremio.openstream", "Public Domain CDN", 29, true, "Direct CDN (99.9%)"),
            com.example.data.model.AddonHealth("org.stremio.cyberflix", "CyberFlix Aggregator", 92, true, "Connected")
        )
    }

    // ==========================================
    // CALENDAR & UP NEXT EPISODES
    // ==========================================
    private val _upcomingEpisodes = MutableStateFlow(
        listOf(
            UpcomingEpisode(
                id = "up_arcane_s2e1",
                mediaId = "tt11126994",
                seriesTitle = "Arcane",
                seasonNumber = 2,
                episodeNumber = 1,
                episodeTitle = "Heavy Is the Crown",
                airDate = "Today, 8:00 PM EST",
                dayOfWeek = "Today",
                thumbnail = "https://images.metahub.space/background/medium/tt11126994/img",
                poster = "https://images.metahub.space/poster/medium/tt11126994/img",
                overview = "The aftermath of the Council attack plunges Piltover into emergency protocol while Zaun mobilizes."
            ),
            UpcomingEpisode(
                id = "up_dandadan_s1e4",
                mediaId = "tt30324765",
                seriesTitle = "Dandadan",
                seasonNumber = 1,
                episodeNumber = 4,
                episodeTitle = "Turbo Granny's Curse",
                airDate = "Today, 11:30 PM",
                dayOfWeek = "Today",
                thumbnail = "https://images.metahub.space/background/medium/tt11126994/img",
                poster = "https://images.metahub.space/poster/medium/tt11126994/img",
                overview = "Momo and Okarun struggle to break the curse before the subterranean spirit awakening."
            ),
            UpcomingEpisode(
                id = "up_bleach_s3e3",
                mediaId = "tt0903747",
                seriesTitle = "Bleach: Thousand-Year Blood War",
                seasonNumber = 3,
                episodeNumber = 3,
                episodeTitle = "The Dark Armistice",
                airDate = "Tomorrow, 7:00 AM",
                dayOfWeek = "Tomorrow",
                thumbnail = "https://images.metahub.space/background/medium/tt0903747/img",
                poster = "https://images.metahub.space/poster/medium/tt0903747/img",
                overview = "Squad Zero faces the elite Royal Guard inside the Soul King palace sanctuary."
            ),
            UpcomingEpisode(
                id = "up_tlou_s2e1",
                mediaId = "tt15239678",
                seriesTitle = "The Last of Us",
                seasonNumber = 2,
                episodeNumber = 1,
                episodeTitle = "Jackson Haven",
                airDate = "In 3 Days (Friday)",
                dayOfWeek = "Friday",
                thumbnail = "https://images.metahub.space/background/medium/tt15239678/img",
                poster = "https://images.metahub.space/poster/medium/tt15239678/img",
                overview = "Five years after their dangerous journey across post-pandemic America, Joel and Ellie settle down in Jackson."
            ),
            UpcomingEpisode(
                id = "up_st5_s5e1",
                mediaId = "tt0816692",
                seriesTitle = "Stranger Things",
                seasonNumber = 5,
                episodeNumber = 1,
                episodeTitle = "The Crawl",
                airDate = "Sunday, 9:00 PM",
                dayOfWeek = "Sunday",
                thumbnail = "https://images.metahub.space/background/medium/tt0816692/img",
                poster = "https://images.metahub.space/poster/medium/tt0816692/img",
                overview = "The dimensional fissure expands over Hawkins as the party prepares for their final confrontation."
            )
        )
    )
    val upcomingEpisodes: StateFlow<List<UpcomingEpisode>> = _upcomingEpisodes.asStateFlow()

    fun toggleEpisodeWatched(episodeId: String) {
        _upcomingEpisodes.value = _upcomingEpisodes.value.map { ep ->
            if (ep.id == episodeId) ep.copy(isWatched = !ep.isWatched) else ep
        }
    }

    fun toggleEpisodeReminder(episodeId: String) {
        _upcomingEpisodes.value = _upcomingEpisodes.value.map { ep ->
            if (ep.id == episodeId) ep.copy(hasReminder = !ep.hasReminder) else ep
        }
    }

    // ==========================================
    // WATCH STATS & TRAKT ANALYTICS
    // ==========================================
    private val _watchStats = MutableStateFlow(WatchStats())
    val watchStats: StateFlow<WatchStats> = _watchStats.asStateFlow()

    fun toggleTraktScrobble(enabled: Boolean) {
        _watchStats.value = _watchStats.value.copy(traktScrobbleEnabled = enabled)
    }

    fun syncTraktCloud() {
        val current = _watchStats.value
        _watchStats.value = current.copy(
            totalWatchHours = current.totalWatchHours + 2,
            episodesBingedCount = current.episodesBingedCount + 3,
            bingeStreakDays = current.bingeStreakDays + 1
        )
    }

    // ==========================================
    // RATINGS & COMMUNITY REVIEWS
    // ==========================================
    private val _communityReviews = MutableStateFlow(
        listOf(
            CommunityReview(
                id = "rev_1",
                mediaId = "tt1375666", // Inception
                userName = "CinemaVoyager",
                rating = 9.8f,
                date = "2 days ago",
                headline = "A masterclass in narrative architecture",
                content = "Nolan balances dream layers with mathematical precision. Hans Zimmer's score drives relentless emotional momentum that still holds up on repeat viewings.",
                helpfulCount = 142,
                tags = listOf("Visual Masterpiece", "Mind Bending", "Peak Nolan")
            ),
            CommunityReview(
                id = "rev_2",
                mediaId = "tt1375666",
                userName = "FilmBuff_99",
                rating = 9.2f,
                date = "1 week ago",
                headline = "Still the gold standard of original sci-fi",
                content = "The practical hallway gravity fight sequence alone makes this an all-time classic. Sound design in Dolby Atmos is unmatched.",
                helpfulCount = 89,
                tags = listOf("Great Soundtrack", "Instant Classic")
            ),
            CommunityReview(
                id = "rev_3",
                mediaId = "tt15239678", // Dune 2
                userName = "ArakisDreamer",
                rating = 9.7f,
                date = "Yesterday",
                headline = "Spectacular cinematic scale and sound",
                content = "Villeneuve delivers a sensory triumph. The worm-riding sequence and the black-and-white Giedi Prime arena scenes are pure cinema.",
                helpfulCount = 210,
                tags = listOf("Epic Scale", "Visually Stunning")
            ),
            CommunityReview(
                id = "rev_4",
                mediaId = "tt11126994", // Arcane
                userName = "ZaunEnthusiast",
                rating = 9.9f,
                date = "3 days ago",
                headline = "Animation redefined for a generation",
                content = "Fortiche's painted aesthetic paired with raw character writing makes this one of the greatest animated television series ever created.",
                helpfulCount = 330,
                tags = listOf("Peak Fiction", "Must Watch")
            )
        )
    )
    val communityReviews: StateFlow<List<CommunityReview>> = _communityReviews.asStateFlow()

    fun getCriticScoresForMedia(mediaId: String): CriticScores {
        return when (mediaId) {
            "tt1375666" -> CriticScores("8.8", "2.5M", 87, 91, 74)
            "tt15239678" -> CriticScores("8.6", "480K", 92, 95, 79)
            "tt15398776" -> CriticScores("8.9", "740K", 93, 91, 88)
            "tt0816692" -> CriticScores("8.7", "2.1M", 73, 86, 74)
            "tt0468569" -> CriticScores("9.0", "2.9M", 94, 94, 84)
            "tt0903747" -> CriticScores("9.5", "2.2M", 96, 98, 87)
            "tt11126994" -> CriticScores("9.0", "280K", 100, 96, 89)
            else -> CriticScores("8.5", "350K", 89, 92, 80)
        }
    }

    fun getReviewsForMedia(mediaId: String): List<CommunityReview> {
        val filtered = _communityReviews.value.filter { it.mediaId == mediaId }
        return if (filtered.isNotEmpty()) filtered else listOf(
            CommunityReview(
                id = "rev_default_${mediaId}",
                mediaId = mediaId,
                userName = "StremioExplorer",
                rating = 9.0f,
                date = "Recently",
                headline = "Fantastic stream quality and pacing",
                content = "Watched in crisp 4K with 5.1 surround sound enabled. Dialogue clarity setting made the voices crystal clear.",
                helpfulCount = 47,
                tags = listOf("Great Quality", "Recommended")
            )
        )
    }

    fun addCommunityReview(mediaId: String, rating: Float, headline: String, content: String, isSpoiler: Boolean, tags: List<String>) {
        val newReview = CommunityReview(
            id = "rev_${System.currentTimeMillis()}",
            mediaId = mediaId,
            userName = "You (Verified)",
            rating = rating,
            date = "Just now",
            headline = headline,
            content = content,
            isSpoiler = isSpoiler,
            helpfulCount = 1,
            tags = tags
        )
        _communityReviews.value = listOf(newReview) + _communityReviews.value
    }

    // ==========================================
    // DEBRID PROVIDER & CLOUD TORRENTS
    // ==========================================
    private val _debridAccount = MutableStateFlow(DebridAccount())
    val debridAccount: StateFlow<DebridAccount> = _debridAccount.asStateFlow()

    private val _cloudTorrents = MutableStateFlow(
        listOf(
            CloudTorrent(
                id = "ct_1",
                name = "Arcane.S02E01.2160p.HDR.DDP5.1.Atmos",
                size = "4.2 GB",
                progressPercent = 1.0f,
                status = "Cached 100% (Instant)",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            CloudTorrent(
                id = "ct_2",
                name = "Dune.Part.Two.2024.IMAX.4K.REMUX.DV",
                size = "28.4 GB",
                progressPercent = 1.0f,
                status = "Cached 100% (Instant)",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            ),
            CloudTorrent(
                id = "ct_3",
                name = "Interstellar.2014.1080p.BluRay.x265.10bit",
                size = "3.8 GB",
                progressPercent = 0.85f,
                status = "Downloading (42 MB/s)",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            )
        )
    )
    val cloudTorrents: StateFlow<List<CloudTorrent>> = _cloudTorrents.asStateFlow()

    fun updateDebridAccount(updated: DebridAccount) {
        _debridAccount.value = updated
    }

    fun addMagnetToDebrid(magnetUri: String, name: String = "Manual Magnet Stream") {
        val newTorrent = CloudTorrent(
            id = "ct_${System.currentTimeMillis()}",
            name = if (name.isNotBlank()) name else "Direct.Torrent.Cached.${System.currentTimeMillis() % 1000}",
            size = "3.4 GB",
            progressPercent = 1.0f,
            status = "Cached 100% (Instant)",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        )
        _cloudTorrents.value = listOf(newTorrent) + _cloudTorrents.value
    }

    // ==========================================
    // PARENTAL CONTROLS & CONTENT FILTER
    // ==========================================
    private val _kidsModeEnabled = MutableStateFlow(false)
    val kidsModeEnabled: StateFlow<Boolean> = _kidsModeEnabled.asStateFlow()

    private val _parentalPin = MutableStateFlow("1234")
    val parentalPin: StateFlow<String> = _parentalPin.asStateFlow()

    fun toggleKidsMode(enabled: Boolean) {
        _kidsModeEnabled.value = enabled
    }

    fun setParentalPin(pin: String) {
        _parentalPin.value = pin
    }
}
