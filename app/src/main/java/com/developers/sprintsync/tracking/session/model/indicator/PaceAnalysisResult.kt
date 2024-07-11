package com.developers.sprintsync.tracking.session.model.indicator

sealed class PaceAnalysisResult {
    object PaceNormal : PaceAnalysisResult()

    object PaceSlowedDown : PaceAnalysisResult()

    object InsufficientData : PaceAnalysisResult()

    object InvalidData : PaceAnalysisResult()
}
