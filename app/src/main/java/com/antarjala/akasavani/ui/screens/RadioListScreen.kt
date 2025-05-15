package com.antarjala.akasavani.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.antarjala.akasavani.data.RadioStation
import com.antarjala.akasavani.data.RadioStationGroup
import com.antarjala.akasavani.data.RadioStations
import com.antarjala.akasavani.network.NetworkChecker
import com.antarjala.akasavani.ui.components.RadioStationGroupCard
import kotlinx.coroutines.launch

@Composable
fun RadioListScreen(
    modifier: Modifier = Modifier,
    currentStation: RadioStation? = null,
    isPlaying: Boolean = false,
    onStationClick: (RadioStation) -> Unit
) {
    var stationGroups by remember { mutableStateOf(RadioStations.stationGroups) }
    val scope = rememberCoroutineScope()
    val networkChecker = remember { NetworkChecker() }

    // Function to update favorite status
    fun updateFavorite(station: RadioStation, isFavorite: Boolean) {
        stationGroups = stationGroups.map { group ->
            group.copy(
                stations = group.stations.map { s ->
                    if (s == station) s.copy(isFavorite = isFavorite) else s
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            stationGroups = stationGroups.map { group ->
                group.copy(
                    stations = group.stations.map { station ->
                        val isAccessible = networkChecker.isUrlAccessible(station.streamUrl)
                        station.copy(isAccessible = isAccessible)
                    }
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Favorites section
            val favoriteStations = stationGroups.flatMap { it.stations }.filter { it.isFavorite }
            if (favoriteStations.isNotEmpty()) {
                item {
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(favoriteStations) { station ->
                    RadioStationGroupCard(
                        group = RadioStationGroup("", listOf(station)),
                        currentStation = currentStation,
                        isPlaying = isPlaying,
                        onStationClick = onStationClick,
                        onFavoriteChange = { s, isFavorite -> updateFavorite(s, isFavorite) }
                    )
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }

            // Regular station groups
            items(stationGroups) { group ->
                RadioStationGroupCard(
                    group = group,
                    currentStation = currentStation,
                    isPlaying = isPlaying,
                    onStationClick = onStationClick,
                    onFavoriteChange = { station, isFavorite -> updateFavorite(station, isFavorite) }
                )
            }
            
            // Add padding at the bottom to prevent content from being hidden behind the footer
            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Footer overlay
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Text(
                text = "A work of Verade Productions",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
} 