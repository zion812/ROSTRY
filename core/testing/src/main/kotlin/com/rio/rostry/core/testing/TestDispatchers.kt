package com.rio.rostry.core.testing

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Test dispatchers for coroutine testing.
 * Provides a test dispatcher that can be used in unit tests.
 */
class TestDispatchers(
    val main: CoroutineDispatcher = UnconfinedTestDispatcher(),
    val io: CoroutineDispatcher = UnconfinedTestDispatcher(),
    val default: CoroutineDispatcher = UnconfinedTestDispatcher()
)

/**
 * Sets up test dispatchers for coroutine testing.
 * Call this in @Before or setUp() method.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun setupTestDispatchers(testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) {
    Dispatchers.setMain(testDispatcher)
}

/**
 * Tears down test dispatchers after testing.
 * Call this in @After or tearDown() method.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun tearDownTestDispatchers() {
    Dispatchers.resetMain()
}
