package com.developers.sprintsync.core.util.log

import javax.inject.Inject

class NoopLogger @Inject constructor() : AppLogger {
    override fun v(message: String, tag: String?) {}

    override fun d(message: String, tag: String?) {}

    override fun i(message: String, tag: String?) {}

    override fun w(message: String, tag: String?) {}

    override fun e(message: String, throwable: Throwable?, tag: String?) {}

    override fun wtf(message: String, tag: String?) {}
}