package com.fstengineering.exportcalendars.calendarExport.utils.interfaces

import com.fstengineering.exportcalendars.calendarExport.models.CalendarMonthYear
import com.fstengineering.exportcalendars.calendarExport.models.CalendarSelectedDate
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
