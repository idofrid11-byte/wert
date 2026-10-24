package com.example.data.remote

import com.example.data.model.Episode
import com.example.data.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StremioApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val cinemetaBaseUrl = "https://v3-cinemeta.strem.io"

    suspend fun getCatalog(type: String = "movie", id: String = "top"): List<MediaItem> = withContext(Dispatchers.IO) {
        val url = "$cinemetaBaseUrl/catalog/$type/$id.json"
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val jsonString = response.body?.string() ?: return@withContext emptyList()
            parseCatalogJson(jsonString, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchCatalog(type: String = "movie", query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        val url = "$cinemetaBaseUrl/catalog/$type/top/search=$encodedQuery.json"
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val jsonString = response.body?.string() ?: return@withContext emptyList()
            parseCatalogJson(jsonString, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMeta(type: String, id: String): MediaItem? = withContext(Dispatchers.IO) {
        val url = "$cinemetaBaseUrl/meta/$type/$id.json"
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null
            val jsonString = response.body?.string() ?: return@withContext null
            val root = JSONObject(jsonString)
            if (!root.has("meta")) return@withContext null
            val metaObj = root.getJSONObject("meta")
            parseMediaItem(metaObj, type)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseCatalogJson(jsonString: String, fallbackType: String): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        try {
            val root = JSONObject(jsonString)
            val metas = root.optJSONArray("metas") ?: return list
            for (i in 0 until metas.length()) {
                val item = metas.getJSONObject(i)
                parseMediaItem(item, fallbackType)?.let { list.add(it) }
            }
        } catch (_: Exception) {}
        return list
    }

    private fun parseMediaItem(obj: JSONObject, fallbackType: String): MediaItem? {
        val id = obj.optString("id", obj.optString("imdb_id", ""))
        val name = obj.optString("name", "")
        if (id.isEmpty() || name.isEmpty()) return null

        val type = obj.optString("type", fallbackType)
        val poster = obj.optString("poster", "").takeIf { it.isNotEmpty() }
        val backdrop = obj.optString("background", obj.optString("backdrop", "")).takeIf { it.isNotEmpty() }
        val year = obj.optString("year", obj.optString("releaseInfo", "2024"))
        val rating = obj.optString("imdbRating", "8.2")
        val description = obj.optString("description", "")
        val runtime = obj.optString("runtime", "")

        val genres = mutableListOf<String>()
        val genreArr = obj.optJSONArray("genres") ?: obj.optJSONArray("genre")
        if (genreArr != null) {
            for (g in 0 until genreArr.length()) {
                genres.add(genreArr.getString(g))
            }
        }

        val cast = mutableListOf<String>()
        val castArr = obj.optJSONArray("cast")
        if (castArr != null) {
            for (c in 0 until castArr.length()) {
                cast.add(castArr.getString(c))
            }
        }

        val director = mutableListOf<String>()
        val dirArr = obj.optJSONArray("director")
        if (dirArr != null) {
            for (d in 0 until dirArr.length()) {
                director.add(dirArr.getString(d))
            }
        }

        val episodes = mutableListOf<Episode>()
        val videosArr = obj.optJSONArray("videos")
        if (videosArr != null) {
            for (v in 0 until videosArr.length()) {
                val vidObj = videosArr.getJSONObject(v)
                val season = vidObj.optInt("season", 1)
                val epNum = vidObj.optInt("episode", v + 1)
                val epTitle = vidObj.optString("title", "Episode $epNum")
                val epOverview = vidObj.optString("overview", "")
                val epThumbnail = vidObj.optString("thumbnail", "").takeIf { it.isNotEmpty() }
                episodes.add(
                    Episode(
                        id = vidObj.optString("id", "${id}:$season:$epNum"),
                        season = season,
                        number = epNum,
                        title = epTitle,
                        overview = epOverview,
                        thumbnail = epThumbnail
                    )
                )
            }
        }

        return MediaItem(
            id = id,
            title = name,
            type = type,
            poster = poster,
            backdrop = backdrop,
            year = year,
            rating = if (rating.isNotEmpty()) rating else "8.0",
            description = description,
            genres = genres,
            cast = cast,
            director = director,
            runtime = runtime,
            episodes = episodes
        )
    }
}
