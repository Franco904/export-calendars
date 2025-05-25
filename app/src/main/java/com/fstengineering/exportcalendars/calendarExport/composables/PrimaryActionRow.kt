package com.fstengineering.exportcalendars.calendarExport.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.exportcalendars.R
import com.fstengineering.exportcalendars.calendarExport.models.CalendarMonthYear
import com.fstengineering.exportcalendars.calendarExport.models.CalendarSelectedDate
import com.fstengineering.exportcalendars.core.presentation.composables.AppFilledButton
import com.fstengineering.exportcalendars.core.presentation.composables.AppFilledTonalButton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

@Composable
fun PrimaryActionRow(
    selectedDates: ImmutableMap<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>,
    onDateRangeSelect: () -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = selectedDates.isEmpty(),
        label = "PrimaryActionRow",
        modifier = modifier
    ) { isSelectedDatesEmpty ->
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            if (isSelectedDatesEmpty) {
                AppFilledButton(
                    icon = Icons.Default.DateRange,
                    text = stringResource(R.string.select_calendar_dates_action_text),
                    onClick = onDateRangeSelect,
                )
            } else {
                AppFilledTonalButton(
                    icon = Icons.Default.Close,
                    text = stringResource(R.string.clear_calendar_selected_dates_action_text),
                    onClick = onClearSelection,
                )
            }
        }
    }
}
