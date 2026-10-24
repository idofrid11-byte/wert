package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CastMember
import com.example.data.model.CustomCollection
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaStream
import com.example.ui.components.MediaPosterCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GoldRating
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MediaDetailSheet(
    media: MediaItem,
    streams: List<MediaStream>,
    similarMedia: List<MediaItem>,
    castProfiles: List<CastMember>,
    collections: List<CustomCollection>,
    isWatchlisted: Boolean,
    onClose: () -> Unit,
    onPlayDefault: () -> Unit,
    onPlayStream: (MediaStream) -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onPlayTrailer: () -> Unit,
    onDownloadStream: (MediaStream) -> Unit,
    onToggleWatchlist: () -> Unit,
    onSelectSimilar: (MediaItem) -> Unit,
    onAddToCollection: (collectionId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedCastMember by remember { mutableStateOf<CastMember?>(null) }
    var showCollectionPicker by remember { mutableStateOf(false) }

    val isSeries = media.type.equals("series", ignoreCase = true)
    val tabs = if (isSeries) listOf("Streams", "Episodes", "Overview") else listOf("Streams", "Overview")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("media_detail_sheet")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Backdrop with Badges & Trailer Action
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(SurfaceDark)
                ) {
                    AsyncImage(
                        model = media.backdrop ?: media.poster,
                        contentDescription = media.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        BackgroundDark
                                    )
                                )
                            )
                    )

                    // Close Button
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .testTag("detail_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextPrimary
                        )
                    }

                    // Trailer Pill Button Overlay
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .clickable(onClick = onPlayTrailer)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Trailer",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Watch Trailer",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Media Info & Actions
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Poster Card
                    Card(
                        modifier = Modifier
                            .width(100.dp)
                            .height(150.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        AsyncImage(
                            model = media.poster ?: media.backdrop,
                            contentDescription = media.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Metadata
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = media.title,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = GoldRating,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = media.rating,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = media.year,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            if (media.runtime.isNotEmpty()) {
                                Text(
                                    text = "• ${media.runtime}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (media.genres.isNotEmpty()) {
                            Text(
                                text = media.genres.joinToString(" • "),
                                color = SecondaryCyan,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Watch, Watchlist, Add to Collection
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onPlayDefault,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("detail_play_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Watch", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = onToggleWatchlist,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("detail_watchlist_button"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = if (isWatchlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Watchlist",
                                    tint = if (isWatchlisted) SecondaryCyan else TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            OutlinedButton(
                                onClick = { showCollectionPicker = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("detail_collection_button"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlaylistAdd,
                                    contentDescription = "Collection",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Tabs Row
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = SurfaceDark,
                    contentColor = TextPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = PrimaryViolet
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = tabTitle,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) TextPrimary else TextMuted
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // STREAMS TAB with Direct Play & Download Buttons
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "AVAILABLE STREAM SOURCES (${streams.size})",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    items(streams, key = { it.id }) { stream ->
                        StreamItemCard(
                            stream = stream,
                            onClick = { onPlayStream(stream) },
                            onDownload = { onDownloadStream(stream) }
                        )
                    }
                }

                1 -> {
                    if (isSeries) {
                        // EPISODES TAB
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "SEASON 1 (${media.episodes.size} EPISODES)",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        items(media.episodes, key = { it.id }) { episode ->
                            EpisodeItemCard(
                                episode = episode,
                                onClick = { onPlayEpisode(episode) }
                            )
                        }
                    } else {
                        // OVERVIEW TAB FOR MOVIES
                        item {
                            OverviewSection(
                                media = media,
                                castProfiles = castProfiles,
                                onCastClick = { selectedCastMember = it }
                            )
                        }
                    }
                }

                2 -> {
                    // OVERVIEW TAB FOR SERIES
                    item {
                        OverviewSection(
                            media = media,
                            castProfiles = castProfiles,
                            onCastClick = { selectedCastMember = it }
                        )
                    }
                }
            }

            // SIMILAR / MORE LIKE THIS CAROUSEL
            if (similarMedia.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "More Like This",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(similarMedia, key = { it.id }) { item ->
                                MediaPosterCard(
                                    item = item,
                                    onClick = { onSelectSimilar(item) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // CAST PROFILE DIALOG
        selectedCastMember?.let { member ->
            CastProfileDialog(
                member = member,
                onDismiss = { selectedCastMember = null }
            )
        }

        // COLLECTION PICKER DIALOG
        if (showCollectionPicker) {
            AlertDialog(
                onDismissRequest = { showCollectionPicker = false },
                containerColor = SurfaceDark,
                title = {
                    Text(
                        text = "Add to Collection",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select a collection for \"${media.title}\":", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        collections.forEach { col ->
                            val isIn = col.itemIds.contains(media.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isIn) PrimaryViolet.copy(alpha = 0.2f) else SurfaceVariantDark)
                                    .clickable { onAddToCollection(col.id) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(col.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                if (isIn) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "In collection", tint = SecondaryCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showCollectionPicker = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet)
                    ) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
private fun StreamItemCard(
    stream: MediaStream,
    onClick: () -> Unit,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(onClick = onClick)
            .testTag("stream_${stream.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quality badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (stream.quality) {
                                "4K" -> PrimaryViolet
                                "1080p" -> SecondaryCyan.copy(alpha = 0.2f)
                                else -> SurfaceHighlight
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stream.quality,
                        color = when (stream.quality) {
                            "4K" -> TextPrimary
                            "1080p" -> SecondaryCyan
                            else -> TextSecondary
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stream.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${stream.addonName}",
                            color = SecondaryCyan,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stream.title,
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (stream.sizeText.isNotEmpty()) {
                    Text(
                        text = stream.sizeText,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = onDownload,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Play Stream",
                        tint = PrimaryViolet,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeItemCard(
    episode: Episode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(onClick = onClick)
            .testTag("episode_${episode.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceHighlight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${episode.number}",
                    color = SecondaryCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (episode.overview.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = episode.overview,
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Episode",
                tint = PrimaryViolet,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun OverviewSection(
    media: MediaItem,
    castProfiles: List<CastMember>,
    onCastClick: (CastMember) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "SYNOPSIS",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = if (media.description.isNotEmpty()) media.description else "No synopsis available for this media.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        if (media.director.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "DIRECTOR",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = media.director.joinToString(", "),
                color = TextPrimary,
                fontSize = 13.sp
            )
        }

        // Clickable Cast Members List
        if (castProfiles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "STARRING CAST (TAP TO VIEW BIO)",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(castProfiles, key = { it.id }) { actor ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceHighlight)
                            .clickable { onCastClick(actor) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(PrimaryViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(actor.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(actor.role, color = SecondaryCyan, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CastProfileDialog(
    member: CastMember,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(member.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(member.role, color = SecondaryCyan, fontSize = 12.sp)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(member.bio, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Featured in this title and available in Cinemeta official catalogs.", color = TextMuted, fontSize = 11.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet)
            ) {
                Text("Close")
            }
        }
    )
}
