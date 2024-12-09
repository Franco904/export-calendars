package com.example.daterangeexporter.calendarExport.localModels

import androidx.compose.runtime.Stable

@Stable
data class CalendarMonthYear(
    val id: Int,
    val month: Int,
    val year: Int,
)
