package com.fstengineering.daterangeexporter.core.data.repositories

import android.graphics.Bitmap
import com.fstengineering.daterangeexporter.core.application.monitoring.interfaces.AppLogger
import com.fstengineering.daterangeexporter.core.data.dataSources.appSpecificStorage.interfaces.AppSpecificStorage
import com.fstengineering.daterangeexporter.core.data.exceptions.InternalStorageException
import com.fstengineering.daterangeexporter.core.domain.repositories.CalendarsRepository
import com.fstengineering.daterangeexporter.core.domain.utils.DataSourceError
import com.fstengineering.daterangeexporter.core.domain.utils.Result
import java.io.File

class CalendarsRepositoryImpl(
    private val appSpecificStorage: AppSpecificStorage,
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

    companion object {
        private const val TAG = "CalendarsRepositoryImpl"
    }
}
