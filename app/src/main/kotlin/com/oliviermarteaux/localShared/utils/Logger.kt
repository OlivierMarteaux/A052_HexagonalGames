package com.oliviermarteaux.localShared.utils

import android.util.Log

/**
 * Simple logging interface for debug messages.
 */
interface Logger {
    /**
     * Logs a debug message.
     *
     * @param message The message to log.
     */
    fun d(message: String)
    fun v(message: String)
    fun i(message: String)
    fun w(message: String)
    fun e(message: String, e: Throwable? = null)
}

/**
 * [Logger] implementation that logs debug messages using Android's Log.d with a predefined tag "OM_TAG".
 */
object AndroidLogger : Logger {
    override fun d(message: String) {
        Log.d("OM_TAG", message)
    }
    override fun v(message: String) {
        Log.v("OM_TAG", message)
    }
    override fun i(message: String) {
        Log.i("OM_TAG", message)
    }
    override fun w(message: String) {
        Log.w("OM_TAG", message)
    }
    override fun e(message: String, e: Throwable?) {
        Log.e("OM_TAG", message, e)
    }
}

/**
 * [Logger] implementation that performs no operation.
 *
 * Useful as a default or stub logger to disable logging.
 */
object NoOpLogger : Logger {
    override fun d(message: String) = Unit
    override fun v(message: String) = Unit
    override fun i(message: String) = Unit
    override fun w(message: String) = Unit
    override fun e(message: String, e: Throwable?) = Unit
}