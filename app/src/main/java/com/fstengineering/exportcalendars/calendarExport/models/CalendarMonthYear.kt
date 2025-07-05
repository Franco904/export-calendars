package com.fstengineering.exportcalendars.calendarExport.models

import java.util.Calendar

data class CalendarMonthYear(
    val id: Int,
    val month: Int,
    val year: Int,
) {
    companion object {
        fun fromCalendar(calendar: Calendar): CalendarMonthYear {
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentYear = calendar.get(Calendar.YEAR)

            return CalendarMonthYear(
                id = currentMonth + currentYear,
                month = currentMonth,
                year = currentYear,
            )
        }
    }
}
