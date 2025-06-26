package com.developers.sprintsync.domain.core

sealed class Resource<out T> {
    sealed class Result<out T> : Resource<T>() {
        data object Empty : Result<Nothing>()
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val throwable: Throwable) : Result<Nothing>()
    }

    data object Loading : Resource<Nothing>()
}

typealias ResourceList<T> = Resource<List<T>>