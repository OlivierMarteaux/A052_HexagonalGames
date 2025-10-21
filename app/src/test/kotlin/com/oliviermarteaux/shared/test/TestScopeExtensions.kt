package com.oliviermarteaux.shared.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
fun TestScope.assertFlagSwitching(
    stateProvider: () -> Boolean,
) {
    advanceTimeBy(50)
    assertTrue(stateProvider())
    advanceUntilIdle()
    assertFalse(stateProvider())
}