package com.example

import com.example.data.model.AudioPlaybackSettings
import com.example.data.model.MediaItem
import com.example.data.model.SubtitlePlaybackSettings
import com.example.data.repository.MediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun repository_initialAddons_containsCinemetaAndTorrentio() {
        val repo = MediaRepository()
        val addons = repo.addons.value
        val cinemeta = addons.find { it.id == "org.stremio.cinemeta" }
        assertNotNull(cinemeta)
        assertTrue(cinemeta!!.isInstalled)
        assertTrue(cinemeta.isOfficial)
    }

    @Test
    fun repository_getStreamsForMedia_providesMultipleQualities() {
        val repo = MediaRepository()
        val testMedia = MediaItem(
            id = "tt1375666",
            title = "Inception",
            type = "movie",
            poster = null,
            backdrop = null,
            year = "2010",
            rating = "8.8",
            description = "Test description"
        )
        val streams = repo.getStreamsForMedia(testMedia)
        assertTrue(streams.isNotEmpty())
        assertTrue(streams.any { it.quality == "4K" })
        assertTrue(streams.any { it.quality == "1080p" })
    }

    @Test
    fun repository_toggleWatchlist_addsAndRemoves() {
        val repo = MediaRepository()
        val testMedia = MediaItem(
            id = "test_123",
            title = "Test Movie",
            type = "movie",
            poster = null,
            backdrop = null,
            year = "2024",
            rating = "8.0",
            description = ""
        )
        assertFalse(repo.isWatchlisted("test_123"))
        repo.toggleWatchlist(testMedia)
        assertTrue(repo.isWatchlisted("test_123"))
        repo.toggleWatchlist(testMedia)
        assertFalse(repo.isWatchlisted("test_123"))
    }

    @Test
    fun repository_audioAndSubtitleTracks_existForMedia() {
        val repo = MediaRepository()
        val testMedia = MediaItem(
            id = "tt1375666",
            title = "Inception",
            type = "movie",
            poster = null,
            backdrop = null,
            year = "2010",
            rating = "8.8",
            description = ""
        )
        val audioTracks = repo.getAudioTracksForMedia(testMedia)
        assertTrue(audioTracks.isNotEmpty())
        assertTrue(audioTracks.any { it.language == "en" })

        val subTracks = repo.getSubtitleTracksForMedia(testMedia)
        assertTrue(subTracks.isNotEmpty())
        assertTrue(subTracks.any { it.id == "en_sub" })
    }

    @Test
    fun repository_audioSettings_updatesSuccessfully() {
        val repo = MediaRepository()
        assertEquals(0, repo.audioSettings.value.audioDelayMs)
        repo.updateAudioSettings(AudioPlaybackSettings(audioDelayMs = 150, audioBoostLevel = 2, speechClarity = true))
        assertEquals(150, repo.audioSettings.value.audioDelayMs)
        assertEquals(2, repo.audioSettings.value.audioBoostLevel)
        assertTrue(repo.audioSettings.value.speechClarity)
    }

    @Test
    fun repository_subtitleSettings_updatesSuccessfully() {
        val repo = MediaRepository()
        repo.updateSubtitleSettings(SubtitlePlaybackSettings(selectedTrackId = "es_sub", textSizeSp = 22, textColorHex = 0xFFFFEB3B))
        assertEquals("es_sub", repo.subtitleSettings.value.selectedTrackId)
        assertEquals(22, repo.subtitleSettings.value.textSizeSp)
        assertEquals(0xFFFFEB3B, repo.subtitleSettings.value.textColorHex)
    }
}
