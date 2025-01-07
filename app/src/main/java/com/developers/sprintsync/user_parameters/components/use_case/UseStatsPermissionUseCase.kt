package com.developers.sprintsync.user_parameters.components.use_case

import com.developers.sprintsync.user_parameters.data.repository.UserPreferencesRepository
import javax.inject.Inject

class UseStatsPermissionUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepository,
    ) {
        operator fun invoke() = repository.useStatsPermissionFlow

        suspend fun saveUseStatsPermission(permission: Boolean) = repository.saveUseStatsPermission(permission)
    }
