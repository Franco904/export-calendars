package com.fstengineering.daterangeexporter.calendarExport.utils

import com.fstengineering.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.fstengineering.daterangeexporter.calendarExport.models.CalendarSelectedDate
import com.fstengineering.daterangeexporter.calendarExport.models.RangeSelectionLabel.First
import com.fstengineering.daterangeexporter.calendarExport.models.RangeSelectionLabel.None
import com.fstengineering.daterangeexporter.calendarExport.models.RangeSelectionLabel.Second
import com.fstengineering.daterangeexporter.calendarExport.models.RangeSelectionLabel.Third
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.TimeZone

class CalendarExportUtilsImplTest {
    private lateinit var sut: CalendarExportUtilsImpl

    @BeforeEach
    fun setUp() {
        sut = CalendarExportUtilsImpl(
            startDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")),
            endDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")),
        )
    }

    @Nested
    @DisplayName("getNewSelectedDates")
    inner class GetNewSelectedDatesTests {
        @Test
        fun `Should return correct range selection dates for a single-day range selection`() {
            val selectedDates = sut.getNewSelectedDates(
                startDateTimeMillis = 1704067200000, // 2024-01-01 00:00:00 UTC
                endDateTimeMillis = 1704067200000, // 2024-01-01 00:00:00 UTC
                currentRangeCount = 1,
                currentSelectedDates = persistentMapOf(),
            )

            selectedDates shouldBeEqualTo persistentMapOf(
                CalendarMonthYear(id = 2025, month = 1, year = 2024) to persistentListOf(
                    CalendarSelectedDate(
                        dayOfMonth = "1",
                        isRangeStart = true,
                        isRangeEnd = false,
                        rangeSelectionLabel = None to First,
                    ),
                )
            )
        }

        @Test
        fun `Should return correct range selection dates for a multi-day, single-month and single-year range selection`() {
            val selectedDates = sut.getNewSelectedDates(
                startDateTimeMillis = 1704067200000, // 2024-01-01 00:00:00 UTC
                endDateTimeMillis = 1704499200000, // 2024-01-06 00:00:00 UTC
                currentRangeCount = 1,
                currentSelectedDates = persistentMapOf(),
            )

            val january2024 = CalendarMonthYear(id = 2025, month = 1, year = 2024)
            val january2024Dates = (1..6).toMutableList()

            selectedDates shouldBeEqualTo persistentMapOf(january2024 to january2024Dates.map { dayOfMonth ->
                val isRangeStart = dayOfMonth == january2024Dates.first()
                val isRangeEnd = dayOfMonth == january2024Dates.last()

                val firstDateHalfLabel = if (isRangeStart) None else First
                val secondDateHalfLabel = if (isRangeEnd) None else First

                CalendarSelectedDate(
                    dayOfMonth = dayOfMonth.toString(),
                    isRangeStart = isRangeStart,
                    isRangeEnd = isRangeEnd,
                    rangeSelectionLabel = firstDateHalfLabel to secondDateHalfLabel,
                )
            })
        }

        @Test
        fun `Should return correct range selection dates for a multi-month and single-year range selection`() {
            val selectedDates = sut.getNewSelectedDates(
                startDateTimeMillis = 1704067200000, // 2024-01-01 00:00:00 UTC
                endDateTimeMillis = 1709241600000, // 2024-02-29 00:00:00 UTC (Leap Year)
                currentRangeCount = 1,
                currentSelectedDates = persistentMapOf(),
            )

            val january2024 = CalendarMonthYear(id = 2025, month = 1, year = 2024)
            val january2024Dates = (1..31).toMutableList()

            val february2024 = CalendarMonthYear(id = 2026, month = 2, year = 2024)
            val february2024Dates = (1..29).toMutableList()

            selectedDates shouldBeEqualTo persistentMapOf(
                january2024 to january2024Dates.map { dayOfMonth ->
                    val isRangeStart = dayOfMonth == january2024Dates.first()
                    val firstDateHalfLabel = if (isRangeStart) None else First

                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = isRangeStart,
                        isRangeEnd = false,
                        rangeSelectionLabel = firstDateHalfLabel to First,
                    )
                },
                february2024 to february2024Dates.map { dayOfMonth ->
                    val isRangeEnd = dayOfMonth == february2024Dates.last()
                    val secondDateHalfLabel = if (isRangeEnd) None else First

                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = isRangeEnd,
                        rangeSelectionLabel = First to secondDateHalfLabel,
                    )
                },
            )
        }

        @Test
        fun `Should return correct range selection dates for a multi-year range selection`() {
            val selectedDates = sut.getNewSelectedDates(
                startDateTimeMillis = 1672531200000, // 2023-01-01 00:00:00 UTC
                endDateTimeMillis = 1704067200000, // 2024-01-01 00:00:00 UTC
                currentRangeCount = 1,
                currentSelectedDates = persistentMapOf(),
            )

            val january2023 = CalendarMonthYear(id = 2024, month = 1, year = 2023)
            val january2023Dates = (1..31).toMutableList()

            val february2023 = CalendarMonthYear(id = 2025, month = 2, year = 2023)
            val february2023Dates = (1..28).toMutableList()

            val march2023 = CalendarMonthYear(id = 2026, month = 3, year = 2023)
            val march2023Dates = (1..31).toMutableList()

            val april2023 = CalendarMonthYear(id = 2027, month = 4, year = 2023)
            val april2023Dates = (1..30).toMutableList()

            val may2023 = CalendarMonthYear(id = 2028, month = 5, year = 2023)
            val may2023Dates = (1..31).toMutableList()

            val june2023 = CalendarMonthYear(id = 2029, month = 6, year = 2023)
            val june2023Dates = (1..30).toMutableList()

            val july2023 = CalendarMonthYear(id = 2030, month = 7, year = 2023)
            val july2023Dates = (1..31).toMutableList()

            val august2023 = CalendarMonthYear(id = 2031, month = 8, year = 2023)
            val august2023Dates = (1..31).toMutableList()

            val september2023 = CalendarMonthYear(id = 2032, month = 9, year = 2023)
            val september2023Dates = (1..30).toMutableList()

            val october2023 = CalendarMonthYear(id = 2033, month = 10, year = 2023)
            val october2023Dates = (1..31).toMutableList()

            val november2023 = CalendarMonthYear(id = 2034, month = 11, year = 2023)
            val november2023Dates = (1..30).toMutableList()

            val december2023 = CalendarMonthYear(id = 2035, month = 12, year = 2023)
            val december2023Dates = (1..31).toMutableList()

            val january2024 = CalendarMonthYear(id = 2025, month = 1, year = 2024)
            val january2024Dates = mutableListOf(1)

            selectedDates shouldBeEqualTo persistentMapOf(
                january2023 to january2023Dates.map { dayOfMonth ->
                    val isRangeStart = dayOfMonth == january2023Dates.first()
                    val firstDateHalfLabel = if (isRangeStart) None else First

                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = isRangeStart,
                        isRangeEnd = false,
                        rangeSelectionLabel = firstDateHalfLabel to First,
                    )
                },
                february2023 to february2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                march2023 to march2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                april2023 to april2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                may2023 to may2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                june2023 to june2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                july2023 to july2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                august2023 to august2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                september2023 to september2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                october2023 to october2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                november2023 to november2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                december2023 to december2023Dates.map { dayOfMonth ->
                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = false,
                        rangeSelectionLabel = First to First,
                    )
                },
                january2024 to january2024Dates.map { dayOfMonth ->
                    val isRangeEnd = dayOfMonth == january2024Dates.last()
                    val secondDateHalfLabel = if (isRangeEnd) None else First

                    CalendarSelectedDate(
                        dayOfMonth = dayOfMonth.toString(),
                        isRangeStart = false,
                        isRangeEnd = isRangeEnd,
                        rangeSelectionLabel = First to secondDateHalfLabel,
                    )
                },
            )
        }

        @Test
        fun `Should return correct range selection dates for when there are 2+ range selections`() {
            val firstRangeDates = sut.getNewSelectedDates(
                startDateTimeMillis = 1704067200000, // 2024-01-01 00:00:00 UTC
                endDateTimeMillis = 1704499200000, // 2024-01-06 00:00:00 UTC
                currentRangeCount = 1,
                currentSelectedDates = persistentMapOf(),
            )

            val firstAndSecondRangesDates = sut.getNewSelectedDates(
                startDateTimeMillis = 1704499200000, // 2024-01-06 00:00:00 UTC
                endDateTimeMillis = 1705276800000, // 2024-01-15 00:00:00 UTC
                currentRangeCount = 2,
                currentSelectedDates = firstRangeDates,
            )

            val allRangesDates = sut.getNewSelectedDates(
                startDateTimeMillis = 1705276800000, // 2024-01-15 00:00:00 UTC
                endDateTimeMillis = 1705785600000, // 2024-01-20 00:00:00 UTC
                currentRangeCount = 3,
                currentSelectedDates = firstAndSecondRangesDates,
            )

            allRangesDates shouldBeEqualTo persistentMapOf(
                CalendarMonthYear(id = 2025, month = 1, year = 2024) to persistentListOf(
                    CalendarSelectedDate(dayOfMonth = "1", isRangeStart = true, isRangeEnd = false, rangeSelectionLabel = None to First),
                    CalendarSelectedDate(dayOfMonth = "2", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = First to First),
                    CalendarSelectedDate(dayOfMonth = "3", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = First to First),
                    CalendarSelectedDate(dayOfMonth = "4", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = First to First),
                    CalendarSelectedDate(dayOfMonth = "5", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = First to First),
                    CalendarSelectedDate(dayOfMonth = "6", isRangeStart = true, isRangeEnd = true, rangeSelectionLabel = First to Second),
                    CalendarSelectedDate(dayOfMonth = "7", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Second to Second),
                    CalendarSelectedDate(dayOfMonth = "8", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Second to Second),
                    CalendarSelectedDate(dayOfMonth = "9", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Second to Second),
                    CalendarSelectedDate(dayOfMonth = "10", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Second to Second),
                    CalendarSelectedDate(dayOfMonth = "11", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Second to Second),
                    CalendarSelectedDate(dayOfMonth = "12", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Second to Second),
                    CalendarSelectedDate(dayOfMonth = "13", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Second to Second),
                    CalendarSelectedDate(dayOfMonth = "14", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Second to Second),
                    CalendarSelectedDate(dayOfMonth = "15", isRangeStart = true, isRangeEnd = true, rangeSelectionLabel = Second to Third),
                    CalendarSelectedDate(dayOfMonth = "16", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Third to Third),
                    CalendarSelectedDate(dayOfMonth = "17", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Third to Third),
                    CalendarSelectedDate(dayOfMonth = "18", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Third to Third),
                    CalendarSelectedDate(dayOfMonth = "19", isRangeStart = false, isRangeEnd = false, rangeSelectionLabel = Third to Third),
                    CalendarSelectedDate(dayOfMonth = "20", isRangeStart = false, isRangeEnd = true, rangeSelectionLabel = Third to None),
                )
            )
        }

        @Test
        fun `Should return empty selection dates when the start date timestamp is greater than the end date timestamp`() {
            val selectedDates = sut.getNewSelectedDates(
                startDateTimeMillis = 1704499200000, // 2024-01-06 00:00:00 UTC
                endDateTimeMillis = 1704067200000, // 2024-01-01 00:00:00 UTC
                currentRangeCount = 1,
                currentSelectedDates = persistentMapOf(),
            )

            selectedDates.isEmpty() shouldBeEqualTo true
        }
    }
}
