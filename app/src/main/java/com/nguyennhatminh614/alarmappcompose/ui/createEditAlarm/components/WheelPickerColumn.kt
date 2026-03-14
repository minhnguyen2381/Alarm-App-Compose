package com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged

private const val VIRTUAL_MULTIPLIER = 1000

@Composable
fun WheelPickerColumn(
    values: ImmutableList<String>,
    selectedIndex: Int,
    onSelectedIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemCount = values.size
    val virtualCount = itemCount * VIRTUAL_MULTIPLIER
    val midpoint = (VIRTUAL_MULTIPLIER / 2) * itemCount

    // Initial position: center the selected item (it's the 2nd visible item, so -1)
    val initialFirstVisible = midpoint + selectedIndex - 1
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = initialFirstVisible)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState)

    // Sync: scroll → parent (emit selection when scroll stops)
    LaunchedEffect(Unit) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex to lazyListState.isScrollInProgress
        }
            .distinctUntilChanged()
            .collect { (firstVisible, isScrolling) ->
                if (!isScrolling) {
                    val realIndex = (firstVisible + 1) % itemCount
                    if (realIndex != selectedIndex) {
                        onSelectedIndexChanged(realIndex)
                    }
                }
            }
    }

    val itemHeight = 72.dp

    LazyColumn(
        state = lazyListState,
        flingBehavior = snapFlingBehavior,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .height(itemHeight * 3)
            .clipToBounds(),
    ) {
        items(count = virtualCount) { virtualIndex ->
            val realIndex = virtualIndex % itemCount
            val isSelected = virtualIndex == lazyListState.firstVisibleItemIndex + 1

            WheelPickerItem(
                text = values[realIndex],
                isSelected = isSelected,
                modifier = Modifier.height(itemHeight),
            )
        }
    }
}

@Composable
private fun WheelPickerItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isSelected) {
        Box(
            modifier = modifier
                .width(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 44.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    } else {
        Box(
            modifier = modifier
                .width(100.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WheelPickerColumnPreview() {
    AlarmAppComposeTheme {
        WheelPickerColumn(
            values = (0..59).map { "%02d".format(it) }.toImmutableList(),
            selectedIndex = 30,
            onSelectedIndexChanged = {},
        )
    }
}
