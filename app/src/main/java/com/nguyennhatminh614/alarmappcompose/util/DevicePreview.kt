package com.nguyennhatminh614.alarmappcompose.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * Custom annotation để preview UI trên nhiều thiết bị và chế độ sáng/tối.
 */
// Phone
@Preview(name = "1. Phone - Light", device = Devices.PIXEL_4, showSystemUi = true, showBackground = true)
@Preview(name = "2. Phone - Dark", device = Devices.PIXEL_4, uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true, showBackground = true)
annotation class DevicePreview