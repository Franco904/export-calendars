package com.fstengineering.daterangeexporter.core.presentation.utils.uiConverters

import com.fstengineering.daterangeexporter.R
import com.fstengineering.daterangeexporter.core.domain.utils.DataSourceError

fun DataSourceError.toUiMessage() = when (this) {
    DataSourceError.InternalStorageError.IOError -> {
        R.string.snackbar_internal_storage_serialization_error
    }

    DataSourceError.InternalStorageError.BitmapCompressError -> {
        R.string.snackbar_internal_storage_bitmap_compress_error
    }

    DataSourceError.InternalStorageError.UnknownError -> {
        R.string.snackbar_internal_storage_unknown_error
    }
}
