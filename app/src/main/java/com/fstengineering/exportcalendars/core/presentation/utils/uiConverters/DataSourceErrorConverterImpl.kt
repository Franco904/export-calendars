package com.fstengineering.exportcalendars.core.presentation.utils.uiConverters

import com.fstengineering.exportcalendars.R
import com.fstengineering.exportcalendars.core.domain.utils.DataSourceError

class DataSourceErrorConverterImpl : ErrorConverter<DataSourceError> {
    override fun toUiMessage(error: DataSourceError) = when (error) {
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
}
