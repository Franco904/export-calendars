package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.models.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.models.RangeSelectionLabel
import com.example.daterangeexporter.calendarExport.utils.CalendarExportUtils
import com.example.daterangeexporter.core.domain.repositories.CalendarsRepository
import com.example.daterangeexporter.core.domain.utils.onError
import com.example.daterangeexporter.core.domain.utils.onSuccess
import com.example.daterangeexporter.core.presentation.utils.toUiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

class CalendarExportViewModel(
    private val appContext: Context,
    private val calendarsRepository: CalendarsRepository,
) : ViewModel() {
    private val calendar = Calendar.getInstance()

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    val initialCalendar = CalendarMonthYear(
        id = currentMonth + currentYear,
        month = currentMonth,
        year = currentYear,
    )

    private val _rangeSelectionLabel = MutableStateFlow(RangeSelectionLabel.First.count)
    val rangeSelectionLabel = _rangeSelectionLabel.asStateFlow()

    private val _selectedDates =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>>(
            persistentMapOf()
        )
    val selectedDates = _selectedDates.asStateFlow()

    private val _calendarLabelInput = MutableStateFlow<String?>(null)
    val calendarLabelInput = _calendarLabelInput.asStateFlow()

    private val _isConvertingToBitmap = MutableStateFlow<ImmutableMap<CalendarMonthYear, Boolean>>(
        persistentMapOf()
    )
    val isConvertingToBitmap = _isConvertingToBitmap.asStateFlow()

    private val _convertedCalendarsBitmaps = MutableStateFlow<Map<CalendarMonthYear, Bitmap>>(
        emptyMap()
    )

    fun onDateRangeSelected(
        startDateTimeMillis: Long,
        endDateTimeMillis: Long,
    ) {
        _selectedDates.update {
            CalendarExportUtils.getSelectedDates(
                startDateTimeMillis = startDateTimeMillis,
                endDateTimeMillis = endDateTimeMillis,
                currentRangeCount = rangeSelectionLabel.value,
                currentSelectedDates = selectedDates.value,
            )
        }

        _rangeSelectionLabel.update { it + 1 }
    }

    fun onClearDateRangeSelection() {
        _rangeSelectionLabel.update { RangeSelectionLabel.First.count }
        _selectedDates.update { persistentMapOf() }
        _calendarLabelInput.update { null }
    }

    fun onCalendarLabelAssign(label: String) {
        _calendarLabelInput.update { label }
    }

    fun onStartCalendarsExport() {
        _isConvertingToBitmap.update {
            _selectedDates.value
                .mapValues { true }
                .toImmutableMap()
        }

        viewModelScope.launch {
            _convertedCalendarsBitmaps
                .filter { it.isNotEmpty() }
                .collect { checkMissingCalendarsBitmaps() }
        }
    }

    private suspend fun checkMissingCalendarsBitmaps() {
        if (isConvertingToBitmap.value.isNotEmpty()) {
            val firstMissingCalendarIndex =
                selectedDates.value.keys.indexOfFirst { monthYear ->
                    monthYear == isConvertingToBitmap.value.keys.first()
                }

            delay(150.milliseconds)
            _uiEvents.send(
                UiEvents.MissingCalendarBitmap(firstMissingBitmapIndex = firstMissingCalendarIndex),
            )
        } else {
            _isConvertingToBitmap.update { persistentMapOf() }

            saveCalendarsBitmaps()
        }
    }

    fun onConvertedCalendarToBitmap(
        calendarMonthYear: CalendarMonthYear,
        bitmap: Bitmap,
    ) {
        _isConvertingToBitmap.update {
            isConvertingToBitmap.value
                .filterKeys { monthYear -> monthYear != calendarMonthYear }
                .toImmutableMap()
        }

        _convertedCalendarsBitmaps.update {
            it
                .toMutableMap()
                .apply { put(calendarMonthYear, bitmap) }
        }
    }

    private fun saveCalendarsBitmaps() {
        viewModelScope.launch {
            val contentUris = arrayListOf<Uri>()

            _convertedCalendarsBitmaps.value.forEach { (calendarMonthYear, calendarBitmap) ->
                val currentTimestamp = Calendar.getInstance().timeInMillis
                val monthYearString = "${calendarMonthYear.month}${calendarMonthYear.year}"

                calendarsRepository.saveCalendarBitmap(
                    bitmap = calendarBitmap,
                    fileName = "calendar-$monthYearString-$currentTimestamp.png",
                    parentFolder = appContext.cacheDir,
                )
                    .onError { error ->
                        _isConvertingToBitmap.update { persistentMapOf() }

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

            _convertedCalendarsBitmaps.update { emptyMap() }

            _uiEvents.send(
                UiEvents.SaveCalendarsBitmapsSuccess(calendarsContentUris = contentUris),
            )
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data class SaveCalendarsBitmapsSuccess(val calendarsContentUris: ArrayList<Uri>) : UiEvents

        data class MissingCalendarBitmap(val firstMissingBitmapIndex: Int) : UiEvents
    }
}
