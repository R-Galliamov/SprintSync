package com.developers.sprintsync.user_parameters.components.cache.data.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.developers.sprintsync.user_parameters.components.cache.data.model.CachedUserParameters
import com.developers.sprintsync.user_parameters.components.cache.data.model.KeyConstants
import com.developers.sprintsync.user_parameters.data.repository.DefaultUserParams
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CachedUserParametersModule {
    @Provides
    @Singleton
    fun prodideCachedUserParametersSharedPrefs(
        @ApplicationContext app: Context,
    ) = app.getSharedPreferences(KeyConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideCachedUserParameters(sharedPref: SharedPreferences): CachedUserParameters {
        val userWeightKg = sharedPref.getFloat(KeyConstants.USER_WEIGHT_KEY, DefaultUserParams.DEFAULT_WEIGHT)
        return CachedUserParameters(userWeightKg)
    }
}
