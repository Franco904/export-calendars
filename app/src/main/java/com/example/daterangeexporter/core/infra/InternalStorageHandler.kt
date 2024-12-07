package com.example.daterangeexporter.core.infra

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object InternalStorageHandler {
    fun Context.saveImage(bitmap: Bitmap, fileName: String): File {
        val file = File(filesDir, fileName)
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        return file
    }

    inline fun Context.deleteAllFiles(on: (file: File) -> Boolean) {
        filesDir.listFiles()?.forEach { file ->
            if (file.isFile && on(file)) file.delete()
        }
    }
}