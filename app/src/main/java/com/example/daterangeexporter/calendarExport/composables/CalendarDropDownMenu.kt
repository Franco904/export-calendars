package com.example.daterangeexporter.calendarExport.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.R
import com.example.daterangeexporter.core.application.theme.AppTheme

@Composable
fun CalendarDropDownMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    mustShowAddNewDateRangeOption: Boolean,
    onAddNewDateRange: () -> Unit,
    onExportCalendar: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
) {
    DropdownMenu(
        expanded = isVisible,
        onDismissRequest = onDismiss,
        offset = offset,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        if (mustShowAddNewDateRangeOption) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_other_date_range_action_text),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.add_other_date_range_action_text),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                },
                onClick = onAddNewDateRange,
            )
        }
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.export_calendar_action_content_description),
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.export_calendar_action_text),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            },
            onClick = onExportCalendar,
        )
    }
}

@Preview
@Composable
fun CalendarDropDownMenuPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CalendarDropDownMenu(
            isVisible = true,
            onDismiss = {},
            mustShowAddNewDateRangeOption = true,
            onAddNewDateRange = {},
            onExportCalendar = {},
            modifier = modifier,
        )
    }
}
