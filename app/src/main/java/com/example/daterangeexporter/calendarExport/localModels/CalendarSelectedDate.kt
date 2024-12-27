package com.example.daterangeexporter.calendarExport.localModels

data class CalendarSelectedDate(
    val dayOfMonth: String,
    val isRangeStart: Boolean = false,
    val isRangeEnd: Boolean = false,
    val rangeSelectionLabel: Pair<RangeSelectionLabel, RangeSelectionLabel> =
        RangeSelectionLabel.None to RangeSelectionLabel.None,
)
