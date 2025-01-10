package com.developers.sprintsync.user_parameters.components.use_case

import com.developers.sprintsync.user_parameters.data.repository.UserParametersRepository
import javax.inject.Inject

class UseStatsPermissionUseCase
    @Inject
    constructor(
        private val repository: UserParametersRepository,
    ) {
        operator fun invoke() = repository.useStatsPermissionFlow

        suspend fun saveUseStatsPermission(permission: Boolean) = repository.saveUseStatsPermission(permission)
    }
