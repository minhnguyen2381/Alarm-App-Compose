package com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun WheelPickerColumn(
    values: List<String>,
    selectedIndex: Int,
    onSelectedIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }
    var itemHeightPx = remember { 0f }

    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            offsetY.snapTo(offsetY.value + delta)
        }
    }

    // Wrap index into valid range
    fun wrapIndex(index: Int): Int {
        val size = values.size
        return ((index % size) + size) % size
    }

    val prevIndex = wrapIndex(selectedIndex - 1)
    val nextIndex = wrapIndex(selectedIndex + 1)

    Column(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { size ->
                // Each visible item is roughly 1/3 of column height
                itemHeightPx = size.height / 3f
            }
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    // Determine how many items to snap
                    val threshold = itemHeightPx * 0.3f
                    val currentOffset = offsetY.value
                    val indexDelta = when {
                        currentOffset > threshold -> -1  // dragged down → previous
                        currentOffset < -threshold -> 1   // dragged up → next
                        else -> 0
                    }

                    // Animate back to zero
                    scope.launch {
                        offsetY.animateTo(0f, spring())
                    }

                    if (indexDelta != 0) {
                        onSelectedIndexChanged(wrapIndex(selectedIndex + indexDelta))
                    }
                },
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Previous item
        Text(
            text = values[prevIndex],
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .clickable { onSelectedIndexChanged(prevIndex) },
        )

        // Current item (highlighted box)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .offset { IntOffset(0, offsetY.value.roundToInt()) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = values[selectedIndex],
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // Next item
        Text(
            text = values[nextIndex],
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .padding(top = 8.dp)
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .clickable { onSelectedIndexChanged(nextIndex) },
        )
    }
}
