package com.fstengineering.daterangeexporter.core.data.exceptions

import com.fstengineering.daterangeexporter.core.domain.utils.DataSourceError
import java.io.IOException

sealed class InternalStorageException(message: String) : Exception(message) {
    class IOError(message: String?) :
        InternalStorageException("Failed to read/write to internal app storage: $message.")

    class BitmapCompressError(message: String?) :
        InternalStorageException("Failed to compress bitmap: $message.")

    class UnknownError(message: String?) : InternalStorageException("Unknown error: $message.")

    fun toInternalStorageError() = when (this) {
        is IOError -> DataSourceError.InternalStorageError.IOError
        is BitmapCompressError -> DataSourceError.InternalStorageError.BitmapCompressError
        is UnknownError -> DataSourceError.InternalStorageError.UnknownError
    }
}

fun Exception.asInternalStorageException(): InternalStorageException {
    val internalStorageException = when (this) {
        is IOException, is SecurityException -> {
            InternalStorageException.IOError(message = "$cause: ${message ?: "Unknown I/O error"}")
        }

        is IllegalStateException, is NullPointerException, is IllegalArgumentException -> {
            InternalStorageException.BitmapCompressError(message = "$cause: ${message ?: "Unknown bitmap compress error"}")
        }

        else -> {
            InternalStorageException.UnknownError(message = "$cause: ${message ?: "Unknown internal app storage error"}")
        }
    }

    return internalStorageException.apply {
        initCause(this@asInternalStorageException)
    }
}
