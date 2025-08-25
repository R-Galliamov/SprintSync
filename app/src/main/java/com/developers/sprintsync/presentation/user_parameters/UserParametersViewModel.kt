package com.developers.sprintsync.presentation.user_parameters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.formatter.DateFormatter
import com.developers.sprintsync.domain.user_profile.model.Sex
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import com.developers.sprintsync.domain.user_profile.model.WellnessGoal
import com.developers.sprintsync.domain.user_profile.use_case.UpdateUserParametersUseCase
import com.developers.sprintsync.domain.user_profile.use_case.UserParametersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserParametersViewModel
@Inject
constructor(
    userParametersUseCase: UserParametersUseCase,
    private val updateUserParameters: UpdateUserParametersUseCase,
) : ViewModel() {
    val parametersFlow = userParametersUseCase().map { UserParametersDisplayMode.create(it) }

    fun saveParameters(parameters: UserParameters) {
        viewModelScope.launch { updateUserParameters(parameters) }
    }
}

data class UserParametersDisplayMode(
    val sex: Sex,
    val birthDate: String,
    val birthDateTimestamp: Long,
    val weight: String,
) {
    companion object {
        fun create(parameters: UserParameters) = Formatter.format(parameters)
    }

    private object Formatter {
        fun format(parameters: UserParameters): UserParametersDisplayMode =
            UserParametersDisplayMode(
                sex = parameters.sex,
                birthDate =
                    DateFormatter.formatDate(
                        parameters.birthDateTimestamp,
                        DateFormatter.Pattern.DAY_MONTH_YEAR,
                    ),
                birthDateTimestamp = parameters.birthDateTimestamp,
                weight = parameters.weightKg.toString(),
            )
    }
}
