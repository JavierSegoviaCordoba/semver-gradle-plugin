package com.javiersc.gradle.version

import kotlin.time.Duration
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

fun runTestNoTimeout(block: suspend TestScope.() -> Unit): TestResult =
    runTest(timeout = Duration.INFINITE, testBody = block)
