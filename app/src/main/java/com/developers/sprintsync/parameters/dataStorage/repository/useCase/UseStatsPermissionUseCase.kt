package com.developers.sprintsync.parameters.dataStorage.repository.useCase

import com.developers.sprintsync.parameters.dataStorage.repository.UserPreferencesRepository
import javax.inject.Inject

class UseStatsPermissionUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepository,
    ) {
        operator fun invoke() = repository.useStatsPermissionFlow

        suspend fun saveUseStatsPermission(permission: Boolean) = repository.saveUseStatsPermission(permission)
    }
