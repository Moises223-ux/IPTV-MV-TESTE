package com.iptv.mv

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class M3URepository(private val context: Context) {
    companion object {
        const val PLAYLIST_URL = "https://raw.githubusercontent.com/ManoLimah/Manoteste/refs/heads/main/ManoTV.m3u"
        private const val CACHE_FILE_NAME = "cached_playlist.m3u"
    }

    interface PlaylistCallback {
        fun onSuccess(channels: List<Channel>, isFromCache: Boolean)
        fun onError(error: String)
    }

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun loadPlaylist(callback: PlaylistCallback) {
        executor.execute {
            try {
                val url = URL(PLAYLIST_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line).append("\n")
                    }
                    reader.close()
                    val rawContent = stringBuilder.toString()
                    saveToCache(rawContent)

                    val channels = M3UParser.parse(rawContent)
                    if (channels.isNotEmpty()) {
                        mainHandler.post { callback.onSuccess(channels, false) }
                    } else {
                        loadFromCacheOrError(callback, "Playlist vazia.")
                    }
                } else {
                    loadFromCacheOrError(callback, "Erro HTTP: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                loadFromCacheOrError(callback, "Erro de rede: ${e.localizedMessage}")
            }
        }
    }

    private fun loadFromCacheOrError(callback: PlaylistCallback, errorMsg: String) {
        val cached = readFromCache()
        if (cached != null) {
            val channels = M3UParser.parse(cached)
            if (channels.isNotEmpty()) {
                mainHandler.post { callback.onSuccess(channels, true) }
                return
            }
        }
        mainHandler.post { callback.onError(errorMsg) }
    }

    private fun saveToCache(content: String) {
        try { File(context.cacheDir, CACHE_FILE_NAME).writeText(content) } catch (_: Exception) {}
    }

    private fun readFromCache(): String? {
        return try {
            val file = File(context.cacheDir, CACHE_FILE_NAME)
            if (file.exists()) file.readText() else null
        } catch (e: Exception) { null }
    }
}
