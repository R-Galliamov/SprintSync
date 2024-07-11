package com.developers.sprintsync.tracking.analytics.viewModel

import androidx.lifecycle.ViewModel
import com.developers.sprintsync.tracking.dataStorage.repository.useCase.GetAllTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        getAllTracksUseCase: GetAllTracksUseCase,
    ) : ViewModel()
