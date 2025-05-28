package com.developers.sprintsync.core.util.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Implementation of [AppLogger] using Timber for logging and Firebase Crashlytics for error reporting.
 */
class TimberAppLogger : AppLogger {

    private val crashlytics: FirebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }

    override fun v(message: String, tag: String?) {
        if (tag != null) {
            Timber.tag(tag).v(message)
        } else {
            Timber.v(message)
        }
    }

    override fun d(message: String, tag: String?) {
        if (tag != null) {
            Timber.tag(tag).d(message)
        } else {
            Timber.d(message)
        }
    }

    override fun i(message: String, tag: String?) {
        if (tag != null) {
            Timber.tag(tag).i(message)
        } else {
            Timber.i(message)
        }
    }

    override fun w(message: String, tag: String?) {
        if (tag != null) {
            Timber.tag(tag).w(message)
        } else {
            Timber.w(message)
        }
    }

    // Logs error message and optional throwable to Timber and Crashlytics
    override fun e(message: String, throwable: Throwable?, tag: String?) {
        try {
            if (tag != null) {
                Timber.tag(tag).e(throwable, message)
                crashlytics.log("$tag: $message")
            } else {
                Timber.e(throwable, message)
                crashlytics.log(message)
            }
            throwable?.let { crashlytics.recordException(it) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to log error: $message")
        }
    }

    override fun wtf(message: String, tag: String?) {
        if (tag != null) {
            Timber.tag(tag).wtf(message)
        } else {
            Timber.wtf(message)
        }
    }

}