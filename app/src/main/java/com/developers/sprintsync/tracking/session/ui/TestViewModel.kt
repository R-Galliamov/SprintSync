package com.developers.sprintsync.tracking.session.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TestViewModel
    @Inject
    constructor() : ViewModel() {
        val flow =
            flow {
                for (i in 1..10) {
                    delay(1000)
                    emit(i)
                }
            }.asLiveData()
    }
