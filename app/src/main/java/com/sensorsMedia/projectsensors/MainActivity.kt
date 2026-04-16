package com.sensorsMedia.projectsensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.sensorsMedia.projectsensors.ui.theme.MusicScreen


data class Song(
    val title: String,
    val artist: String,
    val coverRes: Int,
    val audioRes: Int
)

@Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")
class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var accelerometer: Sensor? = null

    private val songs = listOf(
        Song("Horizonte", "Luz do Norte", R.drawable.capa1, R.raw.musica1),
        Song("Caminho", "Aurora Boreal", R.drawable.capa2, R.raw.musica2)
    )

    private var currentSongIndex by mutableStateOf(0)
    private var lastTiltTime = 0L

    var isPlaying by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exoPlayer = ExoPlayer.Builder(this).build()
        playSong(songs[currentSongIndex])

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            MaterialTheme {
                MusicScreen(
                    song = songs[currentSongIndex],
                    exoPlayer = exoPlayer
                )
            }

            MusicScreen(
                song = songs[currentSongIndex],
                exoPlayer = exoPlayer,
                isPlaying = isPlaying,
                onPlayPause = {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                        isPlaying = false
                    } else {
                        exoPlayer.play()
                        isPlaying = true
                    }
                },
                onNext = {
                    currentSongIndex = (currentSongIndex + 1) % songs.size
                    playSong(songs[currentSongIndex])
                    isPlaying = true
                },
                onPrevious = {
                    currentSongIndex =
                        if (currentSongIndex == 0) songs.size - 1 else currentSongIndex - 1
                    playSong(songs[currentSongIndex])
                    isPlaying = true
                }
            )

        }
    }

    private fun playSong(song: Song) {
        val uri = "android.resource://${packageName}/${song.audioRes}".toUri()
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {

            Sensor.TYPE_PROXIMITY -> {
                if (event.values[0] == 0f) exoPlayer.pause()
                else exoPlayer.play()
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val now = System.currentTimeMillis()

                if (now - lastTiltTime < 1000) return

                if (x > 5) {
                    currentSongIndex = (currentSongIndex + 1) % songs.size
                    playSong(songs[currentSongIndex])
                    lastTiltTime = now
                } else if (x < -5) {
                    currentSongIndex =
                        if (currentSongIndex == 0) songs.size - 1 else currentSongIndex - 1
                    playSong(songs[currentSongIndex])
                    lastTiltTime = now
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        proximitySensor?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        accelerometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}

