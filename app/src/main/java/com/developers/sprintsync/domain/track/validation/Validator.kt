package com.developers.sprintsync.domain.track.validation

interface Validator<T> {
    fun validateOrThrow(data: T): T
}
