package com.developers.sprintsync.core.util.extension

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

/**
 * Combines this [Flow] with the latest value from another [Flow], emitting the result of the [transform] function.
 *
 * This operator collects values from this [Flow] and pairs each value with the most recent value from [other].
 * The [transform] function is then applied to each pair to produce the emitted result.
 * If no value has been emitted by [other] yet, no values are emitted by this [Flow] until [other] emits a value.
 *
 * @param A The type of values emitted by this [Flow].
 * @param B The type of values emitted by the [other] [Flow].
 * @param R The type of values emitted by the resulting [Flow].
 * @param other The [Flow] whose latest value will be used in the [transform] function.
 * @param transform A suspend function that takes a value from this [Flow] and the latest value from [other],
 *                  and returns the transformed result.
 * @return A [Flow] that emits the results of applying [transform] to each value from this [Flow] paired with
 *         the latest value from [other].
 * @throws CancellationException If the collection of [other] is cancelled, the outer scope is also cancelled.
 */
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

/**
 * Combines this [Flow] with the latest value from another [Flow], applying the [transform] function in a concatenated manner.
 *
 * This operator first pairs each value from this [Flow] with the latest value from [other] using [withLatestFrom].
 * Then, it applies the [transform] function to each pair, emitting the result as a new [Flow] using [flatMapConcat].
 * The [flatMapConcat] operator ensures that the [transform] function is applied sequentially for each pair,
 * waiting for the previous transformation to complete before starting the next one.
 *
 * @param T The type of values emitted by this [Flow].
 * @param U The type of values emitted by the [other] [Flow].
 * @param R The type of values emitted by the resulting [Flow].
 * @param other The [Flow] whose latest value will be used in the [transform] function.
 * @param transform A suspend function that takes a value from this [Flow] and the latest value from [other],
 *                  and returns the transformed result.
 * @return A [Flow] that emits the results of applying [transform] to each value from this [Flow] paired with
 *         the latest value from [other], processed sequentially.
 * @throws CancellationException If the collection of [other] is cancelled, the outer scope is also cancelled.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T, U : Any, R> Flow<T>.withLatestConcat(
    other: Flow<U>,
    transform: suspend (T, U) -> R,
): Flow<R> =
    this
        .withLatestFrom(other) { a, b -> a to b }
        .flatMapConcat { (a, b) -> flow { emit(transform(a, b)) } }

/**
 * Combines two [StateFlow] instances, collects the latest combined values in the given [scope], and applies an [action].
 *
 * This function combines this [StateFlow] with [other] using the [combine] operator, applying the [transform] function
 * to each pair of values. The resulting [Flow] is collected using [collectLatest], which ensures that only the latest
 * combined value is processed by the [action]. The collection happens in the provided [scope].
 *
 * @param T1 The type of values emitted by this [StateFlow].
 * @param T2 The type of values emitted by the [other] [StateFlow].
 * @param R The type of values produced by the [transform] function.
 * @param other The [StateFlow] to combine with this [StateFlow].
 * @param scope The [CoroutineScope] in which the collection will be performed.
 * @param transform A suspend function that takes a value from this [StateFlow] and a value from [other],
 *                  and returns the transformed result.
 * @param action A suspend function that is called with the result of [transform] for each combined value.
 */
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
