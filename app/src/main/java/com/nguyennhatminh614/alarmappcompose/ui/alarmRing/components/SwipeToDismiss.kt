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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingAccent
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingOnBackground
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingSwipeHandle
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingSwipeTrack
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val DISMISS_THRESHOLD = 0.7f

@Composable
fun SwipeToDismiss(
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val offsetAnimatable = remember { Animatable(0f) }
    var maxOffset by remember { mutableFloatStateOf(0f) }

    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            val newValue = (offsetAnimatable.value + delta).coerceIn(0f, maxOffset)
            offsetAnimatable.snapTo(newValue)
        }
    }

    val currentOffset = offsetAnimatable.value
    val textAlpha = if (maxOffset > 0f) {
        (1f - (currentOffset / maxOffset)).coerceIn(0f, 1f)
    } else {
        1f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(AlarmRingSwipeTrack)
            .onSizeChanged { size ->
                val handleSizePx = 56.dp.value * size.height / 64.dp.value
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
                .padding(4.dp)
                .clip(CircleShape)
                .background(AlarmRingSwipeHandle)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (maxOffset > 0f && currentOffset >= maxOffset * DISMISS_THRESHOLD) {
                            offsetAnimatable.animateTo(maxOffset)
                            onDismissed()
                        } else {
                            offsetAnimatable.animateTo(0f)
                        }
                    },
                ),
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
