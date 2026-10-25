package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MediaItem
import com.example.data.model.StreamingService
import com.example.ui.MainViewModel
import com.example.ui.components.CatalogSearchBar
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.HeroBanner
import com.example.ui.components.MediaCatalogRow
import com.example.ui.components.MediaPosterCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val filter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedServiceId by viewModel.selectedStreamingService.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val featuredItem by viewModel.featuredItem.collectAsStateWithLifecycle()
    val trending by viewModel.trendingList.collectAsStateWithLifecycle()
    val movies by viewModel.popularMovies.collectAsStateWithLifecycle()
    val series by viewModel.popularSeries.collectAsStateWithLifecycle()
    val anime by viewModel.animeList.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()

    var selectedGenre by remember { mutableStateOf("All") }

    val continueWatchingItems = watchlist.filter { it.progressPercent in 0.01f..0.98f }

    val allCatalogsCombined = remember(trending, movies, series, anime) {
        (trending + movies + series + anime).distinctBy { it.id }
    }

    val streamingServices = viewModel.streamingServices

    val currentService = remember(selectedServiceId, streamingServices) {
        streamingServices.find { it.id == selectedServiceId }
    }

    val serviceMediaItems = remember(selectedServiceId) {
        if (selectedServiceId == "all") emptyList()
        else viewModel.getMediaForStreamingService(selectedServiceId)
    }

    val genreFilteredItems = remember(selectedGenre, allCatalogsCombined) {
        if (selectedGenre == "All") emptyList()
        else allCatalogsCombined.filter { item ->
            item.genres.any { it.contains(selectedGenre, ignoreCase = true) }
        }
    }

    val genres = listOf("All", "Sci-Fi", "Action", "Drama", "Animation", "Crime", "Thriller", "Adventure")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("discover_screen")
    ) {
        // App Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Logo",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AetherStream",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Streaming Hub & Addon Catalogs",
                        color = SecondaryCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Top Actions: Surprise Me + Status Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Surprise Me Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryViolet.copy(alpha = 0.25f))
                        .clickable {
                            if (allCatalogsCombined.isNotEmpty()) {
                                val randomPick = allCatalogsCombined.random()
                                viewModel.openMediaDetail(randomPick)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_surprise_me")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Surprise Me",
                        tint = SecondaryCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Random Pick",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Status Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Cinemeta",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Debounced Search Bar Component with Suggestions & History
        CatalogSearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            onSearchSubmitted = { viewModel.submitSearch(it) },
            onClear = { viewModel.clearSearch() },
            isSearching = isSearching,
            searchHistory = searchHistory,
            onHistoryItemClick = { viewModel.submitSearch(it) },
            onRemoveHistoryItem = { viewModel.removeSearchHistoryItem(it) },
            onClearHistory = { viewModel.clearSearchHistory() },
            onSuggestionClick = { viewModel.submitSearch(it) },
            resultCount = if (searchQuery.isNotBlank() && !isSearching) searchResults.size else null,
            placeholderText = "חיפוש סרטים, סדרות, שחקנים, קטלוגים…",
            modifier = Modifier.padding(bottom = 2.dp)
        )

        // Type Filter Pills (All, Movies, Series, Anime)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("all" to "הכל / All", "movie" to "סרטים / Movies", "series" to "סדרות / Series", "anime" to "אנימה / Anime").forEach { (key, label) ->
                val isSelected = filter == key
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.setFilter(key)
                        selectedGenre = "All"
                    },
                    label = {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryViolet,
                        selectedLabelColor = TextPrimary,
                        containerColor = SurfaceVariantDark,
                        labelColor = TextSecondary
                    ),
                    border = null,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("filter_$key")
                )
            }
        }

        // ==========================================
        // STREAMING SERVICES CATALOG SELECTOR ROW
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = SecondaryCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "קטלוגי סטרימינג / Streaming Catalogs",
                        color = SecondaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                if (selectedServiceId != "all") {
                    Text(
                        text = "הצג הכל (Reset)",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { viewModel.selectStreamingService("all") }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(streamingServices) { service ->
                    val isServiceSelected = selectedServiceId == service.id
                    val serviceColor = Color(service.brandColorHex)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isServiceSelected) serviceColor.copy(alpha = 0.35f)
                                else SurfaceDark
                            )
                            .border(
                                width = if (isServiceSelected) 1.5.dp else 1.dp,
                                color = if (isServiceSelected) serviceColor else SurfaceHighlight,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.selectStreamingService(service.id)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("service_catalog_${service.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(serviceColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = service.badgeShort,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = service.name,
                                color = if (isServiceSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isServiceSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Search Results View OR Genre Filter Grid OR Dedicated Service View OR Main Feed
        if (searchQuery.isNotBlank()) {
            val filteredSearchResults = remember(searchResults, filter) {
                if (filter == "all") searchResults
                else searchResults.filter { it.type == filter }
            }

            if (isSearching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SecondaryCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "מחפש בקטלוגים... / Searching...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "לא נמצאו תוצאות עבור \"$searchQuery\"",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "נסה לחפש לפי שם סרט, סדרה, שחקן, במאי או שירות סטרימינג (כגון Stranger Things, Dune, או Arcane)",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Stranger Things", "Dune 2", "Arcane").forEach { quickTag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryViolet.copy(alpha = 0.2f))
                                        .border(1.dp, PrimaryViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.submitSearch(quickTag) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(quickTag, color = SecondaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            } else if (filteredSearchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "אין תוצאות בסיווג הנוכחי",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "נמצאו ${searchResults.size} תוצאות בקטגוריות אחרות",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryViolet)
                                .clickable { viewModel.setFilter("all") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("הצג את כל ${searchResults.size} התוצאות", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "נמצאו ${filteredSearchResults.size} תוצאות עבור \"$searchQuery\"",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "נקה",
                            color = SecondaryCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { viewModel.clearSearch() }
                                .padding(4.dp)
                        )
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(130.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSearchResults, key = { it.id }) { item ->
                            MediaPosterCard(
                                item = item,
                                onClick = { viewModel.openMediaDetail(item) }
                            )
                        }
                    }
                }
            }
        } else if (selectedServiceId != "all" && currentService != null) {
            // ==========================================
            // DEDICATED STREAMING SERVICE CATALOG VIEW
            // ==========================================
            val serviceMovies = serviceMediaItems.filter { it.type == "movie" }
            val serviceSeries = serviceMediaItems.filter { it.type == "series" || it.type == "anime" }
            val serviceColor = Color(currentService.brandColorHex)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    // Service Brand Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(serviceColor.copy(alpha = 0.45f), SurfaceVariantDark)
                                )
                            )
                            .border(1.dp, serviceColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(serviceColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentService.badgeShort,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${currentService.name} Official Catalog",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${serviceMediaItems.size} סרטים וסדרות מובילים זמינים להזרמה",
                                        color = SecondaryCyan,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentService.slogan,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Service Popular Movies
                if (serviceMovies.isNotEmpty() && (filter == "all" || filter == "movie")) {
                    item {
                        MediaCatalogRow(
                            title = "סרטים פופולריים ב-${currentService.name} (Popular Movies)",
                            items = serviceMovies,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // Service Popular Series
                if (serviceSeries.isNotEmpty() && (filter == "all" || filter == "series" || filter == "anime")) {
                    item {
                        MediaCatalogRow(
                            title = "סדרות מובילות ב-${currentService.name} (Top Series)",
                            items = serviceSeries,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }
            }
        } else if (selectedGenre != "All") {
            // Genre grid
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$selectedGenre Titles (${genreFilteredItems.size})",
                    color = SecondaryCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(130.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(genreFilteredItems, key = { it.id }) { item ->
                        MediaPosterCard(
                            item = item,
                            onClick = { viewModel.openMediaDetail(item) }
                        )
                    }
                }
            }
        } else {
            // Main Feed with Featured, Continue Watching, and Dedicated Streaming Catalogs
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Featured Hero Banner
                if (filter == "all" || filter == "movie") {
                    featuredItem?.let { item ->
                        item {
                            HeroBanner(
                                item = item,
                                isWatchlisted = viewModel.isWatchlisted(item.id),
                                onPlayClick = { viewModel.playMediaDefault(item) },
                                onDetailsClick = { viewModel.openMediaDetail(item) },
                                onWatchlistToggle = { viewModel.toggleWatchlist(item) }
                            )
                        }
                    }
                }

                // Continue Watching
                if (continueWatchingItems.isNotEmpty() && filter == "all") {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "המשך צפייה (Continue Watching)",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(continueWatchingItems, key = { it.mediaId }) { watchItem ->
                                    ContinueWatchingCard(
                                        item = watchItem,
                                        onResume = {
                                            val fullMedia = allCatalogsCombined.find { it.id == watchItem.mediaId }
                                                ?: MediaItem(
                                                    id = watchItem.mediaId,
                                                    title = watchItem.title,
                                                    type = watchItem.type,
                                                    poster = watchItem.poster,
                                                    backdrop = watchItem.poster,
                                                    year = watchItem.year,
                                                    rating = watchItem.rating,
                                                    description = ""
                                                )
                                            viewModel.playMediaDefault(fullMedia)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // NETFLIX CATALOG ROW
                val netflixItems = allCatalogsCombined.filter { it.serviceProvider == "Netflix" }
                if (netflixItems.isNotEmpty()) {
                    item {
                        MediaCatalogRow(
                            title = "🍿 Netflix: להיטים מובילים וסדרות מקור",
                            items = netflixItems,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // HBO MAX CATALOG ROW
                val hboItems = allCatalogsCombined.filter { it.serviceProvider == "HBO Max" }
                if (hboItems.isNotEmpty()) {
                    item {
                        MediaCatalogRow(
                            title = "👑 HBO Max: שוברי קופות וסדרות מופת",
                            items = hboItems,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // DISNEY+ CATALOG ROW
                val disneyItems = allCatalogsCombined.filter { it.serviceProvider == "Disney+" }
                if (disneyItems.isNotEmpty()) {
                    item {
                        MediaCatalogRow(
                            title = "✨ Disney+: מארוול, מלחמת הכוכבים ודיסני",
                            items = disneyItems,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // APPLE TV+ CATALOG ROW
                val appleItems = allCatalogsCombined.filter { it.serviceProvider == "Apple TV+" }
                if (appleItems.isNotEmpty()) {
                    item {
                        MediaCatalogRow(
                            title = "🍏 Apple TV+: סדרות וסרטים עטורי פרסים",
                            items = appleItems,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // PRIME VIDEO CATALOG ROW
                val primeItems = allCatalogsCombined.filter { it.serviceProvider == "Prime Video" }
                if (primeItems.isNotEmpty()) {
                    item {
                        MediaCatalogRow(
                            title = "📦 Prime Video: הסדרות והסרטים הפופולריים ביותר",
                            items = primeItems,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // PARAMOUNT+ & HULU ROW
                val paramounthuluItems = allCatalogsCombined.filter { it.serviceProvider == "Paramount+" || it.serviceProvider == "Hulu" }
                if (paramounthuluItems.isNotEmpty()) {
                    item {
                        MediaCatalogRow(
                            title = "⚡ Paramount+ & Hulu: להיטי טלוויזיה מובילים",
                            items = paramounthuluItems,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // CRUNCHYROLL ANIME CATALOG ROW
                val crunchyrollItems = allCatalogsCombined.filter { it.serviceProvider == "Crunchyroll" }
                if (crunchyrollItems.isNotEmpty()) {
                    item {
                        MediaCatalogRow(
                            title = "⛩️ Crunchyroll: סדרות אנימה מובילות",
                            items = crunchyrollItems,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // Trending Catalog
                if (filter == "all" || filter == "movie") {
                    item {
                        MediaCatalogRow(
                            title = "Trending Now (כללי)",
                            items = trending,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // Popular Movies Catalog
                if (filter == "all" || filter == "movie") {
                    item {
                        MediaCatalogRow(
                            title = "כל הסרטים הפופולריים (Popular Movies)",
                            items = movies,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // Popular Series Catalog
                if (filter == "all" || filter == "series") {
                    item {
                        MediaCatalogRow(
                            title = "סדרות טלוויזיה מובילות (Top TV Series)",
                            items = series,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }
            }
        }
    }
}
