package com.developers.sprintsync.core.util.log

import android.util.Log.ERROR
import com.developers.sprintsync.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimberConfigurator @Inject constructor(
    private val debugTree: Timber.Tree,
    private val releaseTree: Timber.Tree,
) {

    fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(debugTree)
        } else {
            Timber.plant(releaseTree)
        }
    }
}

class DefaultDebugTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val stack = Throwable().stackTrace
        val index = if (stack.size > CALLER_INDEX_OFFSET) CALLER_INDEX_OFFSET else stack.lastIndex
        val element = stack[index]
        val realTag = formatTag(element)
        super.log(priority, realTag, message, t)
    }

    private fun formatTag(element: StackTraceElement) =
        "$IDENTIFIER${element.className.substringAfterLast('.')}.${element.methodName}():${element.lineNumber}"

    companion object {
        private const val CALLER_INDEX_OFFSET = 6
        private const val IDENTIFIER = "AppLogs: "
    }
}

class CrashlyticsReportingTree : Timber.Tree() {

    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log(message)

        if (priority == ERROR) {
            if (t == null) crashlytics.recordException(Throwable(message))
            else crashlytics.recordException(t)
        }
    }
}