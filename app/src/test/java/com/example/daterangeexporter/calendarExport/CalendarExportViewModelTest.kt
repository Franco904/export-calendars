package com.example.daterangeexporter.calendarExport

import android.content.Context
import com.example.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.models.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.utils.interfaces.CalendarExportUtils
import com.example.daterangeexporter.core.application.contentProviders.interfaces.AppFileProviderHandler
import com.example.daterangeexporter.core.domain.repositories.CalendarsRepository
import com.example.daterangeexporter.testUtils.MainDispatcherExtension
import com.example.daterangeexporter.testUtils.faker
import com.example.daterangeexporter.testUtils.fakes.createCalendarMonthYearFake
import com.example.daterangeexporter.testUtils.fakes.createCalendarSelectedDate
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Calendar

@ExtendWith(MainDispatcherExtension::class)
class CalendarExportViewModelTest {
    private lateinit var sut: CalendarExportViewModel

    private lateinit var calendarMock: Calendar
    private lateinit var appContextMock: Context
    private lateinit var calendarsRepositoryMock: CalendarsRepository
    private lateinit var calendarExportUtilsMock: CalendarExportUtils
    private lateinit var appFileProviderHandlerMock: AppFileProviderHandler

    private val currentDayOfMonthFake = faker.random.nextInt()
    private val currentMonthFake = faker.random.nextInt()
    private val currentYearFake = faker.random.nextInt()

    @BeforeEach
    fun setUp() {
        mockkStatic(Calendar::class)

        calendarMock = mockk(relaxed = true) {
            every { get(Calendar.DAY_OF_MONTH) } returns currentDayOfMonthFake
            every { get(Calendar.MONTH) } returns currentMonthFake
            every { get(Calendar.YEAR) } returns currentYearFake
        }

        appContextMock = mockk(relaxUnitFun = true)
        calendarsRepositoryMock = mockk(relaxUnitFun = true)
        calendarExportUtilsMock = mockk(relaxUnitFun = true)
        appFileProviderHandlerMock = mockk(relaxUnitFun = true)

        sut = CalendarExportViewModel(
            calendar = calendarMock,
            appContext = appContextMock,
            calendarsRepository = calendarsRepositoryMock,
            calendarExportUtils = calendarExportUtilsMock,
            appFileProviderHandler = appFileProviderHandlerMock,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Calendar::class)
    }

    @Nested
    @DisplayName("currentDayOfMonth")
    inner class CurrentDayOfMonthTests {
        @Test
        fun `Should hold correct day of month based on the Calendar API`() =
            runTest {
                sut.currentDayOfMonth shouldBeEqualTo currentDayOfMonthFake
            }
    }

    @Nested
    @DisplayName("initialCalendar")
    inner class InitialCalendarTests {
        @Test
        fun `Should hold correct initial calendar based on the values of current month and year`() =
            runTest {
                val adjustedMonthFake = currentMonthFake + 1

                sut.initialCalendar shouldBeEqualTo CalendarMonthYear(
                    id = adjustedMonthFake + currentYearFake,
                    month = adjustedMonthFake,
                    year = currentYearFake,
                )
            }
    }

