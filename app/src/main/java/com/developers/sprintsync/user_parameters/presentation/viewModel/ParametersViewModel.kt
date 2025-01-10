package com.developers.sprintsync.user_parameters.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.user_parameters.components.use_case.GetUserParametersUseCase
import com.developers.sprintsync.user_parameters.components.use_case.SaveUserParametersUseCase
import com.developers.sprintsync.user_parameters.model.UserParameters
import com.developers.sprintsync.user_parameters.presentation.util.formatter.ParametersFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel
    @Inject
    constructor(
        getParameters: GetUserParametersUseCase,
        private val saveUserParameters: SaveUserParametersUseCase,
    ) : ViewModel() {
        val parametersFlow = getParameters.parametersFlow.map { ParametersFormatter.format(it) }

        // getParameters().map { ParametersFormatter.format(it) }

        fun saveParameters(parameters: UserParameters) {
            viewModelScope.launch { saveUserParameters(parameters) }
        }

        companion object {
            private const val TAG = "ParametersViewModel"
        }
    }
