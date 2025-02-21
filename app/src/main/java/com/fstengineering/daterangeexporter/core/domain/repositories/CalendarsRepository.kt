package com.fstengineering.daterangeexporter.core.domain.repositories

import android.graphics.Bitmap
import com.fstengineering.daterangeexporter.core.domain.utils.DataSourceError
import com.fstengineering.daterangeexporter.core.domain.utils.Result
import java.io.File

interface CalendarsRepository {
    suspend fun saveCalendarBitmap(
        bitmap: Bitmap,
        fileName: String,
        parentFolder: File? = null,
    ): Result<File, DataSourceError>

    suspend fun clearCacheDir(): Result<Unit, DataSourceError>
}
