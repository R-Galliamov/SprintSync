package com.developers.sprintsync.global.util.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.cancellation.CancellationException

fun <A, B : Any, R> Flow<A>.withLatestFrom(
    other: Flow<B>,
    transform: suspend (A, B) -> R,
): Flow<R> =
    flow {
        coroutineScope {
            val latestB = AtomicReference<B?>()
            val outerScope = this
            launch {
                try {
                    other.collect { latestB.set(it) }
                } catch (e: CancellationException) {
                    outerScope.cancel(e) // cancel outer scope on cancellation exception, too
                }
            }
            collect { a: A ->
                latestB.get()?.let { b -> emit(transform(a, b)) }
            }
        }
    }

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, U : Any, R> Flow<T>.withLatestConcat(
    other: Flow<U>,
    transform: suspend (T, U) -> R,
): Flow<R> =
    this
        .withLatestFrom(other) { a, b -> a to b }
        .flatMapConcat { (a, b) -> flow { emit(transform(a, b)) } }

@OptIn(ExperimentalCoroutinesApi::class)
fun <T1, T2, R> StateFlow<T1>.combineAndCollectLatest(
    other: StateFlow<T2>,
    scope: CoroutineScope,
    transform: suspend (T1, T2) -> R,
    action: suspend (R) -> Unit,
) {
    scope.launch {
        combine(this@combineAndCollectLatest, other, transform)
            .collectLatest(action)
    }
}
