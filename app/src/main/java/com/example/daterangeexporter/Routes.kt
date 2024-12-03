package com.example.daterangeexporter

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object Calendars

    @Serializable
    data class CalendarExport(val timestamp: Long)
}
