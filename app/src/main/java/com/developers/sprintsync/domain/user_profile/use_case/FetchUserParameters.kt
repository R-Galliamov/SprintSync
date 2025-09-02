package com.developers.sprintsync.domain.user_profile.use_case

import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.user_profile.UserParametersRepository
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchUserParameters
@Inject
constructor(
    private val repository: UserParametersRepository,
) {
    operator fun invoke(): Flow<Resource<UserParameters>> = repository.stream()
}
