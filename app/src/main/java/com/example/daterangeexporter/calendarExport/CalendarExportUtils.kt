package com.example.daterangeexporter.calendarExport

import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.localModels.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.localModels.RangeSelectionLabel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.TimeZone

typealias MutableSelectedDates = Map<CalendarMonthYear, MutableList<CalendarSelectedDate>>
typealias ImmutableSelectedDates = Map<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>

object CalendarExportUtils {
    fun getSelectedDates(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
        currentRangeCount: Int,
        currentSelectedDates: ImmutableSelectedDates,
    ): ImmutableSelectedDates {
        return getRangeDatesGroupedByMonthAndYear(
            startDateTimeMillis = startDateTimeMillis,
            endDateTimeMillis = endDateTimeMillis,
        )
            .populateRangeSelectionLabels(currentRangeCount = currentRangeCount)
            .mergeToExistingDates(currentSelectedDates = currentSelectedDates)
            .removeDuplicatedDates()
    }

    private fun getRangeDatesGroupedByMonthAndYear(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
    ): Map<CalendarMonthYear, MutableList<CalendarSelectedDate>> {
        val startDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            .apply { timeInMillis = startDateTimeMillis }

        val endDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            .apply { timeInMillis = endDateTimeMillis }

        val startDayOfMonth = startDateCalendar.get(Calendar.DAY_OF_MONTH)
        val startMonth = startDateCalendar.get(Calendar.MONTH) + 1
        val startYear = startDateCalendar.get(Calendar.YEAR)

        val endDayOfMonth = endDateCalendar.get(Calendar.DAY_OF_MONTH)
        val endMonth = endDateCalendar.get(Calendar.MONTH) + 1
        val endYear = endDateCalendar.get(Calendar.YEAR)

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
    }

    private fun ImmutableSelectedDates.mergeToExistingDates(
        currentSelectedDates: ImmutableSelectedDates,
    ): ImmutableSelectedDates {
        val newSelectedDates = currentSelectedDates.toMutableMap()

        this.forEach { (monthYear, dates) ->
            val existingValuesForKey =
                currentSelectedDates[monthYear] ?: emptyList()
            val mergedDates =
                (existingValuesForKey + dates).toPersistentList()

            newSelectedDates[monthYear] = mergedDates
        }

        return newSelectedDates
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
        }
    }
}
