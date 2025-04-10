package com.developers.sprintsync.domain.tracking_service.internal.activity_monitoring.pace_analyzer

sealed class PaceAnalysisResult {
    object PaceNormal : PaceAnalysisResult()

    object PaceSlowedDown : PaceAnalysisResult()

    object InsufficientData : PaceAnalysisResult()

    object InvalidData : PaceAnalysisResult()
}
