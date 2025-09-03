package com.developers.sprintsync.domain.core

sealed interface AppResult<out T, out E> {
    data class Success<out T>(val value: T) : AppResult<T, Nothing>

    sealed interface Failure<out E> : AppResult<Nothing, E> {
        data class Validation<out E>(val errors: Set<E>) : Failure<E>
        data class Unexpected(val cause: Exception) : Failure<Nothing>
    }
}