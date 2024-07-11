package com.developers.sprintsync.global.viewModel

import androidx.lifecycle.ViewModel
import com.developers.sprintsync.tracking.viewModel.useCase.GetAllTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        getAllTracksUseCase: GetAllTracksUseCase,
    ) : ViewModel()
