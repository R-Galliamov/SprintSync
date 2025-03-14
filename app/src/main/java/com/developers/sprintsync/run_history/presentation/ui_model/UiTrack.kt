package com.developers.sprintsync.run_history.presentation.ui_model

data class UiTrackPreviewWrapper(
    val id: Int,
    val date: String,
    val distance: String,
    val duration: String,
    val calories: String,
    val previewPath: String?,
)
