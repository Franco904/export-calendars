package com.example.daterangeexporter.core.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.core.utils.CalendarUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    initialCalendar: CalendarMonthYear,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
    isDateSelectionEmpty: Boolean,
    modifier: Modifier = Modifier,
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialDisplayedMonthMillis = CalendarUtils.getMonthTimestamp(
            month = initialCalendar.month,
            year = initialCalendar.year,
        )
    )

    LaunchedEffect(isDateSelectionEmpty) {
        if (isDateSelectionEmpty) {
            dateRangePickerState.setSelection(
                startDateMillis = null,
                endDateMillis = null,
            )
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        modifier = modifier
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Selecione o período da estadia",
                    modifier = Modifier
                        .padding(16.dp)
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                    selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                    displayMode = dateRangePickerState.displayMode,
                    dateFormatter = DatePickerDefaults.dateFormatter(),
                    modifier = Modifier
                        .padding(4.dp)
                        .scale(0.9f)
                )
            },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
