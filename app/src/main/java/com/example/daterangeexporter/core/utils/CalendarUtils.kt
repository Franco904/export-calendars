package com.example.daterangeexporter.core.utils

import android.content.Context
import android.icu.util.Calendar
import com.example.daterangeexporter.R
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate
import java.time.YearMonth
import java.util.TimeZone

object CalendarUtils {
    val daysOfWeek = listOf(
        R.string.sunday_label,
        R.string.monday_label,
        R.string.tuesday_label,
        R.string.wednesday_label,
        R.string.thursday_label,
        R.string.friday_label,
        R.string.saturday_label,
    )

    private val months = mapOf(
        1 to R.string.january_label,
        2 to R.string.february_label,
        3 to R.string.march_label,
        4 to R.string.april_label,
        5 to R.string.may_label,
        6 to R.string.june_label,
        7 to R.string.july_label,
        8 to R.string.august_label,
        9 to R.string.september_label,
        10 to R.string.october_label,
        11 to R.string.november_label,
        12 to R.string.december_label,
    )

    fun Context.getMonthLabelByNumber(monthNumber: Int): String {
        return getString(months[monthNumber] ?: R.string.january_label)
    }

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
    ): Map<CalendarMonthYear, ImmutableList<String>> {
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
                dates.map { it.dayOfMonth.toString() }.toPersistentList()
            }
    }
}
