package com.fstengineering.exportcalendars.calendarExport.utils

import com.fstengineering.exportcalendars.calendarExport.models.CalendarMonthYear
import com.fstengineering.exportcalendars.calendarExport.models.CalendarSelectedDate
import com.fstengineering.exportcalendars.calendarExport.models.RangeSelectionLabel
import com.fstengineering.exportcalendars.calendarExport.utils.interfaces.CalendarExportUtils
import com.fstengineering.exportcalendars.calendarExport.utils.interfaces.ImmutableSelectedDates
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar

class CalendarExportUtilsImpl(
    private val startDateCalendar: Calendar,
    private val endDateCalendar: Calendar,
) : CalendarExportUtils {
    override fun getNewSelectedDates(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
        currentRangeCount: Int,
        currentSelectedDates: ImmutableSelectedDates,
    ): ImmutableSelectedDates {
        return getRangeDatesGroupedByMonthAndYear(
            startDateTimeMillis = startDateTimeMillis,
            endDateTimeMillis = endDateTimeMillis,
            currentRangeCount = currentRangeCount,
        )
            .mergeToExistingDates(currentSelectedDates = currentSelectedDates)
            .removeDuplicatedDates()
    }

    /**
     * Creates a structured range selection dates map with the dates selected for the new range.
     *
     * Dates are first grouped by its month/year combination, then each group values are mapped to
     * flag whether it is the range start or range end date, as well as which date selection range
     * it belongs.
     *
     * @param startDateTimeMillis The timestamp of the start date of the new range selection.
     * @param endDateTimeMillis The timestamp of the end date of the new range selection.
     */
    private fun getRangeDatesGroupedByMonthAndYear(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
        currentRangeCount: Int,
    ): ImmutableSelectedDates {
        val startDateUpdatedCalendar =
            startDateCalendar.apply { timeInMillis = startDateTimeMillis }
        val endDateUpdatedCalendar =
            endDateCalendar.apply { timeInMillis = endDateTimeMillis }

        val startDayOfMonth = startDateUpdatedCalendar.get(Calendar.DAY_OF_MONTH)
        val startMonth = startDateUpdatedCalendar.get(Calendar.MONTH) + 1
        val startYear = startDateUpdatedCalendar.get(Calendar.YEAR)

        val endDayOfMonth = endDateUpdatedCalendar.get(Calendar.DAY_OF_MONTH)
        val endMonth = endDateUpdatedCalendar.get(Calendar.MONTH) + 1
        val endYear = endDateUpdatedCalendar.get(Calendar.YEAR)

        var startDate = YearMonth.of(startYear, startMonth).atDay(startDayOfMonth)
        val endDate = YearMonth.of(endYear, endMonth).atDay(endDayOfMonth)

        val allDates = mutableListOf<LocalDate>()
        while (startDate <= endDate) {
            allDates.add(startDate)
            startDate = startDate.plusDays(1)
        }

        return allDates.groupBy { date ->
            CalendarMonthYear(
                id = date.month.value + date.year,
                month = date.month.value,
                year = date.year,
            )
        }
            .mapValues { (_, groupedDates) ->
                groupedDates
                    .toMutableList()
                    .map { date ->
                        val isRangeStart =
                            date.month.value == startMonth && date.year == startYear && date.dayOfMonth == startDayOfMonth

                        val isRangeEnd = allDates.size > 1 &&
                                date.month.value == endMonth && date.year == endYear && date.dayOfMonth == endDayOfMonth

                        val firstDateHalfLabel = if (isRangeStart) {
                            RangeSelectionLabel.None
                        } else RangeSelectionLabel.fromCount(currentRangeCount)

                        val secondDateHalfLabel = if (isRangeEnd) {
                            RangeSelectionLabel.None
                        } else RangeSelectionLabel.fromCount(currentRangeCount)

                        CalendarSelectedDate(
                            dayOfMonth = date.dayOfMonth.toString(),
                            isRangeStart = isRangeStart,
                            isRangeEnd = isRangeEnd,
                            rangeSelectionLabel = firstDateHalfLabel to secondDateHalfLabel,
                        )
                    }
                    .toImmutableList()
            }
            .toImmutableMap()
    }
}

/**
 * Merge dates values to already existing dates, looking for repeated calendar month/year keys,
 * both containing values for that month/year combination.
 *
 * Duplicated calendar month/year keys may occur when there is already a range selection,
 * and the user adds a new date range, starting from the last month/year of the current date
 * range.
 *
 * @param currentSelectedDates The current range selection dates map for merge, that will be
 * used to obtain the already existing date values for a month/year.
 */
private fun ImmutableSelectedDates.mergeToExistingDates(
    currentSelectedDates: ImmutableSelectedDates,
): ImmutableSelectedDates {
    val newSelectedDates = currentSelectedDates.toMutableMap()

    forEach { (monthYear, dates) ->
        val existingValuesForKey =
            currentSelectedDates[monthYear] ?: emptyList()
        val mergedDates =
            (existingValuesForKey + dates).toPersistentList()

        newSelectedDates[monthYear] = mergedDates
    }

    return newSelectedDates.toImmutableMap()
}

/**
 * Removes eventual duplicated dates, looking for repeated days of month in each calendar
 * month and year map values.
 *
 * Duplicated dates may occur when there is already a range selection, and the user adds a
 * new date range, starting from the last date of the current date range.
 */
private fun ImmutableSelectedDates.removeDuplicatedDates(): ImmutableSelectedDates {
    return mapValues { (_, monthDates) ->
        val daysOfMonth = monthDates.map { it.dayOfMonth }
        val hasDuplicatedDates = daysOfMonth.size != daysOfMonth.toSet().size

        if (!hasDuplicatedDates) return@mapValues monthDates

        val datesGroupedByDayOfMonth = monthDates.groupBy { it.dayOfMonth }

        val datesWithoutDuplications = datesGroupedByDayOfMonth.mapValues { (_, dates) ->
            dates.reduce { previous, current ->
                val newRangeSelectionLabel =
                    previous.rangeSelectionLabel.first to current.rangeSelectionLabel.second

                previous.copy(
                    isRangeStart = current.isRangeStart,
                    rangeSelectionLabel = newRangeSelectionLabel,
                )
            }
        }

        datesWithoutDuplications.values.toPersistentList()
    }.toImmutableMap()
}
