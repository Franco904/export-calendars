package com.example.daterangeexporter.core.utils

import android.icu.util.Calendar
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import java.time.LocalDate
import java.time.YearMonth
import java.util.TimeZone

object CalendarUtils {
    private val months = mapOf(
        1 to "JANEIRO",
        2 to "FEVEREIRO",
        3 to "MARÇO",
        4 to "ABRIL",
        5 to "MAIO",
        6 to "JUNHO",
        7 to "JULHO",
        8 to "AGOSTO",
        9 to "SETEMBRO",
        10 to "OUTUBRO",
        11 to "NOVEMBRO",
        12 to "DEZEMBRO",
    )

    fun getMonthLabelByNumber(monthNumber: Int) = months[monthNumber] ?: "JANEIRO"

    fun getNextYears(): List<Int> {
        val currentYear = getCurrentYear()
        val nextYears = mutableListOf<Int>()

        for (year in currentYear..2030) {
            nextYears.add(year)
        }

        return nextYears
    }

    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH)

    fun getMonthTimestamp(month: Int, year: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)

        return calendar.timeInMillis
    }

    fun getNumberOfDaysOfMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getFirstDayOfWeekOfMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)

        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun getDatesGroupedByMonthAndYear(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
    ): Map<CalendarMonthYear, List<String>> {
        val startDateCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            .apply { timeInMillis = startDateTimeMillis }

        val endDateCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            .apply { timeInMillis = endDateTimeMillis }

        val startDayOfMonth = startDateCalendar.get(java.util.Calendar.DAY_OF_MONTH)
        val startMonth = startDateCalendar.get(java.util.Calendar.MONTH) + 1
        val startYear = startDateCalendar.get(java.util.Calendar.YEAR)

        val endDayOfMonth = endDateCalendar.get(java.util.Calendar.DAY_OF_MONTH)
        val endMonth = endDateCalendar.get(java.util.Calendar.MONTH) + 1
        val endYear = endDateCalendar.get(java.util.Calendar.YEAR)

        val dates = mutableListOf<LocalDate>()
        var startDate = YearMonth.of(startYear, startMonth).atDay(startDayOfMonth)
        val endDate = YearMonth.of(endYear, endMonth).atDay(endDayOfMonth)

        while (startDate <= endDate) {
            dates.add(startDate)
            startDate = startDate.plusDays(1)
        }

        return dates
            .groupBy { date ->
                CalendarMonthYear(
                    id = date.month.value + date.year,
                    month = date.month.value,
                    year = date.year,
                )
            }
            .mapValues { (_, dates) ->
                dates.map { it.dayOfMonth.toString() }
            }
    }
}
