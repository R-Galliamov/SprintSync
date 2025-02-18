package com.developers.sprintsync.core.util.validation

interface Validator<T> {
    fun validate(data: T): ValidationResult
}
