package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.screens.AddonsScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MediaDetailSheet
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VideoPlayerOverlay
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: MainViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedMedia by viewModel.selectedMedia.collectAsStateWithLifecycle()
    val streams by viewModel.selectedMediaStreams.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val audioSettings by viewModel.audioSettings.collectAsStateWithLifecycle()
    val subtitleSettings by viewModel.subtitleSettings.collectAsStateWithLifecycle()

    // Global back handling
    if (playerState.isOpen) {
        BackHandler { viewModel.closePlayer() }
    } else if (selectedMedia != null) {
        BackHandler { viewModel.closeMediaDetail() }
    } else if (currentTab != AppTab.DISCOVER) {
        BackHandler { viewModel.selectTab(AppTab.DISCOVER) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BackgroundDark,
            bottomBar = {
                if (!playerState.isOpen) {
                    NavigationBar(
                        containerColor = SurfaceDark,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        // Discover
                        NavigationBarItem(
                            selected = currentTab == AppTab.DISCOVER,
                            onClick = { viewModel.selectTab(AppTab.DISCOVER) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == AppTab.DISCOVER) Icons.Filled.Explore else Icons.Outlined.Explore,
                                    contentDescription = "Discover"
                                )
                            },
                            label = { Text("Discover", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = SecondaryCyan,
                                indicatorColor = PrimaryViolet,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_item_discover")
                        )

                        // Library
                        NavigationBarItem(
                            selected = currentTab == AppTab.LIBRARY,
                            onClick = { viewModel.selectTab(AppTab.LIBRARY) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == AppTab.LIBRARY) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Library"
                                )
                            },
                            label = { Text("Library", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = SecondaryCyan,
                                indicatorColor = PrimaryViolet,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_item_library")
                        )

                        // Add-ons
                        NavigationBarItem(
                            selected = currentTab == AppTab.ADDONS,
                            onClick = { viewModel.selectTab(AppTab.ADDONS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == AppTab.ADDONS) Icons.Filled.Extension else Icons.Outlined.Extension,
                                    contentDescription = "Add-ons"
                                )
                            },
                            label = { Text("Add-ons", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = SecondaryCyan,
                                indicatorColor = PrimaryViolet,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_item_addons")
                        )

                        // Settings
                        NavigationBarItem(
                            selected = currentTab == AppTab.SETTINGS,
                            onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == AppTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text("Settings", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = SecondaryCyan,
                                indicatorColor = PrimaryViolet,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_item_settings")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BackgroundDark)
            ) {
                when (currentTab) {
                    AppTab.DISCOVER -> DiscoverScreen(viewModel = viewModel)
                    AppTab.LIBRARY -> LibraryScreen(viewModel = viewModel)
                    AppTab.ADDONS -> AddonsScreen(viewModel = viewModel)
                    AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }

        // Animated Media Detail Sheet Modal
        AnimatedVisibility(
            visible = selectedMedia != null && !playerState.isOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            selectedMedia?.let { media ->
                MediaDetailSheet(
                    media = media,
                    streams = streams,
                    isWatchlisted = viewModel.isWatchlisted(media.id),
                    onClose = { viewModel.closeMediaDetail() },
                    onPlayDefault = { viewModel.playMediaDefault(media) },
                    onPlayStream = { stream -> viewModel.playStream(media, stream) },
                    onPlayEpisode = { ep -> viewModel.playMediaDefault(media, ep) },
                    onToggleWatchlist = { viewModel.toggleWatchlist(media) }
                )
            }
        }

        // Full Screen Video Player Overlay with Audio & Subtitles
        AnimatedVisibility(
            visible = playerState.isOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            VideoPlayerOverlay(
                playerState = playerState,
                audioSettings = audioSettings,
                subtitleSettings = subtitleSettings,
                onClose = { viewModel.closePlayer() },
                onPlaybackUpdate = { isPlaying, pos, dur, buff ->
                    viewModel.updatePlayerPlayback(isPlaying, pos, dur, buff)
                },
                onSwitchStream = { newStream ->
                    viewModel.switchStream(newStream)
                },
                onUpdateAudioSettings = { updated ->
                    viewModel.setAudioTrack(updated.selectedTrackId)
                    viewModel.setAudioBoost(updated.audioBoostLevel)
                    viewModel.setAudioDelay(updated.audioDelayMs)
                    viewModel.setPlaybackSpeed(updated.playbackSpeed)
                    viewModel.toggleSurroundSound(updated.surroundSoundEnabled)
                    viewModel.toggleSpeechClarity(updated.speechClarity)
                },
                onUpdateSubtitleSettings = { updated ->
                    viewModel.setSubtitleTrack(updated.selectedTrackId)
                    viewModel.setSecondarySubtitleTrack(updated.secondaryTrackId)
                    viewModel.setSubtitleDelay(updated.subtitleDelayMs)
                    viewModel.setSubtitleSize(updated.textSizeSp)
                    viewModel.setSubtitleColor(updated.textColorHex)
                    viewModel.setSubtitleBackground(updated.backgroundStyle)
                    viewModel.setSubtitlePosition(updated.verticalPosition)
                }
            )
        }
    }
}
