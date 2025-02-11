package com.example.daterangeexporter.calendarExport

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class CalendarExportViewModel : ViewModel() {
    private val _calendarBitmaps = MutableStateFlow<Map<CalendarMonthYear, ImageBitmap>>(emptyMap())
    val calendarBitmaps = _calendarBitmaps.asStateFlow()
        .filter { it.isNotEmpty() }
        .map { it.toSortedMap(compareBy { monthYear -> monthYear.id }) }

    fun emitCalendarBitmap(
        calendarMonthYear: CalendarMonthYear,
        imageBitmap: ImageBitmap,
    ) {
        _calendarBitmaps.update {
            it
                .toMutableMap()
                .apply { put(calendarMonthYear, imageBitmap) }
        }
    }

    fun clearCalendarBitmaps() {
        _calendarBitmaps.update { emptyMap() }
    }
}
