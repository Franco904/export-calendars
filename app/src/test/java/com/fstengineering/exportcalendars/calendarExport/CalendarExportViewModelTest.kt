package com.fstengineering.exportcalendars.calendarExport

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import app.cash.turbine.test
import com.fstengineering.exportcalendars.calendarExport.models.CalendarMonthYear
import com.fstengineering.exportcalendars.calendarExport.models.CalendarSelectedDate
import com.fstengineering.exportcalendars.calendarExport.utils.interfaces.CalendarExportUtils
import com.fstengineering.exportcalendars.core.application.contentProviders.interfaces.AppFileProviderHandler
import com.fstengineering.exportcalendars.core.domain.repositories.CalendarsRepository
import com.fstengineering.exportcalendars.core.domain.utils.DataSourceError
import com.fstengineering.exportcalendars.core.domain.utils.Result
import com.fstengineering.exportcalendars.core.domain.utils.ValidationError
import com.fstengineering.exportcalendars.core.domain.validators.interfaces.CalendarsValidator
import com.fstengineering.exportcalendars.core.presentation.utils.uiConverters.toUiMessage
import com.fstengineering.exportcalendars.testUtils.MainDispatcherExtension
import com.fstengineering.exportcalendars.testUtils.constants.DATA_SOURCE_ERROR_CONVERTER_FILE_NAME
import com.fstengineering.exportcalendars.testUtils.constants.VALIDATION_ERROR_CONVERTER_FILE_NAME
import com.fstengineering.exportcalendars.testUtils.faker
import com.fstengineering.exportcalendars.testUtils.randoms.createCalendarMonthYearRandom
import com.fstengineering.exportcalendars.testUtils.randoms.createCalendarSelectedDateRandom
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.util.Calendar
import kotlin.properties.Delegates.notNull

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class CalendarExportViewModelTest {
    private lateinit var sut: CalendarExportViewModel

    private lateinit var calendarMock: Calendar
    private lateinit var appContextMock: Context
    private lateinit var calendarsRepositoryMock: CalendarsRepository
    private lateinit var calendarsValidatorMock: CalendarsValidator
    private lateinit var calendarExportUtilsMock: CalendarExportUtils
    private lateinit var appFileProviderHandlerMock: AppFileProviderHandler

    private val currentDayOfMonthRandom = faker.random.nextInt()
    private val currentMonthRandom = faker.random.nextInt()
    private val currentYearRandom = faker.random.nextInt()

    @BeforeEach
    fun setUp() {
        restartMocks()

        sut = CalendarExportViewModel(
            calendar = calendarMock,
            appContext = appContextMock,
            calendarsRepository = calendarsRepositoryMock,
            calendarsValidator = calendarsValidatorMock,
            calendarExportUtils = calendarExportUtilsMock,
            appFileProviderHandler = appFileProviderHandlerMock,
        )
    }

    private fun restartMocks() {
        mockkStatic(Calendar::class)

        calendarMock = mockk(relaxUnitFun = true) {
            every { get(Calendar.DAY_OF_MONTH) } returns currentDayOfMonthRandom
            every { get(Calendar.MONTH) } returns currentMonthRandom
            every { get(Calendar.YEAR) } returns currentYearRandom
        }

        appContextMock = mockk(relaxUnitFun = true)
        calendarsRepositoryMock = mockk(relaxUnitFun = true)
        calendarsValidatorMock = mockk(relaxUnitFun = true)
        calendarExportUtilsMock = mockk(relaxUnitFun = true)
        appFileProviderHandlerMock = mockk(relaxUnitFun = true)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Calendar::class)
    }

    @Nested
    @DisplayName("currentDayOfMonth")
    inner class CurrentDayOfMonthTests {
        @Test
        fun `Should hold correct day of month based on the Calendar API`() {
            sut.currentDayOfMonth shouldBeEqualTo currentDayOfMonthRandom
        }
    }

    @Nested
    @DisplayName("initialCalendar")
    inner class InitialCalendarTests {
        @Test
        fun `Should hold correct initial calendar based on the values of current month and year`() {
            val adjustedMonthRandom = currentMonthRandom + 1

            sut.initialCalendar shouldBeEqualTo CalendarMonthYear(
                id = adjustedMonthRandom + currentYearRandom,
                month = adjustedMonthRandom,
                year = currentYearRandom,
            )
        }
    }

    @Nested
    @DisplayName("onDateRangeSelected")
    inner class OnDateRangeSelectedTests {
        private var startDateTimeMillisRandom by notNull<Long>()
        private var endDateTimeMillisRandom by notNull<Long>()

        @BeforeEach
        fun setUp() {
            startDateTimeMillisRandom = faker.random.nextLong()
            endDateTimeMillisRandom = faker.random.nextLong()
        }

        @Test
        fun `Should update the selected dates based on start & end selected timestamps, current range selection and current selected dates`() {
            val mapSizeRandom = faker.random.nextInt(min = 1, max = 200)
            val newSelectedDatesRandom =
                mutableMapOf<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>()
                    .apply {
                        for (i in 0..mapSizeRandom) {
                            val randomSelectedDatesSize =
                                faker.random.nextInt(min = 1, max = 31)

                            val randomCalendarMonthYear = createCalendarMonthYearRandom()
                            val randomSelectedDatesList = List(randomSelectedDatesSize) {
                                createCalendarSelectedDateRandom()
                            }.toImmutableList()

                            put(randomCalendarMonthYear, randomSelectedDatesList)
                        }
                    }
                    .toImmutableMap()

            every {
                calendarExportUtilsMock.getNewSelectedDates(
                    startDateTimeMillis = startDateTimeMillisRandom,
                    endDateTimeMillis = endDateTimeMillisRandom,
                    currentRangeCount = sut.rangeSelectionCount.value,
                    currentSelectedDates = sut.selectedDates.value,
                )
            } returns newSelectedDatesRandom

            // assert default value
            sut.selectedDates.value shouldBeEqualTo persistentMapOf()

            sut.onDateRangeSelected(
                startDateTimeMillis = startDateTimeMillisRandom,
                endDateTimeMillis = endDateTimeMillisRandom,
            )

            sut.selectedDates.value shouldBeEqualTo newSelectedDatesRandom
        }

        @Test
        fun `Should increment the range selection counter by one`() {
            // not core for this test
            every {
                calendarExportUtilsMock.getNewSelectedDates(
                    startDateTimeMillis = startDateTimeMillisRandom,
                    endDateTimeMillis = endDateTimeMillisRandom,
                    currentRangeCount = sut.rangeSelectionCount.value,
                    currentSelectedDates = sut.selectedDates.value,
                )
            } returns persistentMapOf()

            // assert default value
            val defaultRangeSelectionCount = sut.rangeSelectionCount.value
            defaultRangeSelectionCount shouldBeEqualTo 1

            sut.onDateRangeSelected(
                startDateTimeMillis = startDateTimeMillisRandom,
                endDateTimeMillis = endDateTimeMillisRandom,
            )

            sut.rangeSelectionCount.value shouldBeEqualTo defaultRangeSelectionCount + 1
        }
    }

    @Nested
    @DisplayName("onClearDateRangeSelection")
    inner class OnClearDateRangeSelectionTests {
        @Test
        fun `Should restore the range selection counter count to one`() {
            sut.onClearDateRangeSelection()

            sut.rangeSelectionCount.value shouldBeEqualTo 1
        }

        @Test
        fun `Should clear the current selected dates map`() {
            sut.onClearDateRangeSelection()

            sut.selectedDates.value shouldBeEqualTo persistentMapOf()
        }

        @Test
        fun `Should clear the current calendar label input text`() {
            sut.onClearDateRangeSelection()

            with(sut.calendarFormUiState.value) {
                label shouldBeEqualTo null
                labelError shouldBeEqualTo null
            }
        }
    }

    @Nested
    @DisplayName("onCalendarLabelChange")
    inner class OnCalendarLabelChangeTests {
        @Test
        fun `Should clear label field error, keeping the current label text unmodified`() {
            val currentLabel = sut.calendarFormUiState.value.label

            sut.onCalendarLabelChange()

            with(sut.calendarFormUiState.value) {
                label shouldBeEqualTo currentLabel
                labelError shouldBeEqualTo null
            }
        }
    }

    @Nested
    @DisplayName("onCalendarLabelInputCancel")
    inner class OnCalendarLabelInputCancelTests {
        @Test
        fun `Should clear label field error, keeping the current label text unmodified`() {
            val currentLabel = sut.calendarFormUiState.value.label

            sut.onCalendarLabelInputCancel()

            with(sut.calendarFormUiState.value) {
                label shouldBeEqualTo currentLabel
                labelError shouldBeEqualTo null
            }
        }
    }

    @Nested
    @DisplayName("onCalendarLabelAssign")
    inner class OnCalendarLabelAssignTests {
        private lateinit var randomLabel: String

        @BeforeEach
        fun setUp() {
            mockkStatic(VALIDATION_ERROR_CONVERTER_FILE_NAME)

            randomLabel = faker.random.randomString(min = 1, max = 20)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(VALIDATION_ERROR_CONVERTER_FILE_NAME)
        }

        @Test
        fun `Should interrupt calendar label assign, when the validation results any error`() =
            runTest {
                every {
                    calendarsValidatorMock.validateLabel(randomLabel)
                } returns Result.Error(error = ValidationError.CalendarLabel.entries.random())

                val errorMessageId = faker.random.nextInt()
                for (error in ValidationError.CalendarLabel.entries) {
                    every { error.toUiMessage() } returns errorMessageId
                }

                sut.uiEvents.test {
                    sut.onCalendarLabelAssign(label = randomLabel)

                    with(sut.calendarFormUiState.value) {
                        label shouldNotBeEqualTo randomLabel
                        labelError shouldBeEqualTo errorMessageId
                    }

                    expectNoEvents()
                }
            }

        @Test
        fun `Should update the current calendar label input text, when the validation results in success`() {
            every {
                calendarsValidatorMock.validateLabel(randomLabel)
            } returns Result.Success(data = Unit)

            sut.onCalendarLabelAssign(label = randomLabel)

            with(sut.calendarFormUiState.value) {
                label shouldBeEqualTo randomLabel
                labelError shouldBeEqualTo null
            }
        }

        @Test
        fun `Should send 'calendar label assigned' UI event, notifying the UI about the new label assign, when the validation results in success`() =
            runTest {
                every {
                    calendarsValidatorMock.validateLabel(randomLabel)
                } returns Result.Success(data = Unit)

                sut.uiEvents.test {
                    sut.onCalendarLabelAssign(label = randomLabel)

                    val event = awaitItem()
                    event shouldBeEqualTo CalendarExportViewModel.UiEvents.CalendarLabelAssigned

                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()
                }
            }
    }

    // Tests depend on onDateRangeSelected and onConvertedCalendarToBitmap
    @Nested
    @DisplayName("onStartCalendarsExport")
    inner class OnStartCalendarsExportTests {
        private val newSelectedDatesRandom = mutableMapOf(
            createCalendarMonthYearRandom() to List(2) { createCalendarSelectedDateRandom() }.toImmutableList(),
            createCalendarMonthYearRandom() to List(2) { createCalendarSelectedDateRandom() }.toImmutableList(),
        ).toImmutableMap()

        @BeforeEach
        fun setUp() {
            configureDefaultStubs()

            val startDateTimeMillisRandom = faker.random.nextLong()
            val endDateTimeMillisRandom = faker.random.nextLong()

            every {
                calendarExportUtilsMock.getNewSelectedDates(
                    startDateTimeMillis = startDateTimeMillisRandom,
                    endDateTimeMillis = endDateTimeMillisRandom,
                    currentRangeCount = sut.rangeSelectionCount.value,
                    currentSelectedDates = sut.selectedDates.value,
                )
            } returns newSelectedDatesRandom

            sut.onDateRangeSelected(
                startDateTimeMillis = startDateTimeMillisRandom,
                endDateTimeMillis = endDateTimeMillisRandom,
            )
        }

        private fun configureDefaultStubs() {
            mockkStatic(DATA_SOURCE_ERROR_CONVERTER_FILE_NAME)

            coEvery { calendarsRepositoryMock.clearCacheDir() } returns Result.Success(data = Unit)

            every { calendarMock.timeInMillis } returns System.currentTimeMillis()

            every { appContextMock.cacheDir } returns mockk<File>()

            coEvery {
                calendarsRepositoryMock.saveCalendarBitmap(any(), any(), any())
            } returns Result.Success(data = mockk<File>())

            every {
                appFileProviderHandlerMock.getUriForInternalAppFile(file = any())
            } returns mockk<Uri>()

            every {
                DataSourceError.AppSpecificStorageError.UnknownError.toUiMessage()
            } returns 0
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(DATA_SOURCE_ERROR_CONVERTER_FILE_NAME)
        }

        @Test
        fun `Should fill the calendars bitmaps map with default null values and same size as the selected dates map to trigger calendars export start`() =
            runTest {
                sut.onStartCalendarsExport()
                advanceUntilIdle()

                val expectedCalendarsBitmaps = mutableMapOf(
                    newSelectedDatesRandom.keys.toList()[0] to null,
                    newSelectedDatesRandom.keys.toList()[1] to null,
                ).toImmutableMap()

                sut.calendarsBitmaps.value shouldBeEqualTo expectedCalendarsBitmaps
            }

        @Test
        fun `Should emit a 'missing calendar bitmap' UI event to the first calendar in sequence, when there is at least one missing bitmap to collect`() =
            runTest {
                // Pre condition
                sut.calendarsBitmaps.value.size shouldNotBeEqualTo newSelectedDatesRandom.size

                sut.uiEvents.test {
                    sut.onStartCalendarsExport()
                    advanceUntilIdle()

                    val event = awaitItem()
                    event shouldBeEqualTo CalendarExportViewModel.UiEvents.MissingCalendarBitmap(
                        firstMissingBitmapIndex = 0,
                    )

                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()
                }
            }

        @Test
        fun `Should clear out the app's cache folder when all bitmaps are collected to export`() =
            runTest {
                val bitmapMock1 = mockk<Bitmap>(relaxUnitFun = true)
                val bitmapMock2 = mockk<Bitmap>(relaxUnitFun = true)

                sut.onStartCalendarsExport()

                sut.onConvertedCalendarToBitmap(
                    calendarMonthYear = newSelectedDatesRandom.keys.toList()[0],
                    bitmap = bitmapMock1,
                )

                sut.onConvertedCalendarToBitmap(
                    calendarMonthYear = newSelectedDatesRandom.keys.toList()[1],
                    bitmap = bitmapMock2,
                )

                advanceUntilIdle()

                coVerify(exactly = 1) { calendarsRepositoryMock.clearCacheDir() }
            }

        @Test
        fun `Should emit a 'data source error' UI event and stop calendars export, when an error result comes out when clearing out the app's cache folder`() =
            runTest {
                val errorResult = Result.Error<Unit, DataSourceError>(
                    error = DataSourceError.AppSpecificStorageError.UnknownError,
                )
                coEvery {
                    calendarsRepositoryMock.clearCacheDir()
                } returns errorResult

                val bitmapMock1 = mockk<Bitmap>(relaxUnitFun = true)
                val bitmapMock2 = mockk<Bitmap>(relaxUnitFun = true)

                sut.uiEvents.test {
                    sut.onStartCalendarsExport()

                    sut.onConvertedCalendarToBitmap(
                        calendarMonthYear = newSelectedDatesRandom.keys.toList()[0],
                        bitmap = bitmapMock1,
                    )

                    sut.onConvertedCalendarToBitmap(
                        calendarMonthYear = newSelectedDatesRandom.keys.toList()[1],
                        bitmap = bitmapMock2,
                    )

                    advanceUntilIdle()

                    val event = awaitItem()
                    event shouldBeEqualTo CalendarExportViewModel.UiEvents.DataSourceErrorEvent(
                        error = errorResult.error,
                    )

                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()
                }
            }

        @Test
        fun `Should emit a 'save bitmaps success' UI event containing their content URIs for external access and then clear calendars bitmaps map, when all bitmaps are saved successfully`() =
            runTest {
                val currentTimestamp = System.currentTimeMillis()
                every { calendarMock.timeInMillis } returns currentTimestamp

                val cacheFolderMock = mockk<File>(relaxUnitFun = true)
                every { appContextMock.cacheDir } returns cacheFolderMock

                val bitmapMock1 = mockk<Bitmap>(relaxUnitFun = true)
                val calendarMonthYear1 = newSelectedDatesRandom.keys.toList()[0]
                val bitmapResultFileMock1 = mockk<File>(relaxUnitFun = true)

                coEvery {
                    val monthYearString = "${calendarMonthYear1.month}${calendarMonthYear1.year}"

                    calendarsRepositoryMock.saveCalendarBitmap(
                        bitmap = bitmapMock1,
                        fileName = "calendar-$monthYearString-$currentTimestamp.png",
                        parentFolder = cacheFolderMock,
                    )
                } returns Result.Success(data = bitmapResultFileMock1)

                val bitmapMock2 = mockk<Bitmap>(relaxUnitFun = true)
                val calendarMonthYear2 = newSelectedDatesRandom.keys.toList()[1]
                val bitmapResultFileMock2 = mockk<File>(relaxUnitFun = true)

                coEvery {
                    val monthYearString = "${calendarMonthYear2.month}${calendarMonthYear2.year}"

                    calendarsRepositoryMock.saveCalendarBitmap(
                        bitmap = bitmapMock2,
                        fileName = "calendar-$monthYearString-$currentTimestamp.png",
                        parentFolder = cacheFolderMock,
                    )
                } returns Result.Success(data = bitmapResultFileMock2)

                val contentUriBitmapFileMock1 = mockk<Uri>(relaxUnitFun = true)
                every {
                    appFileProviderHandlerMock.getUriForInternalAppFile(bitmapResultFileMock1)
                } returns contentUriBitmapFileMock1

                val contentUriBitmapFileMock2 = mockk<Uri>(relaxUnitFun = true)
                every {
                    appFileProviderHandlerMock.getUriForInternalAppFile(bitmapResultFileMock2)
                } returns contentUriBitmapFileMock2

                sut.uiEvents.test {
                    sut.onStartCalendarsExport()

                    sut.onConvertedCalendarToBitmap(
                        calendarMonthYear = newSelectedDatesRandom.keys.toList()[0],
                        bitmap = bitmapMock1,
                    )

                    sut.onConvertedCalendarToBitmap(
                        calendarMonthYear = newSelectedDatesRandom.keys.toList()[1],
                        bitmap = bitmapMock2,
                    )

                    advanceUntilIdle()

                    val event = awaitItem()
                    event shouldBeEqualTo CalendarExportViewModel.UiEvents.SaveCalendarsBitmapsSuccess(
                        calendarsContentUris = arrayListOf(
                            contentUriBitmapFileMock1,
                            contentUriBitmapFileMock2,
                        ),
                    )

                    sut.calendarsBitmaps.value.size shouldBeEqualTo 0

                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()
                }
            }

        @Test
        fun `Should emit a 'data source error' UI event, reset calendars bitmaps map and then stop calendars export, when an error result comes out when saving a bitmap`() =
            runTest {
                val currentTimestamp = System.currentTimeMillis()
                every { calendarMock.timeInMillis } returns currentTimestamp

                val cacheFolderMock = mockk<File>(relaxUnitFun = true)
                every { appContextMock.cacheDir } returns cacheFolderMock

                val bitmapMock1 = mockk<Bitmap>(relaxUnitFun = true)
                val calendarMonthYear1 = newSelectedDatesRandom.keys.toList()[0]
                val bitmapResultFileMock1 = mockk<File>(relaxUnitFun = true)

                coEvery {
                    val monthYearString = "${calendarMonthYear1.month}${calendarMonthYear1.year}"

                    calendarsRepositoryMock.saveCalendarBitmap(
                        bitmap = bitmapMock1,
                        fileName = "calendar-$monthYearString-$currentTimestamp.png",
                        parentFolder = cacheFolderMock,
                    )
                } returns Result.Success(data = bitmapResultFileMock1)

                val calendarMonthYear2 = newSelectedDatesRandom.keys.toList()[1]
                val bitmapMock2 = mockk<Bitmap>(relaxUnitFun = true)

                val errorResult = Result.Error<File, DataSourceError>(
                    error = DataSourceError.AppSpecificStorageError.UnknownError,
                )
                coEvery {
                    val monthYearString = "${calendarMonthYear2.month}${calendarMonthYear2.year}"

                    calendarsRepositoryMock.saveCalendarBitmap(
                        bitmap = bitmapMock2,
                        fileName = "calendar-$monthYearString-$currentTimestamp.png",
                        parentFolder = cacheFolderMock,
                    )
                } returns errorResult

                val errorMessageId = faker.random.nextInt()
                every {
                    DataSourceError.AppSpecificStorageError.UnknownError.toUiMessage()
                } returns errorMessageId

                sut.uiEvents.test {
                    sut.onStartCalendarsExport()

                    sut.onConvertedCalendarToBitmap(
                        calendarMonthYear = newSelectedDatesRandom.keys.toList()[0],
                        bitmap = bitmapMock1,
                    )

                    sut.onConvertedCalendarToBitmap(
                        calendarMonthYear = newSelectedDatesRandom.keys.toList()[1],
                        bitmap = bitmapMock2,
                    )

                    advanceUntilIdle()

                    val event = awaitItem()
                    event shouldBeEqualTo CalendarExportViewModel.UiEvents.DataSourceErrorEvent(
                        error = errorResult.error,
                    )

                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()

                    sut.calendarsBitmaps.value.size shouldBeEqualTo 0
                }
            }
    }

    // Tests depend on onDateRangeSelected
    @Nested
    @DisplayName("onConvertedCalendarToBitmap")
    inner class OnConvertedCalendarToBitmapTests {
        private val newSelectedDatesRandom = mutableMapOf(
            createCalendarMonthYearRandom() to List(2) { createCalendarSelectedDateRandom() }.toImmutableList(),
            createCalendarMonthYearRandom() to List(2) { createCalendarSelectedDateRandom() }.toImmutableList(),
        ).toImmutableMap()

        @BeforeEach
        fun setUp() {
            val startDateTimeMillisRandom = faker.random.nextLong()
            val endDateTimeMillisRandom = faker.random.nextLong()

            every {
                calendarExportUtilsMock.getNewSelectedDates(
                    startDateTimeMillis = startDateTimeMillisRandom,
                    endDateTimeMillis = endDateTimeMillisRandom,
                    currentRangeCount = sut.rangeSelectionCount.value,
                    currentSelectedDates = sut.selectedDates.value,
                )
            } returns newSelectedDatesRandom

            sut.onDateRangeSelected(
                startDateTimeMillis = startDateTimeMillisRandom,
                endDateTimeMillis = endDateTimeMillisRandom,
            )
        }

        @Test
        fun `Should update the calendars bitmaps map entry associated with a calendar with it's collected bitmap`() {
            val calendarMonthYearRandom = newSelectedDatesRandom.keys.random()
            val bitmapMock: Bitmap = mockk(relaxUnitFun = true)

            sut.onConvertedCalendarToBitmap(
                calendarMonthYear = calendarMonthYearRandom,
                bitmap = bitmapMock,
            )

            sut.calendarsBitmaps.value[calendarMonthYearRandom]
                .shouldBeEqualTo(bitmapMock)
        }
    }

    @Nested
    @DisplayName("getDeviceFreeStoragePercent")
    inner class GetDeviceFreeStoragePercentTests {
        @Test
        fun `Should return the correct device storage free space percent of the total storage`() {
            val freeStorageSpace = faker.random.nextLong()
            coEvery {
                calendarsRepositoryMock.getDeviceFreeStorageBytes()
            } returns freeStorageSpace

            val totalStorageSpace = faker.random.nextLong()
            coEvery {
                calendarsRepositoryMock.getDeviceTotalStorageBytes()
            } returns totalStorageSpace

            val freeSpacePercent = sut.getDeviceFreeStoragePercent()

            freeSpacePercent.shouldBeEqualTo(
                (freeStorageSpace.toDouble() / totalStorageSpace.toDouble() * 100).toInt()
            )
        }
    }
}
