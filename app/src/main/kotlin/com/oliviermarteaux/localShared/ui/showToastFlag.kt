package com.oliviermarteaux.localShared.ui

import kotlinx.coroutines.delay

suspend fun showToastFlag(
    duration: Long,
    setFlag: (Boolean) -> Unit
) {
    setFlag(true)
    delay(duration)
    setFlag(false)
}