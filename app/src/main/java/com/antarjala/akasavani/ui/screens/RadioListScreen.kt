package com.antarjala.akasavani.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.antarjala.akasavani.data.RadioStation
import com.antarjala.akasavani.data.RadioStationGroup
import com.antarjala.akasavani.data.RadioStations
import com.antarjala.akasavani.network.NetworkChecker
import com.antarjala.akasavani.ui.components.RadioStationGroupCard
import com.antarjala.akasavani.ui.components.SearchBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun RadioListScreen(
    modifier: Modifier = Modifier,
    currentStation: RadioStation? = null,
    isPlaying: Boolean = false,
    onStationClick: (RadioStation) -> Unit
) {
    var stationGroups by remember { mutableStateOf(RadioStations.stationGroups) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var expandedGroups by remember { mutableStateOf(setOf<String>()) }
    val scope = rememberCoroutineScope()
    val networkChecker = remember { NetworkChecker() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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

    // Function to filter stations based on search query
    fun filterStations(groups: List<RadioStationGroup>): List<RadioStationGroup> {
        if (searchQuery.isEmpty()) return groups
        return groups.map { group ->
            group.copy(
                stations = group.stations.filter { station ->
                    station.name.contains(searchQuery, ignoreCase = true)
                }
            )
        }.filter { it.stations.isNotEmpty() }
    }

    // Function to handle pull-to-refresh
    fun handlePullToRefresh() {
        scope.launch {
            isSearchVisible = true
        }
    }

    // Effect to handle search query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            expandedGroups = stationGroups.map { it.name }.toSet()
        } else {
            expandedGroups = emptySet()
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

    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = { handlePullToRefresh() }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search bar with optimized animation
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Handle search */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
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
                items(filterStations(stationGroups)) { group ->
                    RadioStationGroupCard(
                        group = group,
                        currentStation = currentStation,
                        isPlaying = isPlaying,
                        onStationClick = onStationClick,
                        onFavoriteChange = { station, isFavorite -> updateFavorite(station, isFavorite) },
                        isExpanded = expandedGroups.contains(group.name)
                    )
                }
                
                // Add padding at the bottom to prevent content from being hidden behind the footer
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
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