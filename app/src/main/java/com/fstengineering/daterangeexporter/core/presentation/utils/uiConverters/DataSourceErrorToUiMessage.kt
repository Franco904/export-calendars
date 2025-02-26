package com.fstengineering.daterangeexporter.core.presentation.utils.uiConverters

import com.fstengineering.daterangeexporter.R
import com.fstengineering.daterangeexporter.core.domain.utils.DataSourceError

fun DataSourceError.toUiMessage() = when (this) {
    DataSourceError.AppSpecificStorageError.IOError -> {
        R.string.dialog_internal_storage_io_error_message
    }

    DataSourceError.AppSpecificStorageError.BitmapCompressError -> {
        R.string.snackbar_internal_storage_bitmap_compress_error
    }

    DataSourceError.AppSpecificStorageError.UnknownError -> {
        R.string.snackbar_internal_storage_unknown_error
    }
}
