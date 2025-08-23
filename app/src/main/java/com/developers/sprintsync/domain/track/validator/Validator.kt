package com.developers.sprintsync.domain.track.validator


/**
 * An interface for validating data of a specific type.
 *
 * This interface defines a contract for classes that can perform validation
 * on objects of type `T`. Implementations of this interface should provide
 * the logic to check if the given data meets certain criteria.
 *
 * @param T The type of data to be validated.
 */
interface Validator<T> {
    fun validate(data: T): Result<T>
}