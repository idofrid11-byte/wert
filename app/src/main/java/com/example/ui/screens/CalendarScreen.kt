package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.MediaItem
import com.example.data.model.UpcomingEpisode
import com.example.ui.MainViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CalendarScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val upcomingEpisodes by viewModel.upcomingEpisodes.collectAsStateWithLifecycle()
    val trendingList by viewModel.trendingList.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Up Next, 1: Weekly Airing, 2: Premiere Radar
    var selectedDay by remember { mutableStateOf("Today") }

    val daysOfWeek = listOf("Today", "Tomorrow", "Friday", "Saturday", "Sunday")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("calendar_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Release Calendar",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Live airing schedule, upcoming episodes & air dates",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendar",
                    tint = SecondaryCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = SurfaceDark,
            contentColor = PrimaryViolet,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = PrimaryViolet
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {
            listOf("Up Next", "Weekly Schedule", "Premiere Radar").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) TextPrimary else TextMuted,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.testTag("calendar_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTabIndex) {
            0 -> {
                // Up Next Episodes Queue
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Episodes Waiting for You",
                                color = SecondaryCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${upcomingEpisodes.count { !it.isWatched }} pending",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    items(upcomingEpisodes, key = { it.id }) { ep ->
                        UpcomingEpisodeCard(
                            episode = ep,
                            onWatchClick = {
                                // Find media item and play default
                                val media = trendingList.find { it.id == ep.mediaId }
                                if (media != null) {
                                    viewModel.playMediaDefault(media)
                                } else if (trendingList.isNotEmpty()) {
                                    viewModel.playMediaDefault(trendingList.first())
                                }
                            },
                            onToggleWatched = {
                                viewModel.toggleEpisodeWatched(ep.id)
                                Toast.makeText(
                                    context,
                                    if (!ep.isWatched) "Marked as Watched" else "Marked as Unwatched",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onToggleReminder = {
                                viewModel.toggleEpisodeReminder(ep.id)
                                Toast.makeText(
                                    context,
                                    if (!ep.hasReminder) "Reminder set for ${ep.airDate}" else "Reminder removed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }

            1 -> {
                // Weekly Airing Schedule
                Column(modifier = Modifier.fillMaxSize()) {
                    // Day Selector Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(daysOfWeek) { day ->
                            val isSelected = selectedDay == day
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PrimaryViolet else SurfaceVariantDark)
                                    .clickable { selectedDay = day }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val filteredEpisodes = upcomingEpisodes.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }

                    if (filteredEpisodes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No scheduled airs for $selectedDay", color = TextMuted, fontSize = 13.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(filteredEpisodes, key = { it.id }) { ep ->
                                UpcomingEpisodeCard(
                                    episode = ep,
                                    onWatchClick = {
                                        val media = trendingList.find { it.id == ep.mediaId }
                                        if (media != null) {
                                            viewModel.playMediaDefault(media)
                                        } else if (trendingList.isNotEmpty()) {
                                            viewModel.playMediaDefault(trendingList.first())
                                        }
                                    },
                                    onToggleWatched = { viewModel.toggleEpisodeWatched(ep.id) },
                                    onToggleReminder = { viewModel.toggleEpisodeReminder(ep.id) }
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Season Premiere Radar
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Upcoming Major Releases", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Keep track of premiere dates for the most anticipated shows and digital movie releases.", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }

                    items(
                        listOf(
                            Triple("Stranger Things (Season 5)", "November 2026", "The Hawkins finale"),
                            Triple("The Last of Us (Season 2)", "Spring 2026", "Jackson sanctuary conflict"),
                            Triple("Dune: Prophecy", "December 2026", "Origin of the Bene Gesserit"),
                            Triple("Avatar: Fire & Ash", "December 2026", "The Ash People expedition")
                        )
                    ) { (title, releaseWindow, tagline) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = tagline, color = TextMuted, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Window: $releaseWindow", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Added to Premiere Tracker", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceHighlight),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Track", color = TextPrimary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingEpisodeCard(
    episode: UpcomingEpisode,
    onWatchClick: () -> Unit,
    onToggleWatched: () -> Unit,
    onToggleReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
        modifier = modifier
            .fillMaxWidth()
            .testTag("upcoming_card_${episode.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail / Poster
                Box(
                    modifier = Modifier
                        .size(width = 85.dp, height = 60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                ) {
                    AsyncImage(
                        model = episode.thumbnail ?: episode.poster,
                        contentDescription = episode.seriesTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = episode.seriesTitle,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "S${episode.seasonNumber} E${episode.episodeNumber}: ${episode.episodeTitle}",
                        color = SecondaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryViolet, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = episode.airDate,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (episode.overview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = episode.overview,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Mark Watched Toggle
                    OutlinedButton(
                        onClick = onToggleWatched,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = if (episode.isWatched) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            tint = if (episode.isWatched) Color(0xFF00E676) else TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (episode.isWatched) "Watched" else "Check In",
                            color = if (episode.isWatched) Color(0xFF00E676) else TextPrimary,
                            fontSize = 11.sp
                        )
                    }

                    // Reminder Button
                    IconButton(
                        onClick = onToggleReminder,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (episode.hasReminder) PrimaryViolet.copy(alpha = 0.3f) else SurfaceHighlight)
                    ) {
                        Icon(
                            imageVector = if (episode.hasReminder) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Set Reminder",
                            tint = if (episode.hasReminder) SecondaryCyan else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Play / Stream Episode
                Button(
                    onClick = onWatchClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stream", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
