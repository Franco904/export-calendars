package com.example.daterangeexporter.calendarExport.localModels

data class CalendarSelectedDate(
    val dayOfMonth: String,
    val isRangeStart: Boolean = false,
    val isRangeEnd: Boolean = false,
    val rangeSelectionCount: RangeSelectionCount = RangeSelectionCount.NONE,
)
