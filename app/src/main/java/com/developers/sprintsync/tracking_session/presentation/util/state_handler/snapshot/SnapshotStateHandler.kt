package com.developers.sprintsync.tracking_session.presentation.util.state_handler.snapshot

import android.graphics.Bitmap
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@ViewModelScoped
class SnapshotStateHandler @Inject constructor() {
    private val _snapshot = MutableStateFlow<Bitmap?>(null)
    val snapshot get() = _snapshot.asStateFlow().filterNotNull()

    fun emitSnapshot(bitmap: Bitmap) {
        _snapshot.update { bitmap }
    }
}
