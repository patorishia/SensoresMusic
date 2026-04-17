package com.sensorsMedia.projectsensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import com.sensorsMedia.projectsensors.ui.theme.MusicScreen
import com.sensorsMedia.projectsensors.ui.theme.MusicViewModel

data class Song(
    val title: String,
    val artist: String,
    val coverRes: Int,
    val audioRes: Int
)

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var proximitySensor: Sensor? = null

    private val songs = listOf(
        Song("Horizonte", "Luz do Norte", R.drawable.capa1, R.raw.musica1),
        Song("Caminho", "Aurora Boreal", R.drawable.capa2, R.raw.musica2)
    )

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        setContent {
            MaterialTheme {

                LaunchedEffect(Unit) {
                    viewModel.playSong(songs[viewModel.currentSongIndex])
                }
                MusicScreen(
                    song = songs[viewModel.currentSongIndex],
                    exoPlayer = viewModel.exoPlayer,
                    isPlaying = viewModel.isPlaying,
                    onPlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.nextSong(songs) },
                    onPrevious = { viewModel.previousSong(songs) }
                )
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        // 🔥 SENSOR DE PROXIMIDADE
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]

            if (distance < (proximitySensor?.maximumRange ?: 1f)){
                // perto → pausa
                viewModel.exoPlayer.pause()
                viewModel.isPlaying = false
            } else {
                // longe → toca
                viewModel.exoPlayer.play()
                viewModel.isPlaying = true
            }
            return
        }

        // 🔥 ACELERÓMETRO (o teu código atual)
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]

            if (x in -3f..3f) {
                viewModel.canTrigger = true
                return
            }

            if (!viewModel.canTrigger) return

            if (x > 6f) {
                viewModel.nextSong(songs)
                viewModel.canTrigger = false
            } else if (x < -6f) {
                viewModel.previousSong(songs)
                viewModel.canTrigger = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}

