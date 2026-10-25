package com.example

import android.app.Application
import com.example.data.local.AetherStreamDatabase
import com.example.data.repository.WatchlistRepository

/**
 * Application class providing application-level singletons including the Room database.
 */
class AetherStreamApp : Application() {

    val database: AetherStreamDatabase by lazy {
        AetherStreamDatabase.getInstance(this)
    }

    val watchlistRepository: WatchlistRepository by lazy {
        WatchlistRepository(database.watchlistDao())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AetherStreamApp
            private set
    }
}
