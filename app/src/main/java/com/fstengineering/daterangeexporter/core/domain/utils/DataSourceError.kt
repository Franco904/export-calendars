package com.fstengineering.daterangeexporter.core.domain.utils

sealed interface DataSourceError : Error {
    enum class InternalStorageError : DataSourceError {
        IOError,
        BitmapCompressError,
        UnknownError,
    }
}
