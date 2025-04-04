package com.developers.sprintsync.domain.user_parameters.use_case

import com.developers.sprintsync.data.user_parameters.repository.UserParametersRepository
import javax.inject.Inject

class UseStatsPermissionUseCase
    @Inject
    constructor(
        private val repository: UserParametersRepository,
    ) {
        operator fun invoke() = repository.useStatsPermissionFlow

        suspend fun saveUseStatsPermission(permission: Boolean) = repository.saveUseStatsPermission(permission)
    }
