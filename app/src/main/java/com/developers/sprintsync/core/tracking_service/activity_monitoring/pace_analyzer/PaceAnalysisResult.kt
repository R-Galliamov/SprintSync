package com.developers.sprintsync.core.tracking_service.activity_monitoring.pace_analyzer

sealed class PaceAnalysisResult {
    object PaceNormal : PaceAnalysisResult()

    object PaceSlowedDown : PaceAnalysisResult()

    object InsufficientData : PaceAnalysisResult()

    object InvalidData : PaceAnalysisResult()
}