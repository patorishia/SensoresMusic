package com.sensorsMedia.projectsensors.ui.theme

    import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.sensorsMedia.projectsensors.R
import com.sensorsMedia.projectsensors.Song

@Composable
    fun MusicScreen(song: Song, exoPlayer: ExoPlayer?) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Sensors Music",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Controlo por Sensores",
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Image(
                painter = painterResource(id = song.coverRes),
                contentDescription = null,
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
            )

            Text(
                text = song.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = song.artist,
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            if (exoPlayer != null) {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            this.player = exoPlayer
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PlayerView Preview", color = Color.White)
                }
            }

            Text(
                text = "Inclinar: próxima / anterior\nCobrir sensor: pausa / retoma",
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MusicScreenPreview() {
        val fakeSong = Song(
            title = "Horizonte",
            artist = "Luz do Norte",
            coverRes = R.drawable.capa1,
            audioRes = R.raw.musica1
        )

        MaterialTheme {
            MusicScreen(
                song = fakeSong,
                exoPlayer = null
            )
        }
    }