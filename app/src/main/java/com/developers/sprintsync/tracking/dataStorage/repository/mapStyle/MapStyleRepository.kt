package com.developers.sprintsync.tracking.dataStorage.repository.mapStyle

import android.content.Context
import android.util.Log
import com.developers.sprintsync.R
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MapStyleRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun getMapStyle(style: MapStyle): MapStyleOptions {
            val resourceId = getResourceId(style)
            return try {
                MapStyleOptions.loadRawResourceStyle(context, resourceId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load map style", e)
                throw e
            }
        }

        private fun getResourceId(style: MapStyle): Int =
            when (style) {
                MapStyle.UNLABELED -> R.raw.map_style_unlabeled
                MapStyle.MINIMAL -> R.raw.map_style_minimal
                MapStyle.DETAILED -> R.raw.map_style_detailed
            }

        companion object {
            private const val TAG = "MyStack: MapStyleRepository"
        }
    }
