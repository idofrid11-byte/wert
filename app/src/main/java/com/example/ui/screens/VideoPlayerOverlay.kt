package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.AudioPlaybackSettings
import com.example.data.model.MediaStream
import com.example.data.model.SubtitlePlaybackSettings
import com.example.ui.PlayerState
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerOverlay(
    playerState: PlayerState,
    audioSettings: AudioPlaybackSettings,
    subtitleSettings: SubtitlePlaybackSettings,
    onClose: () -> Unit,
    onPlaybackUpdate: (isPlaying: Boolean, currentPosMs: Long, durationMs: Long, isBuffering: Boolean) -> Unit,
    onSwitchStream: (MediaStream) -> Unit,
    onUpdateAudioSettings: (AudioPlaybackSettings) -> Unit,
    onUpdateSubtitleSettings: (SubtitlePlaybackSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    val media = playerState.media ?: return
    val stream = playerState.stream ?: return

    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var isControlsVisible by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPosMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var isUserDraggingSlider by remember { mutableStateOf(false) }
    var sliderProgress by remember { mutableFloatStateOf(0f) }

    // Dialog sheets state
    var showStreamPickerMenu by remember { mutableStateOf(false) }
    var showAudioSettingsDialog by remember { mutableStateOf(false) }
    var showSubtitleSettingsDialog by remember { mutableStateOf(false) }
    var showSpeedPickerMenu by remember { mutableStateOf(false) }

    // Auto-hide controls timer (paused when a dialog or menu is open)
    val isAnyModalOpen = showStreamPickerMenu || showAudioSettingsDialog || showSubtitleSettingsDialog || showSpeedPickerMenu
    LaunchedEffect(isControlsVisible, isPlaying, isAnyModalOpen) {
        if (isControlsVisible && isPlaying && !isAnyModalOpen) {
            delay(4000)
            isControlsVisible = false
        }
    }

    // Periodic time polling from VideoView
    LaunchedEffect(videoViewRef, isPlaying) {
        while (true) {
            val vv = videoViewRef
            if (vv != null) {
                try {
                    val pos = vv.currentPosition.toLong()
                    val dur = vv.duration.toLong().coerceAtLeast(1L)
                    currentPosMs = pos
                    durationMs = dur
                    if (!isUserDraggingSlider) {
                        sliderProgress = (pos.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
                    }
                    onPlaybackUpdate(isPlaying, pos, dur, isBuffering)
                } catch (_: Exception) {}
            }
            delay(400)
        }
    }

    // Calculate current live subtitle line according to timestamp & timing offset
    val effectiveTimeMs = currentPosMs + subtitleSettings.subtitleDelayMs
    val currentSubtitleText = remember(effectiveTimeMs, subtitleSettings.selectedTrackId) {
        if (subtitleSettings.selectedTrackId == "none") ""
        else getSubtitleTextForTimestamp(effectiveTimeMs, media.title, subtitleSettings.selectedTrackId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isControlsVisible = !isControlsVisible
            }
            .testTag("video_player_overlay")
    ) {
        // Native Video View
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    setVideoURI(Uri.parse(stream.streamUrl))
                    setOnPreparedListener { mp ->
                        mp.isLooping = true
                        start()
                        isPlaying = true
                        isBuffering = false
                        durationMs = duration.toLong()
                    }
                    setOnInfoListener { _, what, _ ->
                        if (what == 701) isBuffering = true
                        else if (what == 702) isBuffering = false
                        true
                    }
                    setOnErrorListener { _, _, _ ->
                        isBuffering = false
                        true
                    }
                    videoViewRef = this
                }
            },
            update = { vv ->
                val currentUri = Uri.parse(stream.streamUrl)
                vv.setVideoURI(currentUri)
                vv.start()
            },
            modifier = Modifier.fillMaxSize()
        )

        // Live Subtitle Display Rendered on Screen
        if (currentSubtitleText.isNotBlank()) {
            val alignMod = if (subtitleSettings.verticalPosition == "top") {
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 70.dp)
            } else {
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (isControlsVisible) 110.dp else 40.dp)
            }

            Box(
                modifier = alignMod
                    .padding(horizontal = 24.dp)
                    .then(
                        when (subtitleSettings.backgroundStyle) {
                            "box" -> Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                            "solid" -> Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                            else -> Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        }
                    )
            ) {
                Text(
                    text = currentSubtitleText,
                    color = Color(subtitleSettings.textColorHex),
                    fontSize = subtitleSettings.textSizeSp.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = (subtitleSettings.textSizeSp + 6).sp,
                    modifier = Modifier.testTag("live_subtitle_text")
                )
            }
        }

        // Buffering Spinner
        if (isBuffering) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryViolet,
                    modifier = Modifier.size(36.dp),
                    strokeWidth = 3.dp
                )
            }
        }

        // Playback Control Overlay
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .testTag("player_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = media.title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (playerState.episode != null) {
                                Text(
                                    text = "S${playerState.episode.season} E${playerState.episode.number}: ${playerState.episode.title}",
                                    color = SecondaryCyan,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Actions Bar: Quality, Speed, Audio, Subtitles
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Speed Switcher
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { showSpeedPickerMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("player_speed_button"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${audioSettings.playbackSpeed}x",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            DropdownMenu(
                                expanded = showSpeedPickerMenu,
                                onDismissRequest = { showSpeedPickerMenu = false },
                                modifier = Modifier.background(SurfaceDark)
                            ) {
                                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${spd}x Speed",
                                                color = if (audioSettings.playbackSpeed == spd) SecondaryCyan else TextPrimary,
                                                fontWeight = if (audioSettings.playbackSpeed == spd) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            showSpeedPickerMenu = false
                                            onUpdateAudioSettings(audioSettings.copy(playbackSpeed = spd))
                                        }
                                    )
                                }
                            }
                        }

                        // Audio Settings Button
                        IconButton(
                            onClick = { showAudioSettingsDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (audioSettings.audioBoostLevel > 0 || audioSettings.speechClarity) PrimaryViolet.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f))
                                .testTag("player_audio_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = "Audio Settings",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Subtitle Settings Button
                        IconButton(
                            onClick = { showSubtitleSettingsDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (subtitleSettings.selectedTrackId != "none") SecondaryCyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.15f))
                                .testTag("player_subtitle_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ClosedCaption,
                                contentDescription = "Subtitle Settings",
                                tint = if (subtitleSettings.selectedTrackId != "none") SecondaryCyan else TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Quality Switcher
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { showStreamPickerMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("player_quality_button"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stream.quality,
                                    color = SecondaryCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            DropdownMenu(
                                expanded = showStreamPickerMenu,
                                onDismissRequest = { showStreamPickerMenu = false },
                                modifier = Modifier.background(SurfaceDark)
                            ) {
                                playerState.availableStreams.forEach { st ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = "${st.quality} • ${st.name}",
                                                    color = if (st.id == stream.id) SecondaryCyan else TextPrimary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = st.title,
                                                    color = TextMuted,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        },
                                        onClick = {
                                            showStreamPickerMenu = false
                                            onSwitchStream(st)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Center Play/Pause & Seek 10s
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    IconButton(
                        onClick = {
                            videoViewRef?.let { vv ->
                                val target = (vv.currentPosition - 10000).coerceAtLeast(0)
                                vv.seekTo(target)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("player_rewind_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = TextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PrimaryViolet)
                            .clickable {
                                videoViewRef?.let { vv ->
                                    if (vv.isPlaying) {
                                        vv.pause()
                                        isPlaying = false
                                    } else {
                                        vv.start()
                                        isPlaying = true
                                    }
                                }
                            }
                            .testTag("player_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = TextPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            videoViewRef?.let { vv ->
                                val target = (vv.currentPosition + 10000).coerceAtMost(vv.duration)
                                vv.seekTo(target)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("player_forward_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = TextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Bottom Timeline and Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTimeMs(currentPosMs),
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = formatTimeMs(durationMs),
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Slider(
                        value = sliderProgress,
                        onValueChange = { newVal ->
                            isUserDraggingSlider = true
                            sliderProgress = newVal
                        },
                        onValueChangeFinished = {
                            isUserDraggingSlider = false
                            videoViewRef?.let { vv ->
                                val target = (sliderProgress * durationMs).toInt()
                                vv.seekTo(target)
                            }
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = SecondaryCyan,
                            activeTrackColor = PrimaryViolet,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_timeline_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val activeAudio = playerState.availableAudioTracks.find { it.id == audioSettings.selectedTrackId }
                            Text(
                                text = "Audio: ${activeAudio?.name ?: "English"}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val activeSub = playerState.availableSubtitleTracks.find { it.id == subtitleSettings.selectedTrackId }
                            Text(
                                text = "• Sub: ${activeSub?.name ?: "Off"}",
                                color = if (subtitleSettings.selectedTrackId != "none") SecondaryCyan else TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "Tap to toggle UI",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // AUDIO SETTINGS DIALOG
        if (showAudioSettingsDialog) {
            AudioSettingsModal(
                settings = audioSettings,
                tracks = playerState.availableAudioTracks,
                onDismiss = { showAudioSettingsDialog = false },
                onSave = { updated ->
                    onUpdateAudioSettings(updated)
                    showAudioSettingsDialog = false
                }
            )
        }

        // SUBTITLE SETTINGS DIALOG
        if (showSubtitleSettingsDialog) {
            SubtitleSettingsModal(
                settings = subtitleSettings,
                tracks = playerState.availableSubtitleTracks,
                onDismiss = { showSubtitleSettingsDialog = false },
                onSave = { updated ->
                    onUpdateSubtitleSettings(updated)
                    showSubtitleSettingsDialog = false
                }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoViewRef?.stopPlayback()
        }
    }
}

@Composable
private fun AudioSettingsModal(
    settings: AudioPlaybackSettings,
    tracks: List<com.example.data.model.AudioTrack>,
    onDismiss: () -> Unit,
    onSave: (AudioPlaybackSettings) -> Unit
) {
    var selectedTrack by remember { mutableStateOf(settings.selectedTrackId) }
    var boostLevel by remember { mutableStateOf(settings.audioBoostLevel) }
    var audioDelay by remember { mutableStateOf(settings.audioDelayMs) }
    var speechClarity by remember { mutableStateOf(settings.speechClarity) }
    var surroundSound by remember { mutableStateOf(settings.surroundSoundEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Audiotrack, contentDescription = null, tint = PrimaryViolet)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Audio & Sound Settings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Audio Tracks Picker
                Text("AUDIO TRACK", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                tracks.forEach { track ->
                    val isSelected = selectedTrack == track.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryViolet.copy(alpha = 0.25f) else SurfaceVariantDark)
                            .clickable { selectedTrack = track.id }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(track.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("${track.channels} Channels • ${track.codec}", color = TextMuted, fontSize = 11.sp)
                        }
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Speech Clarity / Night Mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceVariantDark)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Speech & Dialogue Boost", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Enhances whispered speech and compresses dynamic range for night listening", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = speechClarity,
                        onCheckedChange = { speechClarity = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Volume Boost Level
                Column {
                    Text("VOLUME BOOST", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "Normal", 1 to "+50%", 2 to "+100%", 3 to "+200%").forEach { (lvl, lbl) ->
                            val chosen = boostLevel == lvl
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (chosen) PrimaryViolet else SurfaceVariantDark)
                                    .clickable { boostLevel = lvl }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (chosen) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Audio Delay / Sync Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("AUDIO SYNC OFFSET", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("${if (audioDelay >= 0) "+$audioDelay" else "$audioDelay"} ms", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = audioDelay.toFloat(),
                        onValueChange = { audioDelay = ((it / 50).toInt() * 50) },
                        valueRange = -500f..500f,
                        steps = 19,
                        colors = SliderDefaults.colors(thumbColor = SecondaryCyan, activeTrackColor = PrimaryViolet)
                    )
                }

                // Surround Sound 5.1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceVariantDark)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Spatial / Surround Audio", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Dolby Digital & multi-channel headphone virtualization", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = surroundSound,
                        onCheckedChange = { surroundSound = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        settings.copy(
                            selectedTrackId = selectedTrack,
                            audioBoostLevel = boostLevel,
                            audioDelayMs = audioDelay,
                            speechClarity = speechClarity,
                            surroundSoundEnabled = surroundSound
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet)
            ) {
                Text("Apply Settings")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun SubtitleSettingsModal(
    settings: SubtitlePlaybackSettings,
    tracks: List<com.example.data.model.SubtitleTrack>,
    onDismiss: () -> Unit,
    onSave: (SubtitlePlaybackSettings) -> Unit
) {
    var selectedTrack by remember { mutableStateOf(settings.selectedTrackId) }
    var subtitleDelay by remember { mutableStateOf(settings.subtitleDelayMs) }
    var textSize by remember { mutableStateOf(settings.textSizeSp) }
    var textColor by remember { mutableStateOf(settings.textColorHex) }
    var bgStyle by remember { mutableStateOf(settings.backgroundStyle) }
    var position by remember { mutableStateOf(settings.verticalPosition) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ClosedCaption, contentDescription = null, tint = SecondaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Subtitle & Caption Settings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Subtitle Track Selection
                Text("SUBTITLE LANGUAGE", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                tracks.forEach { track ->
                    val isSelected = selectedTrack == track.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryViolet.copy(alpha = 0.25f) else SurfaceVariantDark)
                            .clickable { selectedTrack = track.id }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(track.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            if (track.provider.isNotEmpty()) {
                                Text("Powered by ${track.provider}", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle Timing Delay
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("SUBTITLE TIMING DELAY", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("${if (subtitleDelay >= 0) "+$subtitleDelay" else "$subtitleDelay"} ms", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = subtitleDelay.toFloat(),
                        onValueChange = { subtitleDelay = ((it / 100).toInt() * 100) },
                        valueRange = -2000f..2000f,
                        steps = 39,
                        colors = SliderDefaults.colors(thumbColor = SecondaryCyan, activeTrackColor = PrimaryViolet)
                    )
                }

                // Subtitle Text Size
                Column {
                    Text("FONT SIZE", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(14 to "Small", 18 to "Medium", 22 to "Large", 26 to "Extra").forEach { (sz, lbl) ->
                            val chosen = textSize == sz
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (chosen) PrimaryViolet else SurfaceVariantDark)
                                    .clickable { textSize = sz }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (chosen) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Subtitle Color
                Column {
                    Text("TEXT COLOR", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            0xFFFFFFFF to "White",
                            0xFFFFEB3B to "Yellow",
                            0xFF00E5FF to "Cyan",
                            0xFF69F0AE to "Mint"
                        ).forEach { (colorVal, lbl) ->
                            val isColorChosen = textColor == colorVal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceVariantDark)
                                    .border(
                                        width = if (isColorChosen) 2.dp else 1.dp,
                                        color = if (isColorChosen) PrimaryViolet else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { textColor = colorVal }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color(colorVal))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(lbl, color = TextPrimary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Background Style
                Column {
                    Text("BACKGROUND STYLE", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("box" to "Black Box", "shadow" to "Drop Shadow", "none" to "Transparent").forEach { (styleKey, lbl) ->
                            val isChosen = bgStyle == styleKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChosen) PrimaryViolet else SurfaceVariantDark)
                                    .clickable { bgStyle = styleKey }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isChosen) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Vertical Position (Bottom / Top)
                Column {
                    Text("SCREEN POSITION", color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("bottom" to "Bottom (Standard)", "top" to "Top of Screen").forEach { (posKey, lbl) ->
                            val isChosen = position == posKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChosen) PrimaryViolet else SurfaceVariantDark)
                                    .clickable { position = posKey }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isChosen) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        settings.copy(
                            selectedTrackId = selectedTrack,
                            subtitleDelayMs = subtitleDelay,
                            textSizeSp = textSize,
                            textColorHex = textColor,
                            backgroundStyle = bgStyle,
                            verticalPosition = position
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet)
            ) {
                Text("Save & Apply")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// Generate realistic subtitles timed with the media playback
private fun getSubtitleTextForTimestamp(timeMs: Long, title: String, trackId: String): String {
    val sec = (timeMs / 1000) % 60
    val isSpanish = trackId.startsWith("es")
    val isFrench = trackId.startsWith("fr")
    val isJapanese = trackId.startsWith("ja")

    return when {
        sec in 0..4 -> when {
            isSpanish -> "[Música orquestal dramática sonando]"
            isFrench -> "[Musique orchestrale dramatique]"
            isJapanese -> "[ドラマチックなオーケストラ音楽が流れる]"
            else -> "[Dramatic orchestral theme plays]"
        }
        sec in 5..9 -> when {
            isSpanish -> "— ¿Estás listo para entrar en el sueño?"
            isFrench -> "— Êtes-vous prêt à entrer dans le rêve ?"
            isJapanese -> "— 夢に入る準備はできているか？"
            else -> "— Are you ready to enter the dream sequence?"
        }
        sec in 10..15 -> when {
            isSpanish -> "— Una idea es como un virus. Resistente. Altamente contagiosa."
            isFrench -> "— Une idée est comme un virus. Résistante. Hautement contagieuse."
            isJapanese -> "— アイデアはウイルスのようだ。強く、伝染しやすい。"
            else -> "— An idea is like a virus. Resilient. Highly contagious."
        }
        sec in 16..22 -> when {
            isSpanish -> "Tenemos que profundizar más. Construir el mundo entero."
            isFrench -> "Nous devons aller plus profondément. Construire le monde."
            isJapanese -> "もっと深く潜る必要がある。世界全体を構築する。"
            else -> "We have to go deeper. Build the entire architecture."
        }
        sec in 23..29 -> when {
            isSpanish -> "[Efectos de sonido de ciencia ficción y reverberación]"
            isFrench -> "[Effets sonores de science-fiction]"
            isJapanese -> "[SFの効果音とリバーブ]"
            else -> "[Cinematic bass drop & audio reverberation]"
        }
        sec in 30..36 -> when {
            isSpanish -> "— El tiempo se mueve más rápido en cada nivel."
            isFrench -> "— Le temps s'écoule plus vite à chaque niveau."
            isJapanese -> "— 階層ごとに時間の進み方が加速する。"
            else -> "— Time moves slower in the outer layers, faster inside."
        }
        sec in 37..44 -> when {
            isSpanish -> "— No pierdas tu tótem. Es la única forma de saber qué es real."
            isFrench -> "— Ne perds pas ton totem. C'est le seul moyen de savoir."
            isJapanese -> "— トーテムを失うな。それが現実を知る唯一の手がかりだ。"
            else -> "— Never lose your totem. It's the only way to know what's real."
        }
        sec in 45..52 -> when {
            isSpanish -> "[Intensificación de la banda sonora]"
            isFrench -> "[Montée en puissance de la bande sonore]"
            isJapanese -> "[劇的なサウンドトラックの高まり]"
            else -> "[Tension builds in the orchestral score]"
        }
        else -> when {
            isSpanish -> "— ¡Sujétate! ¡El salto está por comenzar!"
            isFrench -> "— Accrochez-vous ! Le saut va commencer !"
            isJapanese -> "— つかまれ！キックが始まる！"
            else -> "— Hold on tight! The kick is about to hit!"
        }
    }
}

private fun formatTimeMs(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
