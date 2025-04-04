package com.developers.sprintsync.data.user_parameters.cache.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.developers.sprintsync.data.user_parameters.cache.CachedUserParameters
import com.developers.sprintsync.data.user_parameters.cache.KeyConstants
import com.developers.sprintsync.data.user_parameters.repository.DefaultUserParams
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
    fun provideCachedUserParametersSharedPrefs(
        @ApplicationContext app: Context,
    ) = app.getSharedPreferences(KeyConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideCachedUserParameters(sharedPref: SharedPreferences): CachedUserParameters {
        val userWeightKg = sharedPref.getFloat(KeyConstants.USER_WEIGHT_KEY, DefaultUserParams.DEFAULT_WEIGHT)
        return CachedUserParameters(userWeightKg)
    }
}
