package com.antarjala.akasavani.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antarjala.akasavani.data.RadioStation
import com.antarjala.akasavani.data.RadioStationGroup

@Composable
fun RadioStationGroupCard(
    group: RadioStationGroup,
    currentStation: RadioStation?,
    isPlaying: Boolean,
    onStationClick: (RadioStation) -> Unit,
    onFavoriteChange: (RadioStation, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Group header (only show if group has a name)
            if (group.name.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }
            }

            // Station list
            AnimatedVisibility(
                visible = expanded || group.name.isEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    group.stations.forEach { station ->
                        RadioStationCard(
                            station = station,
                            isPlaying = station == currentStation && isPlaying,
                            onPlayClick = { onStationClick(station) },
                            onFavoriteChange = { isFavorite -> onFavoriteChange(station, isFavorite) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
} 