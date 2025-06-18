package com.developers.sprintsync.presentation.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.statistics.WorkoutsStatsCreator
import com.developers.sprintsync.domain.track.use_case.storage.GetTracksFlowUseCase
import com.developers.sprintsync.presentation.workouts_stats.WorkoutsStatsUiModel
import com.developers.sprintsync.presentation.workouts_stats.WorkoutsStatsUiModelFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * State for the home screen, containing workout statistics.
 */
data class HomeScreenState(
    val recordBoardUiModel: RecordBoardUiModel,
    val workoutsGeneralStats: WorkoutsStatsUiModel,
)

/**
 * ViewModel for the home screen, managing workout statistics.
 */
@HiltViewModel
class HomeViewModel
@Inject
constructor(
    getTracksFlowUseCase: GetTracksFlowUseCase,
    workoutsStatsCreator: WorkoutsStatsCreator,
    recordBoardUiModelCreator: RecordBoardCreator,
    private val workoutsStatsUiModelFormatter: WorkoutsStatsUiModelFormatter,
    private val log: AppLogger,
) : ViewModel() {

    init {
        log.i("HomeViewModel initialized")
    }

    val screenState: StateFlow<HomeScreenState> = getTracksFlowUseCase.tracks.map { tracks ->
        try {
            val stats = workoutsStatsCreator.create(tracks)
            val uiStats = workoutsStatsUiModelFormatter.format(stats)
            val recordBoardUiModel = recordBoardUiModelCreator.create(stats)
            HomeScreenState(recordBoardUiModel, uiStats)
        } catch (e: Exception) {
            log.e("Error mapping tracks to screen state: ${e.message}", e)
            HomeScreenState(
                recordBoardUiModel = RecordBoardUiModel.EMPTY,
                workoutsGeneralStats = WorkoutsStatsUiModel.EMPTY,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenState(
            recordBoardUiModel = RecordBoardUiModel.EMPTY,
            workoutsGeneralStats = WorkoutsStatsUiModel.EMPTY,
        )
    )

    override fun onCleared() {
        log.i("HomeViewModel cleared")
        super.onCleared()
    }
}
