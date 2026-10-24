package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import com.example.data.model.WatchlistItem
import com.example.ui.MainViewModel
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.HeroBanner
import com.example.ui.components.MediaCatalogRow
import com.example.ui.components.MediaPosterCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceDark
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
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val featuredItem by viewModel.featuredItem.collectAsStateWithLifecycle()
    val trending by viewModel.trendingList.collectAsStateWithLifecycle()
    val movies by viewModel.popularMovies.collectAsStateWithLifecycle()
    val series by viewModel.popularSeries.collectAsStateWithLifecycle()
    val anime by viewModel.animeList.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()

    val continueWatchingItems = watchlist.filter { it.progressPercent in 0.01f..0.98f }

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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
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
                        text = "Stremio Add-on Hub",
                        color = SecondaryCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Status Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariantDark)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Cinemeta Online",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search movies, TV shows, anime, genres…", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (searchQuery.isNotEmpty()) SecondaryCyan else TextMuted
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextMuted
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceVariantDark,
                unfocusedContainerColor = SurfaceVariantDark,
                disabledContainerColor = SurfaceVariantDark,
                focusedIndicatorColor = PrimaryViolet,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("search_text_field")
        )

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("all" to "All", "movie" to "Movies", "series" to "Series", "anime" to "Anime").forEach { (key, label) ->
                val isSelected = filter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setFilter(key) },
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

        // Search Results View OR Normal Catalogs View
        if (searchQuery.isNotBlank()) {
            if (isSearching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryViolet)
                }
            } else if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No results for \"$searchQuery\"",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching for titles like Inception, Dune, or Arcane",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(130.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults, key = { it.id }) { item ->
                        MediaPosterCard(
                            item = item,
                            onClick = { viewModel.openMediaDetail(item) }
                        )
                    }
                }
            }
        } else {
            // Main Feed
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
                                text = "Continue Watching",
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
                                            val fullMedia = (trending + movies + series).find { it.id == watchItem.mediaId }
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

                // Trending Catalog
                if (filter == "all" || filter == "movie") {
                    item {
                        MediaCatalogRow(
                            title = "Trending Now",
                            items = trending,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // Popular Movies Catalog
                if (filter == "all" || filter == "movie") {
                    item {
                        MediaCatalogRow(
                            title = "Popular Movies",
                            items = movies,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // Popular Series Catalog
                if (filter == "all" || filter == "series") {
                    item {
                        MediaCatalogRow(
                            title = "Top TV Series",
                            items = series,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }

                // Anime Catalog
                if (filter == "all" || filter == "anime") {
                    item {
                        MediaCatalogRow(
                            title = "Anime & Animation",
                            items = anime,
                            onItemClick = { viewModel.openMediaDetail(it) }
                        )
                    }
                }
            }
        }
    }
}
