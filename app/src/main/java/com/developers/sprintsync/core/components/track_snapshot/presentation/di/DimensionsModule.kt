package com.developers.sprintsync.core.components.track_snapshot.presentation.di

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.core.components.track_snapshot.presentation.model.SnapshotDimensions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DimensionsModule {
    @Provides
    fun provideSnapshotDimensions(
        @ApplicationContext context: Context,
    ): SnapshotDimensions {
        val resources = context.resources
        val width =
            resources.getDimensionPixelSize(R.dimen.imageSize_mapPreview_width)
        val height =
            resources.getDimensionPixelSize(R.dimen.imageSize_mapPreview_height)
        return SnapshotDimensions(width, height)
    }
}
