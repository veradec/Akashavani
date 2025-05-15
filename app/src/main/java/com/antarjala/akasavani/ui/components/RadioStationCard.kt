package com.antarjala.akasavani.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.antarjala.akasavani.data.RadioStation
import kotlin.math.abs

@Composable
fun RadioStationCard(
    station: RadioStation,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    val scale by animateFloatAsState(
        targetValue = if (station.isFavorite) 1.1f else 1f,
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(offsetX) > 100) {
                                onFavoriteChange(!station.isFavorite)
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (offsetX + dragAmount >= 0) {
                                offsetX += dragAmount
                            }
                        }
                    )
                }
                .scale(scale),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (station.isFavorite) 
                    MaterialTheme.colorScheme.tertiaryContainer 
                else 
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (station.needsHeaders) {
                        Text(
                            text = "Requires Headers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                IconButton(onClick = onPlayClick) {
                    Icon(
                        imageVector = if (isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }
            }
        }
        
        if (!station.isAccessible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.2f))
            )
        }
    }
} 