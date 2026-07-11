package com.liquidglass.musicplayer.data.remote

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.liquidglass.musicplayer.data.local.TrackDao
import com.liquidglass.musicplayer.data.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao,
    private val authManager: SpotifyAuthManager
) {

    private val _downloadQueue = MutableStateFlow<List<Track>>(emptyList())
    val downloadQueue: StateFlow<List<Track>> = _downloadQueue.asStateFlow()

    private val _activeDownloads = MutableStateFlow<Map<String, Float>>(emptyMap())
    val activeDownloads: StateFlow<Map<String, Float>> = _activeDownloads.asStateFlow()

    suspend fun downloadTrack(track: Track, spotifyApi: SpotifyApi): Result<String> = runCatching {
        val previewUrl = track.previewUrl
            ?: throw Exception("No preview available for offline download")

        val downloadDir = context.getExternalFilesDir(null)?.let {
            java.io.File(it, "downloads")
        } ?: context.filesDir.let { java.io.File(it, "downloads") }

        downloadDir.mkdirs()

        val fileName = "${track.id}.mp3"
        val outputFile = java.io.File(downloadDir, fileName)

        if (outputFile.exists()) {
            trackDao.markDownloaded(track.id, outputFile.absolutePath)
            return@runCatching outputFile.absolutePath
        }

        val client = okhttp3.OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url(previewUrl)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Download failed: ${response.code}")
        }

        response.body?.byteStream()?.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        trackDao.markDownloaded(track.id, outputFile.absolutePath)
        outputFile.absolutePath
    }

    suspend fun deleteDownload(track: Track): Result<Unit> = runCatching {
        track.downloadPath?.let { path ->
            val file = java.io.File(path)
            if (file.exists()) file.delete()
        }
        trackDao.markNotDownloaded(track.id)
    }

    fun getDownloadDir(): java.io.File {
        val dir = context.getExternalFilesDir(null)?.let {
            java.io.File(it, "downloads")
        } ?: context.filesDir.let { java.io.File(it, "downloads") }
        dir.mkdirs()
        return dir
    }

    fun getOfflineTracks(): StateFlow<List<Track>> {
        return MutableStateFlow(emptyList()) // Will be populated by ViewModel
    }

    suspend fun isDownloaded(trackId: String): Boolean {
        return trackDao.getTrack(trackId)?.isDownloaded == true
    }

    suspend fun getLocalFileForTrack(track: Track): java.io.File? {
        return track.downloadPath?.let { path ->
            val file = java.io.File(path)
            if (file.exists()) file else null
        }
    }
}
