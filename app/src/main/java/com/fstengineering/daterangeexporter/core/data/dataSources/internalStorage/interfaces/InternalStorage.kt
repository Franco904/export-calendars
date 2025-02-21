package com.fstengineering.daterangeexporter.core.data.dataSources.internalStorage.interfaces

import android.graphics.Bitmap
import java.io.File

interface InternalStorage {
    suspend fun saveImage(
        bitmap: Bitmap,
        fileName: String,
        parentFolder: File? = null,
    ): File

    suspend fun clearCacheDir()
}
