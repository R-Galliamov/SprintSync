package com.developers.sprintsync.presentation.home_screen

import androidx.lifecycle.ViewModel
import com.developers.sprintsync.data.statistics.WorkoutsStatsCreator
import com.developers.sprintsync.domain.track.use_case.storage.GetTracksFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    getTracksFlowUseCase: GetTracksFlowUseCase,
    workoutsStatsCreator: WorkoutsStatsCreator,
    recordBoardUiModelCreator: RecordBoardCreator,
) : ViewModel() {
    val statistics: Flow<RecordBoardUiModel> =
        getTracksFlowUseCase.tracks.map { tracks ->
            val stats = workoutsStatsCreator.create(tracks)
            recordBoardUiModelCreator.create(stats)
        }
}
