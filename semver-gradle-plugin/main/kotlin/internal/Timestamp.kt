package com.javiersc.semver.gradle.plugin.internal

import java.sql.Timestamp
import java.util.Date

internal fun timestamp(mockDate: Date?): String =
    Timestamp(mockDate?.time ?: System.currentTimeMillis())
        .toString()
        .substringBeforeLast(".")
        .map { char ->
            when {
                char.isDigit() -> char
                char == '-' -> "-"
                char == ':' -> "-"
                char.isWhitespace() -> "--"
                char.isDigit().not() -> ""
                else -> ""
            }
        }
        .joinToString("")
