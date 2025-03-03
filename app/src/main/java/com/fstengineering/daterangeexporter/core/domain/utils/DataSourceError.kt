package com.fstengineering.daterangeexporter.core.domain.utils

sealed interface DataSourceError : Error {
    enum class AppSpecificStorageError : DataSourceError {
        IOError,
        BitmapCompressError,
        UnknownError,
    }
}
