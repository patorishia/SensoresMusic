package com.sensorsMedia.projectsensors.ui.theme

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.sensorsMedia.projectsensors.Song

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    // PLAYER
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()

    // ESTADO
    var currentSongIndex by mutableIntStateOf(0)
    var isPlaying by mutableStateOf(false)

    // SENSOR
    var canTrigger by mutableStateOf(true)


    // FUNÇÃO PARA TOCAR MÚSICA
    fun playSong(song: Song) {
        val context = getApplication<Application>()
        val uri = "android.resource://${context.packageName}/${song.audioRes}".toUri()

        val mediaItem = MediaItem.fromUri(uri)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        exoPlayer.playWhenReady = true

        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
        })
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            isPlaying = false
        } else {
            exoPlayer.playWhenReady = true
            exoPlayer.play()
            isPlaying = true
        }

        Log.d("PLAYER", "exoPlayer.isPlaying = ${exoPlayer.isPlaying}")
    }

    fun nextSong(songs: List<Song>) {
        currentSongIndex = (currentSongIndex + 1) % songs.size
        playSong(songs[currentSongIndex])
    }

    fun previousSong(songs: List<Song>) {
        currentSongIndex =
            if (currentSongIndex == 0) songs.size - 1
            else currentSongIndex - 1

        playSong(songs[currentSongIndex])
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
