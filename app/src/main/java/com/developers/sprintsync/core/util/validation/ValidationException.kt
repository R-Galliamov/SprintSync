package com.developers.sprintsync.core.util.validation

class ValidationException(
    val data: Any?,
    val errors: List<ValidationError>,
) : Exception(buildErrorMessage(data, errors)) {

    companion object {
        private fun buildErrorMessage(data: Any?, errors: List<ValidationError>): String {
            val errorMessages = errors.joinToString(separator = "\n") { "- ${it.message}" }
            val dataInfo = data?.toString() ?: "No data provided"
            return "Data validation failed:\n$errorMessages\n\nData:\n$dataInfo"
        }
    }
}
