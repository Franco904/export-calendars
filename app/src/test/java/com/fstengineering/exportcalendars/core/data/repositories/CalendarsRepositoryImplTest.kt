package com.fstengineering.exportcalendars.core.data.repositories

import android.graphics.Bitmap
import com.fstengineering.exportcalendars.core.application.monitoring.interfaces.AppLogger
import com.fstengineering.exportcalendars.core.data.dataSources.appSpecificStorage.interfaces.AppSpecificStorage
import com.fstengineering.exportcalendars.core.data.dataSources.storageStats.interfaces.StorageStatsHandler
import com.fstengineering.exportcalendars.core.data.exceptions.InternalStorageException
import com.fstengineering.exportcalendars.core.domain.utils.DataSourceError
import com.fstengineering.exportcalendars.core.domain.utils.Result
import com.fstengineering.exportcalendars.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.should
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class CalendarsRepositoryImplTest {
    private lateinit var sut: CalendarsRepositoryImpl

    private lateinit var appSpecificStorageMock: AppSpecificStorage
    private lateinit var storageStatsHandlerMock: StorageStatsHandler
    private lateinit var loggerMock: AppLogger

    private val bitmapMock = mockk<Bitmap>()
    private val fileNameFake = "test.png"

    @BeforeEach
    fun setUp() {
        appSpecificStorageMock = mockk(relaxUnitFun = true)
        storageStatsHandlerMock = mockk(relaxUnitFun = true)
        loggerMock = mockk(relaxUnitFun = true)

        sut = CalendarsRepositoryImpl(
            appSpecificStorage = appSpecificStorageMock,
            storageStatsHandler = storageStatsHandlerMock,
            logger = loggerMock,
        )
    }

    @Nested
    @DisplayName("saveCalendarBitmap")
    inner class SaveCalendarBitmapTests {
        @Test
        fun `Should return success result containing the bitmap file when the the image bitmap is successfully saved in internal storage`() =
            runTest {
                val expectedFile = mockk<File>()

                coEvery {
                    appSpecificStorageMock.saveImage(
                        bitmap = bitmapMock,
                        fileName = fileNameFake,
                        parentFolder = null,
                    )
                } returns expectedFile

                val result = sut.saveCalendarBitmap(
                    bitmap = bitmapMock,
                    fileName = fileNameFake,
                    parentFolder = null,
                )

                result.shouldBeInstanceOf<Result.Success<*, *>>()
                (result as Result.Success).data shouldBeEqualTo expectedFile
            }

        @Test
        fun `Should return error result with internal storage error when an InternalStorageException is thrown`() =
            runTest {
                coEvery {
                    appSpecificStorageMock.saveImage(
                        bitmap = bitmapMock,
                        fileName = fileNameFake,
                        parentFolder = null,
                    )
                } throws InternalStorageException.UnknownError(message = "test")

                val result = sut.saveCalendarBitmap(
                    bitmap = bitmapMock,
                    fileName = fileNameFake,
                    parentFolder = null,
                )

                result.shouldBeInstanceOf<Result.Error<*, *>>()
                (result as Result.Error).error should { this is DataSourceError.AppSpecificStorageError }
            }

        @Test
        fun `Should rethrow the exception when an unexpected exception is thrown`() = runTest {
            coEvery {
                appSpecificStorageMock.saveImage(
                    bitmap = bitmapMock,
                    fileName = fileNameFake,
                    parentFolder = null,
                )
            } throws Exception("unexpected")

            val exception = assertThrows<Exception> {
                sut.saveCalendarBitmap(
                    bitmap = bitmapMock,
                    fileName = fileNameFake,
                    parentFolder = null,
                )
            }

            exception.message shouldBeEqualTo "unexpected"
        }
    }

    @Nested
    @DisplayName("clearCacheDir")
    inner class ClearCacheDirTests {
        @Test
        fun `Should successfully clear the app's cache dir`() = runTest {
            sut.clearCacheDir()

            coVerify(exactly = 1) { appSpecificStorageMock.clearCacheDir() }
        }

        @Test
        fun `Should return error result with internal storage error when an InternalStorageException is thrown`() =
            runTest {
                coEvery {
                    appSpecificStorageMock.clearCacheDir()
                } throws InternalStorageException.UnknownError(message = "test")

                val result = sut.clearCacheDir()

                result.shouldBeInstanceOf<Result.Error<*, *>>()
                (result as Result.Error).error should { this is DataSourceError.AppSpecificStorageError }
            }

        @Test
        fun `Should rethrow the exception when an unexpected exception is thrown`() = runTest {
            coEvery {
                appSpecificStorageMock.clearCacheDir()
            } throws Exception("unexpected")

            val exception = assertThrows<Exception> {
                sut.clearCacheDir()
            }

            exception.message shouldBeEqualTo "unexpected"
        }
    }

    @Nested
    @DisplayName("getDeviceFreeStorageBytes")
    inner class GetDeviceFreeStorageBytesTests {
        @Test
        fun `Should return the device storage current free space`() = runTest {
            val expectedFreeStorageSpace = faker.random.nextLong()

            coEvery {
                storageStatsHandlerMock.getDeviceFreeStorageBytes()
            } returns expectedFreeStorageSpace

            val freeStorageSpace = sut.getDeviceFreeStorageBytes()

            freeStorageSpace shouldBeEqualTo expectedFreeStorageSpace
        }
    }

    @Nested
    @DisplayName("getDeviceTotalStorageBytes")
    inner class GetDeviceTotalStorageBytesTests {
        @Test
        fun `Should return the device storage total space`() = runTest {
            val expectedTotalStorageSpace = faker.random.nextLong()

            coEvery {
                storageStatsHandlerMock.getDeviceTotalStorageBytes()
            } returns expectedTotalStorageSpace

            val totalStorageSpace = sut.getDeviceTotalStorageBytes()

            totalStorageSpace shouldBeEqualTo expectedTotalStorageSpace
        }
    }
}
