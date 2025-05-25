package com.fstengineering.exportcalendars.core.application.monitoring.interfaces

interface AppLogger {
    fun logEvent(name: String)

    fun logError(
        tag: String,
        message: String,
    )
}

