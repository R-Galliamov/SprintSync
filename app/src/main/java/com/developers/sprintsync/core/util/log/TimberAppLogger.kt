package com.developers.sprintsync.core.util.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

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

    override fun e(message: String, tag: String?) {
        if (tag != null) {
            Timber.tag(tag).e(message)
        } else {
            Timber.e(message)
        }
    }

    override fun wtf(message: String, tag: String?) {
        if (tag != null) {
            Timber.tag(tag).wtf(message)
        } else {
            Timber.wtf(message)
        }
    }

    override fun e(throwable: Throwable, message: String?, tag: String?) {
        if (tag != null) {
            Timber.tag(tag).e(throwable, message)
            message?.let { crashlytics.log("$tag: $it") }
        } else {
            Timber.e(throwable, message)
            message?.let { crashlytics.log(it) }
        }
        crashlytics.recordException(throwable)
    }

}