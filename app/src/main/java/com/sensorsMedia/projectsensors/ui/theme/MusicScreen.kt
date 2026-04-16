package com.sensorsMedia.projectsensors.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun MusicScreen(
    song: Song,
    exoPlayer: ExoPlayer?,
    onNext: (() -> Unit)? = null,
    onPrevious: (() -> Unit)? = null,
    onPlayPause: (() -> Unit)? = null,
    isPlaying: Boolean = true
) {
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    LaunchedEffect(exoPlayer) {
        while (true) {
            if (exoPlayer != null && exoPlayer.isPlaying) {
                position = exoPlayer.currentPosition
                duration = exoPlayer.duration.coerceAtLeast(1L)
            }
            kotlinx.coroutines.delay(200)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // CAPA
        Image(
            painter = painterResource(id = song.coverRes),
            contentDescription = null,
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
        )

        // TÍTULO
        Text(
            text = song.title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )

        // ARTISTA
        Text(
            text = song.artist,
            color = Color.LightGray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // BARRA DE PROGRESSO
        Slider(
            value = if (duration > 0) position.toFloat() / duration.toFloat() else 0f,
            onValueChange = { newValue ->
                if (exoPlayer != null) {
                    val newPos = (newValue * duration).toLong()
                    exoPlayer.seekTo(newPos)
                    position = newPos
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )


        // TEMPO ATUAL / TOTAL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(position), color = Color.LightGray, fontSize = 12.sp)
            Text(formatTime(duration), color = Color.LightGray, fontSize = 12.sp)
        }


        Spacer(modifier = Modifier.height(32.dp))

        // BOTÕES
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            // PREVIOUS
            IconButton(onClick = { onPrevious?.invoke() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_previous),
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            // PLAY / PAUSE
            IconButton(onClick = { onPlayPause?.invoke() }) {
                Icon(
                    painter = painterResource(
                        id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = "PlayPause",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            // NEXT
            IconButton(onClick = { onNext?.invoke() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // PLAYER VIEW (oculto)
        if (exoPlayer != null) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = exoPlayer
                        this.useController = false
                    }
                },
                modifier = Modifier.size(1.dp)
            )
        }
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
            exoPlayer = null,
            isPlaying = false
        )
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
