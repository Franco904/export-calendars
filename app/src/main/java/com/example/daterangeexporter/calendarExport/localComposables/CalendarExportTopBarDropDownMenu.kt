package com.example.daterangeexporter.calendarExport.localComposables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.R
import com.example.daterangeexporter.core.theme.AppTheme


@Composable
fun CalendarExportTopBarDropDownMenu(
    isVisible: Boolean,
    hasDatesSelected: Boolean,
    hasLabelAssigned: Boolean,
    onDatesSelect: () -> Unit,
    onLabelAssign: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstActionLabel = if (hasDatesSelected) {
        R.string.clear_calendar_selected_dates_action_text
    } else R.string.edit_calendar_selected_dates_action_text

    val secondActionLabel = if (hasLabelAssigned) {
        R.string.rename_calendar_action_text
    } else R.string.assign_name_calendar_action_text

    DropdownMenu(
        expanded = isVisible,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (hasDatesSelected) Icons.Default.Close else Icons.Default.Edit,
                    contentDescription = stringResource(firstActionLabel),
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            text = {
                Text(
                    text = stringResource(firstActionLabel),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            },
            onClick = onDatesSelect,
        )
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Label,
                    contentDescription = stringResource(secondActionLabel),
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            text = {
                Text(
                    text = stringResource(secondActionLabel),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            },
            onClick = onLabelAssign,
        )
    }
}

@Preview
@Composable
fun CalendarExportTopBarDropDownMenuPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CalendarExportTopBarDropDownMenu(
            isVisible = true,
            hasDatesSelected = false,
            hasLabelAssigned = false,
            onDatesSelect = {},
            onLabelAssign = {},
            onDismiss = {},
            modifier = modifier
        )
    }
}
