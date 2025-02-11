package com.example.daterangeexporter.core.infra

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object InternalStorageHandler {
    suspend fun Context.saveImage(
        bitmap: Bitmap,
        fileName: String,
        folder: File? = null,
    ): File {
        val file = if (folder != null) {
            File(folder, fileName)
        } else File(filesDir, fileName)

        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }

        return file
    }
}