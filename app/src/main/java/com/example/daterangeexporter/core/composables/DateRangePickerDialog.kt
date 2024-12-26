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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.R
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
import com.example.daterangeexporter.core.utils.getSelectableDates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
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
        ),
        selectableDates = getSelectableDates(
            initialCalendar = initialCalendar,
        ),
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
                Text(stringResource(R.string.date_range_picker_dialog_primary_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.date_range_picker_dialog_secondary_button))
            }
        },
        modifier = modifier
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = stringResource(R.string.date_range_picker_dialog_title),
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
                        .padding(6.dp)
                        .scale(0.9f)
                )
            },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun DateRangePickerDialogPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        DateRangePickerDialog(
            initialCalendar = CalendarMonthYear(0, 2, 2025),
            onDateRangeSelected = {},
            onDismiss = {},
            isDateSelectionEmpty = false,
        )
    }
}
