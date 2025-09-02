package com.developers.sprintsync.presentation.user_parameters.model

import com.developers.sprintsync.domain.user_profile.model.UserParameters

sealed class UserParametersUiState {
    data class Success(
        val draft: UserParametersDraft,
        val persisted: UserParameters?,
    ) : UserParametersUiState()

    data object Loading : UserParametersUiState()
    data object Empty : UserParametersUiState()
}