package com.developers.sprintsync.domain.core

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
