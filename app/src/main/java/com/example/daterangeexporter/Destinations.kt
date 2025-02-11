package com.example.daterangeexporter

import kotlinx.serialization.Serializable

sealed interface Destinations {
    @Serializable
    data object Calendars

    @Serializable
    data object CalendarExport
}
