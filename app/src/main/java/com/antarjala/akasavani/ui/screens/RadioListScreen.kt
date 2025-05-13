package com.antarjala.akasavani.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antarjala.akasavani.data.RadioStation
import com.antarjala.akasavani.data.RadioStations
import com.antarjala.akasavani.network.NetworkChecker
import com.antarjala.akasavani.ui.components.RadioStationCard
import kotlinx.coroutines.launch

@Composable
fun RadioListScreen(
    modifier: Modifier = Modifier,
    currentStation: RadioStation? = null,
    isPlaying: Boolean = false,
    onStationClick: (RadioStation) -> Unit
) {
    var stations by remember { mutableStateOf(RadioStations.stations) }
    val scope = rememberCoroutineScope()
    val networkChecker = remember { NetworkChecker() }

    LaunchedEffect(Unit) {
        scope.launch {
            stations = stations.map { station ->
                val isAccessible = networkChecker.isUrlAccessible(station.streamUrl)
                station.copy(isAccessible = isAccessible)
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(stations) { station ->
            RadioStationCard(
                station = station,
                isPlaying = station == currentStation && isPlaying,
                onPlayClick = {
                    onStationClick(station)
                }
            )
        }
    }
} 