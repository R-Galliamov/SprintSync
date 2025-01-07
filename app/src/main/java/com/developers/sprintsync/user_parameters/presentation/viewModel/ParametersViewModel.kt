package com.developers.sprintsync.user_parameters.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.user_parameters.components.use_case.PersonalParametersUseCase
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
        private val personalParameters: PersonalParametersUseCase,
    ) : ViewModel() {
        val parametersFlow = personalParameters.parametersFlow.map { ParametersFormatter.format(it) }

        fun saveParameters(parameters: UserParameters) {
            viewModelScope.launch { personalParameters.saveParameters(parameters) }
        }

        companion object {
            private const val TAG = "ParametersViewModel"
        }
    }
