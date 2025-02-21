package com.fstengineering.daterangeexporter.core.application.monitoring.interfaces

interface AppLogger {
    fun logError(
        tag: String,
        message: String,
    )
}

