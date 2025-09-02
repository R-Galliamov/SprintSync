package com.developers.sprintsync.presentation.user_parameters.di

import com.developers.sprintsync.core.util.view.DatePickerFactory
import com.developers.sprintsync.core.util.view.DefDatePickerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
abstract class UserParametersDiModule {

    @Binds
    @FragmentScoped
    abstract fun bindDatePickerFactory(impl: DefDatePickerFactory): DatePickerFactory
}