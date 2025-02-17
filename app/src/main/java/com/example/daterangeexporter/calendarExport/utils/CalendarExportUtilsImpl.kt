package com.example.daterangeexporter.calendarExport.utils

import com.example.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.models.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.models.RangeSelectionLabel
import com.example.daterangeexporter.calendarExport.utils.interfaces.CalendarExportUtils
import com.example.daterangeexporter.calendarExport.utils.interfaces.ImmutableSelectedDates
import com.example.daterangeexporter.calendarExport.utils.interfaces.MutableSelectedDates
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar

class CalendarExportUtilsImpl(
    private val startDateCalendar: Calendar,
    private val endDateCalendar: Calendar,
) : CalendarExportUtils {
    override fun getSelectedDates(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
        currentRangeCount: Int,
        currentSelectedDates: ImmutableSelectedDates,
    ): ImmutableSelectedDates {
        val selectedDates = getRangeDatesGroupedByMonthAndYear(
            startDateTimeMillis = startDateTimeMillis,
            endDateTimeMillis = endDateTimeMillis,
        )
            .populateRangeSelectionLabels(currentRangeCount = currentRangeCount)
            .mergeToExistingDates(currentSelectedDates = currentSelectedDates)
            .removeDuplicatedDates()

        return selectedDates
    }

    private fun getRangeDatesGroupedByMonthAndYear(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
    ): MutableSelectedDates {
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

        val dates = mutableListOf<LocalDate>()
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
                dates.map { date ->
                    val isRangeStart =
                        date.month.value == startMonth && date.year == startYear && date.dayOfMonth == startDayOfMonth

                    val isRangeEnd =
                        date.month.value == endMonth && date.year == endYear && date.dayOfMonth == endDayOfMonth

                    CalendarSelectedDate(
                        dayOfMonth = date.dayOfMonth.toString(),
                        isRangeStart = isRangeStart,
                        isRangeEnd = isRangeEnd,
                    )
                }.toMutableList()
            }
            .toImmutableMap()
    }

    private fun MutableSelectedDates.populateRangeSelectionLabels(
        currentRangeCount: Int,
    ): ImmutableSelectedDates {
        return mapValues { (_, dates) ->
            dates.map { date ->
                val firstDateHalfLabel = if (date.isRangeStart) {
                    RangeSelectionLabel.None
                } else RangeSelectionLabel.fromCount(currentRangeCount)

                val secondDateHalfLabel = if (date.isRangeEnd) {
                    RangeSelectionLabel.None
                } else RangeSelectionLabel.fromCount(currentRangeCount)

                date.copy(
                    rangeSelectionLabel = firstDateHalfLabel to secondDateHalfLabel
                )
            }.toPersistentList()
        }
            .toImmutableMap()
    }

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
}
