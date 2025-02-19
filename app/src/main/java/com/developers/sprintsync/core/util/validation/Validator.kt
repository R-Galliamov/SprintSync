package com.developers.sprintsync.core.util.validation

interface Validator<T> {
    fun validateOrThrow(data: T): T
}
