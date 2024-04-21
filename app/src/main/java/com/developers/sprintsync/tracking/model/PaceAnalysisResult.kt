package com.developers.sprintsync.tracking.model

sealed class PaceAnalysisResult {
    object PaceNormal : PaceAnalysisResult()

    object PaceSlowedDown : PaceAnalysisResult()

    object InsufficientData : PaceAnalysisResult()

    object InvalidData : PaceAnalysisResult()
}
