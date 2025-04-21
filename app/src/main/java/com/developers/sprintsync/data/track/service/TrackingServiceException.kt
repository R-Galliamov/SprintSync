package com.developers.sprintsync.data.track.service

sealed class TrackingServiceException(
    override val message: String,
) : Exception(message) {
    data class BindingFailed(
        override val message: String = "Failed to bind to tracking service",
    ) : TrackingServiceException(message)

    data class ServiceDisconnected(
        override val message: String = "Tracking service disconnected unexpectedly",
    ) : TrackingServiceException(message)

    data class LocationUpdatesFailed( // TODO check for location updates in service
        override val message: String = "Failed to retrieve location updates",
    ) : TrackingServiceException(message)

}
