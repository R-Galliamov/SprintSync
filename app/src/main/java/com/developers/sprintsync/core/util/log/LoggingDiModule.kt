package com.developers.sprintsync.core.util.log

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LoggingDiModule {

    @Provides
    @Singleton
    fun provideLogger(): AppLogger = TimberAppLogger()

    @Provides
    @Singleton
    @Named(DEBUG_TREE_KEY)
    fun provideDebugTree() : Timber.Tree = DefaultDebugTree()

    @Provides
    @Singleton
    @Named(RELEASE_TREE_KEY)
    fun provideReleaseTree() : Timber.Tree = CrashlyticsReportingTree()

    @Provides
    @Singleton
    fun provideTimberConfigurator(
        @Named(DEBUG_TREE_KEY) debugTree: Timber.Tree,
        @Named(RELEASE_TREE_KEY) releaseTree: Timber.Tree,
    ): TimberConfigurator = TimberConfigurator(debugTree = debugTree, releaseTree = releaseTree)

    companion object {
        private const val DEBUG_TREE_KEY = "debug_tree"
        private const val RELEASE_TREE_KEY = "release_tree"
    }

}