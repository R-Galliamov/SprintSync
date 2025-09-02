package com.developers.sprintsync.presentation.user_parameters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.extension.toFloatOrNullLocale
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.user_profile.model.Sex
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import com.developers.sprintsync.domain.user_profile.use_case.FetchUserParameters
import com.developers.sprintsync.domain.user_profile.use_case.SaveResult
import com.developers.sprintsync.domain.user_profile.use_case.SaveUserParameters
import com.developers.sprintsync.domain.user_profile.use_case.UserParamsError
import com.developers.sprintsync.presentation.user_parameters.model.UIError
import com.developers.sprintsync.presentation.user_parameters.model.UserParametersDraft
import com.developers.sprintsync.presentation.user_parameters.model.UserParametersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class UserParametersViewModel
@Inject
constructor(
    fetchUserParameters: FetchUserParameters,
    private val saveUserParameters: SaveUserParameters,
    private val log: AppLogger,
) : ViewModel() {

    private val _state = MutableStateFlow<UserParametersUiState>(UserParametersUiState.Loading)
    val state = _state.asStateFlow()

    private val _errors = MutableSharedFlow<Set<UIError>>()
    val errors = _errors.asSharedFlow()

    val sexLabel: (Sex) -> String = Sex::toUi
    val upDraft: UserParametersDraft? get() = (state.value as? UserParametersUiState.Success)?.draft


    init {
        fetchUserParameters().onEach { res ->
            _state.update { prev ->
                when (res) {
                    Resource.Loading -> UserParametersUiState.Loading
                    Resource.Result.Empty -> UserParametersUiState.Empty
                    is Resource.Result.Error -> {
                        prev.also { emitError(setOf(UIError.UNSPECIFIED)) }
                    }

                    is Resource.Result.Success -> {
                        val up = res.data
                        UserParametersUiState.Success(draft = up.toDraft(), persisted = up)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onSexChanged(sex: Sex) {
        trySave { it.copy(sex = sex) }
    }

    fun onDateChanged(epochMillis: Long) {
        val bd = Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        trySave { it.copy(birthDate = bd) }
        log.d("onDateChanged")
    }

    fun onWeightChanged(cs: CharSequence?) {
        if (cs.isNullOrEmpty()) return
        val text = cs.toString()
        log.d("onWeightChanged: $text")
        val float = text.toFloatOrNullLocale()
        if (float == null) {
            emitError(UIError.INVALID_WEIGHT_INPUT)
        } else {
            trySave { it.copy(weightKg = float) }
        }
    }


    private fun trySave(mutator: (UserParameters) -> UserParameters) {
        log.d("trySave")
        val up = when (val s = state.value) {
            is UserParametersUiState.Success -> s.persisted
            else -> UserParameters.DEFAULT
        }

        viewModelScope.launch {
            up?.let {
                val next = mutator(up)
                when (val result = saveUserParameters(next)) {
                    is SaveResult.Invalid -> {
                        val err = result.errors.toUiErr()
                        emitError(err)
                    }

                    SaveResult.Ok -> {/* NO-OP */
                    }
                }
            }
        }
    }


    private fun emitError(error: UIError) {
        emitError(setOf(error))
    }

    private fun emitError(errors: Set<UIError>) {
        log.d("emitError")
        viewModelScope.launch {
            _errors.emit(errors)
        }
    }
}

private fun Sex.toUi(): String {
    return when (this) {
        Sex.MALE -> "Male"
        Sex.FEMALE -> "Female"
        Sex.OTHER -> "Other"
        Sex.UNSPECIFIED -> "Unspecified"
    }
}

private fun LocalDate.toUi(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    return this.format(formatter)
}

private fun Set<UserParamsError>.toUiErr(): Set<UIError> = map { err ->
    when (err) {
        UserParamsError.INVALID_BIRTHDATE -> UIError.INVALID_DATE_INPUT
        UserParamsError.INVALID_WEIGHT -> UIError.INVALID_WEIGHT_INPUT
    }
}.toSet()

private fun UserParameters.toDraft(): UserParametersDraft {
    val sex = this.sex
    val bdEpochMilli = this.birthDate
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val bdText = this.birthDate.toUi()
    val weightText = this.weightKg.toString()
    return UserParametersDraft(
        sex, bdEpochMilli, bdText, weightText
    )
}