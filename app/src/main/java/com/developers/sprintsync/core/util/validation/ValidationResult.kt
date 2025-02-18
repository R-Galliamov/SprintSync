package com.developers.sprintsync.core.util.validation

sealed class ValidationResult {
    data object Valid : ValidationResult()

    data class Invalid(
        val exception: ValidationException,
    ) : ValidationResult()
}
