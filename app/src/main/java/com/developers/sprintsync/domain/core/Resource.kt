package com.developers.sprintsync.domain.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart

sealed class Resource<out T> {
    sealed class Result<out T> : Resource<T>() {
        data object Empty : Result<Nothing>()
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val throwable: Throwable) : Result<Nothing>()
    }

    data object Loading : Resource<Nothing>()
}

typealias ResourceList<T> = Resource<List<T>>

private fun Any.isEmptyCollection(): Boolean = this is Collection<*> && this.isEmpty()
private fun Any.isNotEmptyCollection(): Boolean = !this.isEmptyCollection()
private fun <T> T?.isEmptyCollectionOrNull(): Boolean = this == null || this.isEmptyCollection()
fun <T> fromLocalOrRemote(
    forceRefresh: Boolean,
    readLocalSource: suspend () -> List<T>,
    writeLocalSource: suspend (List<T>) -> Any,
    readRemoteSource: suspend () -> List<T>
): Flow<List<T>> = flow {
    val localData = readLocalSource()
    val needToRefresh = forceRefresh || localData.isEmptyCollectionOrNull()
    val resultData = if (needToRefresh) {
        val remoteData = readRemoteSource()
        if (remoteData.isNotEmptyCollection()) writeLocalSource(remoteData)
        remoteData
    } else localData
    emit(resultData)
}

suspend fun <T> fromLocalOrRemote(
    readLocalSource: suspend () -> T?,
    writeLocalSource: suspend (T) -> Unit,
    readRemoteSource: suspend () -> T?
): T? {
    return readLocalSource() ?: readRemoteSource()?.also { writeLocalSource(it) }
}

suspend fun <T> runResult(block: suspend () -> T?): Resource.Result<T> = try {
    val data = block()
    when {
        data == null -> Resource.Result.Empty
        data.isEmptyCollection() -> Resource.Result.Empty
        else -> Resource.Result.Success(data)
    }
} catch (e: Exception) {
    Resource.Result.Error(e)
}

fun <T> Flow<T?>.toResource(): Flow<Resource<T>> {
    return this
        .map<T?, Resource<T>> { res ->
            if (res == null || res.isEmptyCollection()) Resource.Result.Empty
            else Resource.Result.Success(res)
        }
        .onStart { emit(Resource.Loading) }
        .catch { emit(Resource.Result.Error(it)) }
}

fun <T> resourceFlow(block: suspend FlowCollector<T?>.() -> Unit): Flow<Resource<T>> = flow(block).toResource()
suspend fun <T, O> Resource<T>.mapResource(transform: suspend (T) -> O): Resource<O> =
    when (this) {
        is Resource.Result.Success -> Resource.Result.Success(transform(data))
        is Resource.Result.Empty -> Resource.Result.Empty
        is Resource.Result.Error -> Resource.Result.Error(throwable)
        Resource.Loading -> Resource.Loading
    }

suspend fun <T, O : Any> Resource<T>.mapResourceNotNull(transform: suspend (T) -> O?): Resource<O> =
    when (this) {
        is Resource.Result.Success -> transform(data)?.let { Resource.Result.Success(it) } ?: Resource.Result.Empty
        is Resource.Result.Empty -> Resource.Result.Empty
        is Resource.Result.Error -> Resource.Result.Error(throwable)
        Resource.Loading -> Resource.Loading
    }

suspend fun <T, O> Resource<T>.flatMapResource(transform: suspend (T) -> Resource<O>): Resource<O> =
    when (this) {
        is Resource.Result.Success -> transform(data)
        is Resource.Result.Empty -> Resource.Result.Empty
        is Resource.Result.Error -> Resource.Result.Error(throwable)
        Resource.Loading -> Resource.Loading
    }

fun <T, O : Any> Flow<Resource<T>>.mapResourceNotNull(transform: suspend (T) -> O?): Flow<Resource<O>> {
    return this.mapNotNull { res -> res.mapResourceNotNull { t -> transform(t) } }
}