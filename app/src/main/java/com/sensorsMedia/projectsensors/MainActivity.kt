package com.sensorsMedia.projectsensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.musica1)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)


        val startButton = findViewById<Button>(R.id.startButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        startButton.setOnClickListener {
            mediaPlayer.start()
            statusText.text = getString(R.string.textMusic)
        }
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {

            val distance = event.values[0]

            if (distance == 0f) {
                // Objeto perto → PAUSAR
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            } else {
                // Objeto longe → RETOMAR
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}

