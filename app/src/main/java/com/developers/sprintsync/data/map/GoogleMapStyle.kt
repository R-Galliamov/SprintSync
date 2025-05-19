package com.developers.sprintsync.data.map

import androidx.annotation.RawRes
import com.developers.sprintsync.R

enum class GoogleMapStyle(
    @RawRes val styleResId: Int,
) {
    UNLABELED(R.raw.map_style_unlabeled),
    MINIMAL(R.raw.map_style_minimal),
    DETAILED(R.raw.map_style_detailed),
}