    @Nested
    @DisplayName("onDateRangeSelected")
    inner class OnDateRangeSelectedTests {
        @Test
        fun `Should update the selected dates based on start & end selected timestamps, current range selection and current selected dates`() =
            runTest {
                val randomMapSize = faker.random.nextInt(min = 1, max = 200)
                val newSelectedDatesFake =
                    mutableMapOf<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>()
                        .apply {
                            for (i in 0..randomMapSize) {
                                val randomSelectedDatesSize =
                                    faker.random.nextInt(min = 1, max = 31)

                                val randomCalendarMonthYear = createCalendarMonthYearFake()
                                val randomSelectedDatesList = List(randomSelectedDatesSize) {
                                    createCalendarSelectedDate()
                                }.toImmutableList()

                                put(randomCalendarMonthYear, randomSelectedDatesList)
                            }
                        }
                        .toImmutableMap()

                val startDateTimeMillisFake = faker.random.nextLong()
                val endDateTimeMillisFake = faker.random.nextLong()

                every {
                    calendarExportUtilsMock.getNewSelectedDates(
                        startDateTimeMillis = startDateTimeMillisFake,
                        endDateTimeMillis = endDateTimeMillisFake,
                        currentRangeCount = sut.rangeSelectionCount.value,
                        currentSelectedDates = sut.selectedDates.value,
                    )
                } returns newSelectedDatesFake

                // assert default values
                sut.rangeSelectionCount.value shouldBeEqualTo 1
                sut.selectedDates.value shouldBeEqualTo persistentMapOf()

                sut.onDateRangeSelected(
                    startDateTimeMillis = startDateTimeMillisFake,
                    endDateTimeMillis = endDateTimeMillisFake,
                )

                sut.selectedDates.value shouldBeEqualTo newSelectedDatesFake
            }

        @Test
        fun `Should increment the range selection counter by one`() =
            runTest {
                val startDateTimeMillisFake = faker.random.nextLong()
                val endDateTimeMillisFake = faker.random.nextLong()

                // not core for this test
                every {
                    calendarExportUtilsMock.getNewSelectedDates(
                        startDateTimeMillis = startDateTimeMillisFake,
                        endDateTimeMillis = endDateTimeMillisFake,
                        currentRangeCount = sut.rangeSelectionCount.value,
                        currentSelectedDates = sut.selectedDates.value,
                    )
                } returns persistentMapOf()

                val defaultRangeSelectionCount = sut.rangeSelectionCount.value

                sut.onDateRangeSelected(
                    startDateTimeMillis = startDateTimeMillisFake,
                    endDateTimeMillis = endDateTimeMillisFake,
                )

                sut.rangeSelectionCount.value shouldBeEqualTo defaultRangeSelectionCount + 1
            }
    }

    @Nested
    @DisplayName("onClearDateRangeSelection")
    inner class OnClearDateRangeSelectionTests {
        @Test
        fun `Should restore the range selection counter count to one`() =
            runTest { }

        @Test
        fun `Should clear the current selected dates map`() =
            runTest { }

        @Test
        fun `Should clear the current calendar label input text, when the current selected date range is cleared`() =
            runTest { }
    }

    @Nested
    @DisplayName("onCalendarLabelAssign")
    inner class OnCalendarLabelAssignTests {
        @Test
        fun `Should update the current calendar label input text`() =
            runTest { }
    }

    @Nested
    @DisplayName("onStartCalendarsExport")
    inner class OnStartCalendarsExportTests {
        @Test
        fun `Should fill the calendars bitmaps map with default null values to trigger calendars export start`() =
            runTest { }

        @Test
        fun `Should emit a 'missing calendar bitmap' UI event to the first calendar in sequence, when there is at least one bitmap already collected and yet there are missing bitmaps`() =
            runTest { }

        @Test
        fun `Should clear out the app's cache folder when all bitmaps are collected to export`() =
            runTest { }

        @Test
        fun `Should emit a 'data source error' UI event and stop calendars export, when an error result comes out when clearing out the app's cache folder`() =
            runTest { }

        @Test
        fun `Should save each bitmap in the app's cache folder and then emit a 'saving bitmaps success' UI event containing their content URIs for external access`() =
            runTest { }

        @Test
        fun `Should reset calendars bitmaps map, when all bitmaps are saved successfully`() =
            runTest { }
    }

    @Nested
    @DisplayName("onConvertedCalendarToBitmap")
    inner class OnConvertedCalendarToBitmapTests {
        @Test
        fun `Should update the calendars bitmaps map entry associated with a calendar with it's collected bitmap`() =
            runTest { }
    }
}
