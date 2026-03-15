package com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmRingAccent
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmRingOnBackground
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmRingSwipeHandle
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmRingSwipeTrack
import kotlin.math.roundToInt

private const val DISMISS_THRESHOLD = 0.7f

@Composable
fun SwipeToDismiss(
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var offsetPx by remember { mutableFloatStateOf(0f) }
    val animatedOffset = remember { Animatable(0f) }
    var maxOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val currentOffset = if (isDragging) offsetPx else animatedOffset.value
    val textAlpha = if (maxOffset > 0f) {
        (1f - (currentOffset / maxOffset)).coerceIn(0f, 1f)
    } else {
        1f
    }

    val draggableState = rememberDraggableState { delta ->
        offsetPx = (offsetPx + delta).coerceIn(0f, maxOffset)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(AlarmRingSwipeTrack)
            .onSizeChanged { size ->
                val handleSizePx = size.height.toFloat() * 56f / 64f
                val newMax = size.width.toFloat() - handleSizePx
                if (newMax > 0) maxOffset = newMax
            },
    ) {
        Text(
            text = stringResource(R.string.alarm_ring_swipe_to_turn_off),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AlarmRingAccent,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 56.dp)
                .alpha(textAlpha),
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(currentOffset.roundToInt(), 0) }
                .size(56.dp)
                .align(Alignment.CenterStart)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStarted = {
                        isDragging = true
                    },
                    onDragStopped = {
                        isDragging = false
                        val finalOffset = offsetPx
                        animatedOffset.snapTo(finalOffset)
                        if (maxOffset > 0f && finalOffset >= maxOffset * DISMISS_THRESHOLD) {
                            animatedOffset.animateTo(maxOffset)
                            onDismissed()
                        } else {
                            animatedOffset.animateTo(0f)
                            offsetPx = 0f
                        }
                    },
                )
                .padding(4.dp)
                .clip(CircleShape)
                .background(AlarmRingSwipeHandle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.alarm_ring_swipe_to_turn_off),
                tint = AlarmRingOnBackground,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
