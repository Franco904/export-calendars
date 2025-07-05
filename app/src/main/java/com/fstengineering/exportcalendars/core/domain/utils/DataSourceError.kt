package com.fstengineering.exportcalendars.core.domain.utils

sealed interface DataSourceError : Error {
    enum class AppSpecificStorageError : DataSourceError {
        IOError,
        BitmapCompressError,
        UnknownError,
    }
}
