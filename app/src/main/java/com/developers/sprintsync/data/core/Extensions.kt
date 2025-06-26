package com.developers.sprintsync.data.core

import com.developers.sprintsync.domain.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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