package com.sensorsMedia.projectsensors

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.musica1)

        val startButton = findViewById<Button>(R.id.startButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        startButton.setOnClickListener {
            mediaPlayer.start()
            statusText.text = getString(R.string.textMusic)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}

