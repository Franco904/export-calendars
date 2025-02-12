package com.example.daterangeexporter.core.presentation.utils

import com.example.daterangeexporter.R
import com.example.daterangeexporter.core.domain.utils.DataSourceError

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
