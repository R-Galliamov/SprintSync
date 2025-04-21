package com.developers.sprintsync.data.track_preview.cropper

import android.content.Context
import com.developers.sprintsync.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DimensionsModule {
    @Provides
    fun providePreviewDimensions(
        @ApplicationContext context: Context,
    ): TrackPreviewDimensions {
        val resources = context.resources
        val width =
            resources.getDimensionPixelSize(R.dimen.imageSize_mapPreview_width)
        val height =
            resources.getDimensionPixelSize(R.dimen.imageSize_mapPreview_height)
        return TrackPreviewDimensions(width, height)
    }
}
