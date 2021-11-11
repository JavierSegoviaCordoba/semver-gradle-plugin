package com.javiersc.semver.gradle.plugin.internal

import java.time.Instant
import java.util.Date

internal fun timestamp(mockDate: Date?): String =
    if (mockDate != null) "${Instant.ofEpochSecond(mockDate.time)}"
    else "${Instant.now()}".replace(":", "-").replace(".", "-")
