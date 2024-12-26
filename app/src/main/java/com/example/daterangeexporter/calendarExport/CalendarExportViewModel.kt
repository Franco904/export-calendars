package com.example.daterangeexporter.calendarExport

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.localModels.CalendarSelectedDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

typealias SelectedDates = ImmutableMap<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>

class CalendarExportViewModel : ViewModel() {
    var initialCalendar by mutableStateOf(
        CalendarMonthYear(
            id = 0,
            month = 0,
            year = 0,
        )
    )

    val emptySelectedDates: SelectedDates = persistentMapOf(initialCalendar to persistentListOf())

    private val _selectedDates = MutableStateFlow(emptySelectedDates)
    val selectedDates: StateFlow<SelectedDates> = _selectedDates.asStateFlow()

    fun init(
        selectedMonth: Int,
        selectedYear: Int,
    ) {
        initialCalendar = CalendarMonthYear(
            id = selectedMonth + selectedYear,
            month = selectedMonth,
            year = selectedYear,
        )
    }

    fun onClearSelectedDates() {
        _selectedDates.update { emptySelectedDates }
    }

    fun onDateRangeSelected(newDates: SelectedDates) {
        _selectedDates.update {
            (selectedDates.value + newDates) as SelectedDates
        }
    }
}