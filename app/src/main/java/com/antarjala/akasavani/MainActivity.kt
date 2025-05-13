package com.antarjala.akasavani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.antarjala.akasavani.data.RadioStation
import com.antarjala.akasavani.player.RadioPlayer
import com.antarjala.akasavani.ui.screens.RadioListScreen
import com.antarjala.akasavani.ui.theme.AkasavaniTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var radioPlayer: RadioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        radioPlayer = RadioPlayer(this)
        enableEdgeToEdge()
        setContent {
            AkasavaniTheme {
                var currentStation by remember { mutableStateOf<RadioStation?>(null) }
                var isPlaying by remember { mutableStateOf(false) }

                // Observe player state changes
                LaunchedEffect(Unit) {
                    while (true) {
                        isPlaying = radioPlayer.isPlaying()
                        currentStation = radioPlayer.getCurrentStation()
                        delay(100) // Check every 100ms
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RadioListScreen(
                        modifier = Modifier.padding(innerPadding),
                        currentStation = currentStation,
                        isPlaying = isPlaying,
                        onStationClick = { station ->
                            radioPlayer.togglePlayPause(station)
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        radioPlayer.release()
    }
}

@Preview(showBackground = true)
@Composable
fun RadioListScreenPreview() {
    AkasavaniTheme {
        RadioListScreen(
            onStationClick = {}
        )
    }
}