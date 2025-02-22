package com.fstengineering.daterangeexporter.calendarExport.utils.interfaces

import com.fstengineering.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.fstengineering.daterangeexporter.calendarExport.models.CalendarSelectedDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

typealias ImmutableSelectedDates = ImmutableMap<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>

interface CalendarExportUtils {
    fun getNewSelectedDates(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
        currentRangeCount: Int,
        currentSelectedDates: ImmutableSelectedDates,
    ): ImmutableSelectedDates
}
