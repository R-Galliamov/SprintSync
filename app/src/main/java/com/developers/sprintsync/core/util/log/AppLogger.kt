package com.developers.sprintsync.core.util.log

/**
 * Interface for logging messages and exceptions with various log levels.
 */
interface AppLogger {
    /**
     * Logs a verbose message.
     * @param message The message to log.
     * @param tag Optional tag for the log.
     */
    fun v(message: String, tag: String? = null)

    /**
     * Logs a debug message.
     * @param message The message to log.
     * @param tag Optional tag for the log.
     */
    fun d(message: String, tag: String? = null)

    /**
     * Logs an info message.
     * @param message The message to log.
     * @param tag Optional tag for the log.
     */
    fun i(message: String, tag: String? = null)

    /**
     * Logs a warning message.
     * @param message The message to log.
     * @param tag Optional tag for the log.
     */
    fun w(message: String, tag: String? = null)

    /**
     * Logs an error message with an optional throwable.
     * @param message The message to log.
     * @param throwable Optional [Throwable] to log.
     * @param tag Optional tag for the log.
     */
    fun e(message: String, throwable: Throwable? = null, tag: String? = null)

    /**
     * Logs an assertion (WTF) message.
     * @param message The message to log.
     * @param tag Optional tag for the log.
     */
    fun wtf(message: String, tag: String? = null)
}