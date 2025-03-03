package com.fstengineering.daterangeexporter.core.data.dataSources.appSpecificStorage.interfaces

import android.graphics.Bitmap
import java.io.File

interface AppSpecificStorage {
    suspend fun saveImage(
        bitmap: Bitmap,
        fileName: String,
        parentFolder: File? = null,
    ): File

    suspend fun clearCacheDir()
}
