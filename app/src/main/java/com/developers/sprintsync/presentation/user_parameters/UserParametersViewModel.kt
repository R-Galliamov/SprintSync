package com.developers.sprintsync.presentation.user_parameters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.formatter.DateFormatter
import com.developers.sprintsync.domain.user_parameters.model.Gender
import com.developers.sprintsync.domain.user_parameters.model.UserParameters
import com.developers.sprintsync.domain.user_parameters.model.WellnessGoal
import com.developers.sprintsync.domain.user_parameters.use_case.GetUserParametersUseCase
import com.developers.sprintsync.domain.user_parameters.use_case.SaveUserParametersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserParametersViewModel
    @Inject
    constructor(
        getParameters: GetUserParametersUseCase,
        private val saveUserParameters: SaveUserParametersUseCase,
    ) : ViewModel() {
        val parametersFlow = getParameters.parametersFlow.map { UserParametersDisplayMode.create(it) }

        // getParameters().map { ParametersFormatter.format(it) }

        fun saveParameters(parameters: UserParameters) {
            viewModelScope.launch { saveUserParameters(parameters) }
        }
    }

data class UserParametersDisplayMode(
    val gender: Gender,
    val birthDate: String,
    val birthDateTimestamp: Long,
    val weight: String,
    val wellnessGoal: WellnessGoal,
    val useStatsPermission: Boolean,
) {
    companion object {
        fun create(parameters: UserParameters) = Formatter.format(parameters)
    }

    private object Formatter {
        fun format(parameters: UserParameters): UserParametersDisplayMode =
            UserParametersDisplayMode(
                gender = parameters.gender,
                birthDate =
                    DateFormatter.formatDate(
                        parameters.birthDateTimestamp,
                        DateFormatter.Pattern.DAY_MONTH_YEAR,
                    ),
                birthDateTimestamp = parameters.birthDateTimestamp,
                weight = parameters.weight.toString(),
                wellnessGoal = parameters.wellnessGoal,
                useStatsPermission = parameters.useStatsPermission,
            )
    }
}
