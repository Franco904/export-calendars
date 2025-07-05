package com.fstengineering.exportcalendars.core.data.repositories

import android.graphics.Bitmap
import com.fstengineering.exportcalendars.core.application.monitoring.interfaces.AppLogger
import com.fstengineering.exportcalendars.core.data.dataSources.appSpecificStorage.interfaces.AppSpecificStorage
import com.fstengineering.exportcalendars.core.data.dataSources.storageStats.interfaces.StorageStatsHandler
import com.fstengineering.exportcalendars.core.data.exceptions.InternalStorageException
import com.fstengineering.exportcalendars.core.domain.repositories.CalendarsRepository
import com.fstengineering.exportcalendars.core.domain.utils.DataSourceError
import com.fstengineering.exportcalendars.core.domain.utils.Result
import java.io.File

class CalendarsRepositoryImpl(
    private val appSpecificStorage: AppSpecificStorage,
    private val storageStatsHandler: StorageStatsHandler,
    private val logger: AppLogger,
) : CalendarsRepository {
    override suspend fun saveCalendarBitmap(
        bitmap: Bitmap,
        fileName: String,
        parentFolder: File?,
    ): Result<File, DataSourceError> {
        return try {
            val file = appSpecificStorage.saveImage(
                bitmap = bitmap,
                fileName = fileName,
                parentFolder = parentFolder,
            )

            Result.Success(data = file)
        } catch (e: Exception) {
            logger.logError(TAG, "saveCalendarBitmap - ${e.message}")

            val error = when (e) {
                is InternalStorageException -> e.toInternalStorageError()
                else -> throw e
            }

            Result.Error(error = error)
        }
    }

    override suspend fun clearCacheDir(): Result<Unit, DataSourceError> {
        return try {
            appSpecificStorage.clearCacheDir()

            Result.Success(data = Unit)
        } catch (e: Exception) {
            logger.logError(TAG, "clearCacheDir - ${e.message}")

            val error = when (e) {
                is InternalStorageException -> e.toInternalStorageError()
                else -> throw e
            }

            Result.Error(error = error)
        }
    }

    override fun getDeviceFreeStorageBytes(): Long {
        return storageStatsHandler.getDeviceFreeStorageBytes()
    }

    override fun getDeviceTotalStorageBytes(): Long {
        return storageStatsHandler.getDeviceTotalStorageBytes()
    }

    companion object {
        private const val TAG = "CalendarsRepositoryImpl"
    }
}
