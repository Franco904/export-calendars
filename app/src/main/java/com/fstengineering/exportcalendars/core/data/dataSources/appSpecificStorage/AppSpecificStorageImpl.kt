package com.fstengineering.exportcalendars.core.data.dataSources.appSpecificStorage

import android.content.Context
import android.graphics.Bitmap
import com.fstengineering.exportcalendars.core.data.dataSources.appSpecificStorage.interfaces.AppSpecificStorage
import com.fstengineering.exportcalendars.core.data.exceptions.asInternalStorageException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AppSpecificStorageImpl(
    private val appContext: Context,
) : AppSpecificStorage {
    override suspend fun saveImage(
        bitmap: Bitmap,
        fileName: String,
        parentFolder: File?,
    ): File {
        try {
            val file = if (parentFolder != null) {
                File(parentFolder, fileName)
            } else File(appContext.filesDir, fileName)

            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }

            return file
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            throw e.asInternalStorageException()
        }
    }

    override suspend fun clearCacheDir() {
        try {
            withContext(Dispatchers.IO) {
                appContext.cacheDir?.listFiles { cacheFile ->
                    cacheFile.deleteRecursively()
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            throw e.asInternalStorageException()
        }
    }
}
