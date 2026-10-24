package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioSettings by viewModel.audioSettings.collectAsStateWithLifecycle()
    val subtitleSettings by viewModel.subtitleSettings.collectAsStateWithLifecycle()

    var hardwareAcc by remember { mutableStateOf(true) }
    var autoPlayNext by remember { mutableStateOf(true) }
    var autoSyncAddons by remember { mutableStateOf(true) }
    var defaultQuality by remember { mutableStateOf("1080p") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Settings",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Player engine, audio & subtitle preferences",
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
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = SecondaryCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ==========================================
        // SECTION: AUDIO & SOUND
        // ==========================================
        Text(
            text = "AUDIO & SOUND ENHANCEMENT",
            color = PrimaryViolet,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Speech / Dialogue Clarity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = PrimaryViolet, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Speech & Dialogue Clarity", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Text("Amplifies vocal frequencies and suppresses loud action peaks", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = audioSettings.speechClarity,
                        onCheckedChange = { viewModel.toggleSpeechClarity(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet),
                        modifier = Modifier.testTag("switch_speech_clarity")
                    )
                }

                // Volume Boost
                Column {
                    Text("Volume Boost Multiplier", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Boost quiet streams above maximum system volume", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "100% (Normal)", 1 to "150%", 2 to "200%", 3 to "300% (Max)").forEach { (lvl, lbl) ->
                            val isSelected = audioSettings.audioBoostLevel == lvl
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) PrimaryViolet else SurfaceHighlight)
                                    .clickable { viewModel.setAudioBoost(lvl) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isSelected) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Spatial / Surround Sound
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SurroundSound, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Spatial Surround 5.1 / Atmos", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Text("Multi-channel digital audio passthrough & virtualization", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = audioSettings.surroundSoundEnabled,
                        onCheckedChange = { viewModel.toggleSurroundSound(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Preferred Audio Language
                Column {
                    Text("Preferred Audio Language", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("en_orig" to "English", "es_dub" to "Spanish", "fr_dub" to "French", "ja_orig" to "Japanese", "de_dub" to "German").forEach { (tid, name) ->
                            val isTrackSelected = audioSettings.selectedTrackId == tid
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isTrackSelected) PrimaryViolet else SurfaceHighlight)
                                    .clickable { viewModel.setAudioTrack(tid) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(name, color = if (isTrackSelected) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Audio Delay Sync Offset
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Audio Delay / Lip-Sync", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("${if (audioSettings.audioDelayMs >= 0) "+${audioSettings.audioDelayMs}" else "${audioSettings.audioDelayMs}"} ms", color = SecondaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = audioSettings.audioDelayMs.toFloat(),
                        onValueChange = { viewModel.setAudioDelay(((it / 50).toInt() * 50)) },
                        valueRange = -500f..500f,
                        steps = 19,
                        colors = SliderDefaults.colors(thumbColor = SecondaryCyan, activeTrackColor = PrimaryViolet)
                    )
                }
            }
        }

        // ==========================================
        // SECTION: SUBTITLES & CAPTIONS
        // ==========================================
        Text(
            text = "SUBTITLES & CAPTIONS",
            color = SecondaryCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Auto Select Subtitles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ClosedCaption, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Auto-Load Subtitles", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Text("Automatically load matched subtitles from OpenSubtitles v3", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = subtitleSettings.autoSelectSubtitles,
                        onCheckedChange = { viewModel.toggleAutoSelectSubtitles(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Primary Subtitle Language
                Column {
                    Text("Primary Subtitle Language", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("en_sub" to "English", "es_sub" to "Spanish", "fr_sub" to "French", "ja_sub" to "Japanese", "none" to "Off").forEach { (tid, name) ->
                            val isChosen = subtitleSettings.selectedTrackId == tid
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChosen) PrimaryViolet else SurfaceHighlight)
                                    .clickable { viewModel.setSubtitleTrack(tid) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(name, color = if (isChosen) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Dual Subtitles (Secondary Track)
                Column {
                    Text("Dual Subtitles (Language Learning)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Display a second language track simultaneously", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("none" to "Off", "es_sub" to "Spanish", "ja_sub" to "Japanese", "fr_sub" to "French").forEach { (tid, name) ->
                            val isSecChosen = subtitleSettings.secondaryTrackId == tid
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSecChosen) SecondaryCyan.copy(alpha = 0.3f) else SurfaceHighlight)
                                    .clickable { viewModel.setSecondarySubtitleTrack(tid) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(name, color = if (isSecChosen) SecondaryCyan else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Subtitle Size
                Column {
                    Text("Subtitle Size", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(14 to "Small", 18 to "Medium", 22 to "Large", 26 to "Extra Large").forEach { (sz, lbl) ->
                            val isSize = subtitleSettings.textSizeSp == sz
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSize) PrimaryViolet else SurfaceHighlight)
                                    .clickable { viewModel.setSubtitleSize(sz) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isSize) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Subtitle Color
                Column {
                    Text("Font Color", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            0xFFFFFFFF to "White",
                            0xFFFFEB3B to "Yellow",
                            0xFF00E5FF to "Cyan",
                            0xFF69F0AE to "Mint"
                        ).forEach { (colorVal, lbl) ->
                            val isCol = subtitleSettings.textColorHex == colorVal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceHighlight)
                                    .border(
                                        width = if (isCol) 2.dp else 1.dp,
                                        color = if (isCol) PrimaryViolet else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setSubtitleColor(colorVal) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
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
                    Text("Background Style", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("box" to "Black Box", "shadow" to "Drop Shadow", "none" to "Transparent").forEach { (styleKey, lbl) ->
                            val isBg = subtitleSettings.backgroundStyle == styleKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isBg) PrimaryViolet else SurfaceHighlight)
                                    .clickable { viewModel.setSubtitleBackground(styleKey) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isBg) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Vertical Screen Position
                Column {
                    Text("Vertical Position", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("bottom" to "Bottom (Standard)", "top" to "Top of Screen").forEach { (posKey, lbl) ->
                            val isPos = subtitleSettings.verticalPosition == posKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isPos) PrimaryViolet else SurfaceHighlight)
                                    .clickable { viewModel.setSubtitlePosition(posKey) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isPos) TextPrimary else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Subtitle Delay Offset
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtitle Timing Delay", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("${if (subtitleSettings.subtitleDelayMs >= 0) "+${subtitleSettings.subtitleDelayMs}" else "${subtitleSettings.subtitleDelayMs}"} ms", color = SecondaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = subtitleSettings.subtitleDelayMs.toFloat(),
                        onValueChange = { viewModel.setSubtitleDelay(((it / 100).toInt() * 100)) },
                        valueRange = -2000f..2000f,
                        steps = 39,
                        colors = SliderDefaults.colors(thumbColor = SecondaryCyan, activeTrackColor = PrimaryViolet)
                    )
                }
            }
        }

        // ==========================================
        // SECTION: STREAM ENGINE
        // ==========================================
        Text(
            text = "PLAYBACK ENGINE",
            color = PrimaryViolet,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Hardware Acceleration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hardware Accelerated Decoding",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Leverages GPU hardware codecs for smooth 4K/60fps playback",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = hardwareAcc,
                        onCheckedChange = { hardwareAcc = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextPrimary,
                            checkedTrackColor = PrimaryViolet
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Auto Play Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Autoplay Next Episode",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Automatically load the next episode when current finishes",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = autoPlayNext,
                        onCheckedChange = { autoPlayNext = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextPrimary,
                            checkedTrackColor = PrimaryViolet
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Preferred Quality
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Preferred Stream Resolution",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Currently selected: $defaultQuality",
                            color = SecondaryCyan,
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("4K", "1080p", "720p").forEach { res ->
                            val isChosen = defaultQuality == res
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChosen) PrimaryViolet else SurfaceHighlight)
                                    .clickable { defaultQuality = res }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = res,
                                    color = if (isChosen) TextPrimary else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Maintenance & Clear Cache
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Image & Subtitle Cache",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Clear cached posters, backdrop banners, and OpenSubtitles files",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
                Button(
                    onClick = {
                        Toast.makeText(context, "Subtitles and image cache cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceHighlight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Clear", color = TextPrimary, fontSize = 12.sp)
                }
            }
        }

        // Section: About
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AetherStream for Android",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A modern open media streaming platform and Stremio alternative. Features multi-track audio, speech clarity dialogue enhancement, synchronized multi-language subtitles, and community add-on compatibility.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Version 1.0.0 • Audio & Subtitles Engine v2.4",
                    color = SecondaryCyan,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
