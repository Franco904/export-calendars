package com.example.daterangeexporter.calendarExport.utils.interfaces

import com.example.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.models.CalendarSelectedDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

typealias MutableSelectedDates = ImmutableMap<CalendarMonthYear, MutableList<CalendarSelectedDate>>
typealias ImmutableSelectedDates = ImmutableMap<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>

interface CalendarExportUtils {
    fun getSelectedDates(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
        currentRangeCount: Int,
        currentSelectedDates: ImmutableSelectedDates,
    ): ImmutableSelectedDates
}
