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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
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

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val debridAccount by viewModel.debridAccount.collectAsStateWithLifecycle()
    val watchStats by viewModel.watchStats.collectAsStateWithLifecycle()
    val kidsModeEnabled by viewModel.kidsModeEnabled.collectAsStateWithLifecycle()

    var hardwareAcc by remember { mutableStateOf(true) }
    var autoPlayNext by remember { mutableStateOf(true) }
    var autoSyncAddons by remember { mutableStateOf(true) }
    var defaultQuality by remember { mutableStateOf("1080p") }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newPasswordInput by remember { mutableStateOf("") }
    var magnetInput by remember { mutableStateOf("") }

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
                    text = "Account, debrid, audio, subtitles & stream engine",
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
        // SECTION: ACCOUNT & PROFILE (MANDATORY GATE)
        // ==========================================
        Text(
            text = "ACCOUNT & CLOUD PROFILE",
            color = PrimaryViolet,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
            modifier = Modifier.testTag("account_profile_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(PrimaryViolet),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.username ?: "AetherStream User",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentUser?.email ?: "Not signed in",
                            color = SecondaryCyan,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF00E676).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("VIP Member", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cloud Sync Active", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showChangePasswordDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("שנה סיסמה", color = TextPrimary, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.logout()
                            Toast.makeText(context, "התנתקת בהצלחה. יש להתחבר או ליצור חשבון כדי להמשיך.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935).copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("logout_button")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFFF8A80), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("התנתק (Log Out)", color = Color(0xFFFF8A80), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ==========================================
        // SECTION: DEBRID CLOUD SERVICE PROVIDER
        // ==========================================
        Text(
            text = "DEBRID CLOUD STREAMING",
            color = SecondaryCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cloud, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Active Debrid Provider", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text("Instant cached bufferless torrent streaming", color = TextMuted, fontSize = 11.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00E676).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("${debridAccount.daysRemaining} Days Left", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Provider Switcher Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Real-Debrid", "AllDebrid", "Premiumize", "TorBox").forEach { prov ->
                        val isProv = debridAccount.provider == prov
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isProv) PrimaryViolet else SurfaceHighlight)
                                .clickable { viewModel.updateDebridAccount(debridAccount.copy(provider = prov)) }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(prov, color = if (isProv) TextPrimary else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // CDN Server Location
                Column {
                    Text("Debrid CDN Server Routing", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(debridAccount.selectedCdnServer, color = SecondaryCyan, fontSize = 11.sp)
                }

                // Cloud Magnet Direct Cacher
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Instant Cloud Magnet Streamer", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = magnetInput,
                            onValueChange = { magnetInput = it },
                            placeholder = { Text("magnet:?xt=urn:btih:...", fontSize = 11.sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                focusedIndicatorColor = SecondaryCyan,
                                unfocusedIndicatorColor = SurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (magnetInput.isNotBlank()) {
                                    viewModel.addMagnetToDebrid(magnetInput, "Direct Cloud Stream")
                                    magnetInput = ""
                                    Toast.makeText(context, "Cached to Cloud! Ready to stream instantly.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Please paste a magnet link or hash", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cache", color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION: TRAKT & ANILIST SCROBBLER
        // ==========================================
        Text(
            text = "TRAKT & ANILIST SCROBBLE",
            color = PrimaryViolet,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Automatic Trakt.tv Scrobble", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text("Auto-log watched minutes and scrobble episodes to Trakt profile", color = TextMuted, fontSize = 11.sp)
                    }

                    Switch(
                        checked = watchStats.traktScrobbleEnabled,
                        onCheckedChange = { viewModel.toggleTraktScrobble(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Linked Trakt Account", color = TextMuted, fontSize = 11.sp)
                        Text("@${watchStats.traktUsername}", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.syncTraktCloud()
                            Toast.makeText(context, "Trakt library synced with cloud!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sync Now", color = SecondaryCyan, fontSize = 11.sp)
                    }
                }
            }
        }

        // ==========================================
        // SECTION: PARENTAL CONTROLS & CONTENT FILTER
        // ==========================================
        Text(
            text = "PARENTAL CONTROLS & CONTENT FILTER",
            color = SecondaryCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = PrimaryViolet, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kids Safe Mode", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text("Filter out explicit, mature, or unrated adult content", color = TextMuted, fontSize = 11.sp)
                    }

                    Switch(
                        checked = kidsModeEnabled,
                        onCheckedChange = { viewModel.toggleKidsMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }
            }
        }

        // ==========================================
        // SECTION: AUDIO & SOUND
        // ==========================================
        Text(
            text = "AUDIO & SOUND ENHANCEMENT / הגדרות שמע ואודיו",
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
                            Text("Speech & Dialogue Clarity (הדגשת דיבור)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Text("מגביר תדרי דיבור ומנמיך פיצוצים ורעשי רקע חזקים", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = audioSettings.speechClarity,
                        onCheckedChange = { viewModel.toggleSpeechClarity(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet),
                        modifier = Modifier.testTag("switch_speech_clarity")
                    )
                }

                // Night Mode Volume Leveling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Night Mode Volume Leveling (מצב לילה שקט)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Text("איזון אוטומטי של טווח דינמי כדי למנוע קפיצות ווליום חדות בלילה", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = audioSettings.nightModeVolumeLeveling,
                        onCheckedChange = { viewModel.toggleNightModeVolumeLeveling(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet),
                        modifier = Modifier.testTag("switch_night_mode_leveling")
                    )
                }

                // Prefer Hebrew Audio Dubbing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("דיבוב בעברית כברירת מחדל (Hebrew Audio)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("בחר אוטומטית ברצועת שמע בעברית כאשר זמינה בסרטי ילדים ואנימציה", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = audioSettings.preferredHebrewAudio,
                        onCheckedChange = { viewModel.togglePreferredHebrewAudio(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Volume Boost
                Column {
                    Text("Volume Boost Multiplier (הגברת עוצמת שמע)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("הגברת שמע עבור סטרימים חלשים מעבר למקסימום המערכת", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "100% (רגיל)", 1 to "150%", 2 to "200%", 3 to "300% (מקסימום)").forEach { (lvl, lbl) ->
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

                // Audio Passthrough Mode
                Column {
                    Text("Digital Audio Passthrough (מעבר שמע דיגיטלי)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Auto" to "אוטומטי", "PCM Stereo" to "סטריאו PCM", "Dolby Digital" to "Dolby AC3", "Atmos Bitstream" to "Dolby Atmos").forEach { (modeKey, lbl) ->
                            val isPassSelected = audioSettings.audioPassthrough == modeKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isPassSelected) PrimaryViolet else SurfaceHighlight)
                                    .clickable { viewModel.setAudioPassthrough(modeKey) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isPassSelected) TextPrimary else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                        Text("הדמיית סראונד רב-ערוצי באוזניות ורמקולים", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = audioSettings.surroundSoundEnabled,
                        onCheckedChange = { viewModel.toggleSurroundSound(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Preferred Audio Language
                Column {
                    Text("שפת שמע מועדפת (Preferred Audio Language)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("he_dub" to "עברית", "en_orig" to "English", "es_dub" to "Español", "fr_dub" to "Français", "ja_orig" to "日本語").forEach { (tid, name) ->
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
                        Text("Audio Delay / Lip-Sync (סנכרון שפתיים)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
            text = "SUBTITLES & CAPTIONS / הגדרות כתוביות ותרגום",
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
                            Text("טעינת כתוביות אוטומטית (Auto-Load)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Text("טעינה אוטומטית של תרגום מותאם מ-OpenSubtitles v3 ו-Torec", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = subtitleSettings.autoSelectSubtitles,
                        onCheckedChange = { viewModel.toggleAutoSelectSubtitles(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Default Hebrew Subtitles Priority
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("עדיפות עליונה לכתוביות בעברית (Hebrew Priority)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("הצג כתוביות בעברית באופן אוטומטי בכל תחילת ניגון", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = subtitleSettings.preferredHebrewSubtitles,
                        onCheckedChange = { viewModel.togglePreferredHebrewSubtitles(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Hebrew RTL and Punctuation Fix
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("תיקון כיווניות ופיסוק לעברית (BiDi RTL Fix)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("יישור מימין לשמאל ותיקון סימני שאלה, פסיקים ומספרים בכתוביות", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = subtitleSettings.cjkHebrewRtlOptimization,
                        onCheckedChange = { viewModel.toggleHebrewRtlOptimization(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Dual Subtitles Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("כתוביות כפולות (עברית + אנגלית במקביל)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("הצגת שתי שפות במקביל ללמידה ושיפור אנגלית", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = subtitleSettings.dualSubtitlesEnabled,
                        onCheckedChange = { viewModel.toggleDualSubtitles(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryViolet)
                    )
                }

                // Primary Subtitle Language
                Column {
                    Text("שפת כתוביות ראשית (Primary Subtitle)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("he_sub" to "עברית", "en_sub" to "English", "es_sub" to "Spanish", "fr_sub" to "French", "none" to "ללא (Off)").forEach { (tid, name) ->
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

                // Subtitle Encoding
                Column {
                    Text("קידוד כתוביות (Subtitle Encoding)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("למניעת ג'יבריש וסימנים משובשים בקבצי SRT ישנים", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("UTF-8" to "UTF-8 (מומלץ)", "Windows-1255" to "Windows-1255 (עברית)", "ISO-8859-8" to "ISO-8859-8").forEach { (encKey, lbl) ->
                            val isEnc = subtitleSettings.subtitleEncoding == encKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isEnc) PrimaryViolet else SurfaceHighlight)
                                    .clickable { viewModel.setSubtitleEncoding(encKey) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isEnc) TextPrimary else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Subtitle Size
                Column {
                    Text("גודל גופן כתוביות (Font Size)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(14 to "קטן (14)", 18 to "רגיל (18)", 22 to "גדול (22)", 26 to "ענק (26)").forEach { (sz, lbl) ->
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
                    Text("צבע גופן (Font Color)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            0xFFFFFFFF to "לבן / White",
                            0xFFFFEB3B to "צהוב קולנוע",
                            0xFF00E5FF to "טורקיז / Cyan",
                            0xFF69F0AE to "ירוק מנטה"
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
                                    Text(lbl, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Background & Shadow Style
                Column {
                    Text("עיצוב רקע והצללה (Background & Shadow)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("box" to "קופסה שחורה", "shadow" to "צל שחור מודגש", "none" to "שקוף ללא רקע").forEach { (styleKey, lbl) ->
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

                // Subtitle Timing Delay Offset
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("השהיית / הקדמת כתוביות (Timing Offset)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
