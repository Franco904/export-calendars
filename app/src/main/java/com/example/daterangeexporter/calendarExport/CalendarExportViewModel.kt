package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.example.daterangeexporter.core.domain.repositories.CalendarsRepository
import com.example.daterangeexporter.core.domain.utils.onError
import com.example.daterangeexporter.core.domain.utils.onSuccess
import com.example.daterangeexporter.core.presentation.utils.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarExportViewModel(
    private val appContext: Context,
    private val calendarsRepository: CalendarsRepository,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _calendarBitmaps = MutableStateFlow<Map<CalendarMonthYear, Bitmap>>(emptyMap())
    val calendarBitmaps = _calendarBitmaps.asStateFlow()
        .filter { it.isNotEmpty() }
        .map { it.toSortedMap(compareBy { monthYear -> monthYear.id }) }

    fun saveCalendarsBitmaps(
        bitmaps: Map<CalendarMonthYear, Bitmap>
    ) {
        viewModelScope.launch {
            val contentUris = arrayListOf<Uri>()

            bitmaps.forEach { (calendarMonthYear, calendarBitmap) ->
                val currentTimestamp = Calendar.getInstance().timeInMillis
                val monthYearString = "${calendarMonthYear.month}${calendarMonthYear.year}"

                calendarsRepository.saveCalendarBitmap(
                    bitmap = calendarBitmap,
                    fileName = "calendar-$monthYearString-$currentTimestamp.png",
                    parentFolder = appContext.cacheDir,
                )
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    }
                    .onSuccess { file ->
                        val contentUri = FileProvider.getUriForFile(
                            /* context = */ appContext,
                            /* authority = */ "${appContext.packageName}.fileprovider",
                            file,
                        )

                        contentUris.add(contentUri)
                    }
            }

            _uiEvents.send(
                UiEvents.SaveCalendarsBitmapsSuccess(calendarsContentUris = contentUris),
            )
        }
    }

    fun emitCalendarBitmap(
        calendarMonthYear: CalendarMonthYear,
        bitmap: Bitmap,
    ) {
        _calendarBitmaps.update {
            it
                .toMutableMap()
                .apply { put(calendarMonthYear, bitmap) }
        }
    }

    fun clearCalendarBitmaps() {
        _calendarBitmaps.update { emptyMap() }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data class SaveCalendarsBitmapsSuccess(val calendarsContentUris: ArrayList<Uri>) : UiEvents
    }
}
