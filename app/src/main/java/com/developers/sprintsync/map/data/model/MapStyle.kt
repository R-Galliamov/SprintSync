package com.developers.sprintsync.map.data.model

import androidx.annotation.RawRes
import com.developers.sprintsync.R

enum class MapStyle(
    @RawRes val styleResId: Int,
) {
    UNLABELED(R.raw.map_style_unlabeled),
    MINIMAL(R.raw.map_style_minimal),
    DETAILED(R.raw.map_style_detailed),
}
