package com.antarjala.akasavani.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
    currentStation: RadioStation? = null,
    isPlaying: Boolean = false,
    onStationClick: (RadioStation) -> Unit,
    onFavoriteChange: (RadioStation, Boolean) -> Unit,
    isExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(isExpanded) }

    // Update expanded state when isExpanded parameter changes
    LaunchedEffect(isExpanded) {
        expanded = isExpanded
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (group.name.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded || group.name.isEmpty(),
                enter = expandVertically(
                    animationSpec = tween(300)
                ) + fadeIn(
                    animationSpec = tween(300)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(300)
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            ) {
                Column(
                    modifier = Modifier.padding(top = if (group.name.isNotEmpty()) 8.dp else 0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    group.stations.forEach { station ->
                        RadioStationCard(
                            station = station,
                            isPlaying = station == currentStation && isPlaying,
                            onPlayClick = { onStationClick(station) },
                            onFavoriteChange = { isFavorite -> onFavoriteChange(station, isFavorite) }
                        )
                    }
                }
            }
        }
    }
} 