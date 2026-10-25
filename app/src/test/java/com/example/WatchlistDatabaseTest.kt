package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AetherStreamDatabase
import com.example.data.local.dao.WatchlistDao
import com.example.data.local.entity.WatchlistItemEntity
import com.example.data.local.entity.toMediaItem
import com.example.data.local.entity.toWatchlistEntity
import com.example.data.local.entity.toWatchlistItem
import com.example.data.model.MediaItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WatchlistDatabaseTest {

    private lateinit var db: AetherStreamDatabase
    private lateinit var dao: WatchlistDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AetherStreamDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.watchlistDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveWatchlistItem() = runBlocking {
        val movie = WatchlistItemEntity(
            mediaId = "tt1375666",
            title = "Inception",
            type = "movie",
            posterUrl = "https://images.metahub.space/poster/medium/tt1375666/img",
            year = "2010",
            rating = "8.8",
            overview = "A thief who steals corporate secrets...",
            genres = "Action, Sci-Fi",
            serviceProvider = "Netflix"
        )

        dao.insertOrUpdate(movie)

        val retrieved = dao.getWatchlistItemSync("tt1375666")
        assertNotNull(retrieved)
        assertEquals("Inception", retrieved?.title)
        assertEquals("movie", retrieved?.type)
        assertEquals("Netflix", retrieved?.serviceProvider)

        val isWatchlisted = dao.isWatchlisted("tt1375666")
        assertTrue(isWatchlisted)
    }

    @Test
    fun testFilterByType() = runBlocking {
        val movie = WatchlistItemEntity(
            mediaId = "m1",
            title = "Dune 2",
            type = "movie",
            posterUrl = null,
            year = "2024",
            rating = "8.6"
        )
        val series = WatchlistItemEntity(
            mediaId = "s1",
            title = "Arcane",
            type = "series",
            posterUrl = null,
            year = "2021",
            rating = "9.0"
        )

        dao.insertOrUpdate(movie)
        dao.insertOrUpdate(series)

        val movies = dao.getWatchlistByType("movie").first()
        val shows = dao.getWatchlistByType("series").first()

        assertEquals(1, movies.size)
        assertEquals("Dune 2", movies[0].title)

        assertEquals(1, shows.size)
        assertEquals("Arcane", shows[0].title)
    }

    @Test
    fun testContinueWatchingProgress() = runBlocking {
        val movie = WatchlistItemEntity(
            mediaId = "m1",
            title = "Interstellar",
            type = "movie",
            posterUrl = null,
            year = "2014",
            rating = "8.7",
            lastWatchedPositionMs = 0L,
            totalDurationMs = 10000000L,
            progressPercent = 0f
        )
        dao.insertOrUpdate(movie)

        // Initially not in continue watching
        val initialCw = dao.getContinueWatching().first()
        assertTrue(initialCw.isEmpty())

        // Update progress
        dao.updateProgress(
            mediaId = "m1",
            positionMs = 5000000L,
            durationMs = 10000000L,
            progressPercent = 0.5f
        )

        val updatedCw = dao.getContinueWatching().first()
        assertEquals(1, updatedCw.size)
        assertEquals("Interstellar", updatedCw[0].title)
        assertEquals(5000000L, updatedCw[0].lastWatchedPositionMs)
        assertEquals(0.5f, updatedCw[0].progressPercent, 0.01f)
    }

    @Test
    fun testFavoriteToggleAndFilter() = runBlocking {
        val item = WatchlistItemEntity(
            mediaId = "f1",
            title = "Cyberpunk: Edgerunners",
            type = "anime",
            posterUrl = null,
            year = "2022",
            rating = "8.3",
            isFavorite = false
        )
        dao.insertOrUpdate(item)

        var favorites = dao.getFavorites().first()
        assertTrue(favorites.isEmpty())

        // Mark as favorite
        dao.updateFavorite("f1", isFavorite = true)

        favorites = dao.getFavorites().first()
        assertEquals(1, favorites.size)
        assertEquals("Cyberpunk: Edgerunners", favorites[0].title)
        assertTrue(favorites[0].isFavorite)
    }

    @Test
    fun testDeleteAndClear() = runBlocking {
        val item1 = WatchlistItemEntity("1", "Item 1", "movie", null, null, "2020", "7.0")
        val item2 = WatchlistItemEntity("2", "Item 2", "series", null, null, "2021", "8.0")
        dao.insertAll(listOf(item1, item2))

        val countInitial = dao.getAllWatchlist().first().size
        assertEquals(2, countInitial)

        dao.deleteByMediaId("1")
        val afterDelete = dao.getAllWatchlist().first()
        assertEquals(1, afterDelete.size)
        assertEquals("2", afterDelete[0].mediaId)

        dao.clearWatchlist()
        val afterClear = dao.getAllWatchlist().first()
        assertTrue(afterClear.isEmpty())
        assertFalse(dao.isWatchlisted("2"))
    }

    @Test
    fun testMappers() {
        val mediaItem = MediaItem(
            id = "test_media_1",
            title = "Blade Runner 2049",
            type = "movie",
            poster = "https://example.com/poster.jpg",
            backdrop = "https://example.com/backdrop.jpg",
            year = "2017",
            rating = "8.0",
            description = "A young blade runner...",
            genres = listOf("Action", "Sci-Fi", "Drama"),
            serviceProvider = "HBO Max"
        )

        val entity = mediaItem.toWatchlistEntity(isFavorite = true)
        assertEquals("test_media_1", entity.mediaId)
        assertEquals("Blade Runner 2049", entity.title)
        assertEquals("movie", entity.type)
        assertEquals("Action, Sci-Fi, Drama", entity.genres)
        assertEquals("HBO Max", entity.serviceProvider)
        assertTrue(entity.isFavorite)

        val domainItem = entity.toMediaItem()
        assertEquals(mediaItem.id, domainItem.id)
        assertEquals(mediaItem.title, domainItem.title)
        assertEquals(listOf("Action", "Sci-Fi", "Drama"), domainItem.genres)
        assertEquals("HBO Max", domainItem.serviceProvider)

        val watchlistItem = entity.toWatchlistItem()
        assertEquals("test_media_1", watchlistItem.mediaId)
        assertEquals("Blade Runner 2049", watchlistItem.title)
    }
}
