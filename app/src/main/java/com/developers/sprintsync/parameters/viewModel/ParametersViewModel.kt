package com.developers.sprintsync.parameters.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.parameters.dataStorage.repository.useCase.PersonalParametersUseCase
import com.developers.sprintsync.parameters.model.UserParameters
import com.developers.sprintsync.parameters.util.formatter.ParametersFormatter
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
