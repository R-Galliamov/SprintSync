package com.developers.sprintsync.core.util.log

interface AppLogger {
    fun v(message: String, tag: String? = null)
    fun d(message: String, tag: String? = null)
    fun i(message: String, tag: String? = null)
    fun w(message: String, tag: String? = null)
    fun e(message: String, tag: String? = null)
    fun wtf(message: String, tag: String? = null)
    fun e(throwable: Throwable, message: String? = null, tag: String? = null)
}