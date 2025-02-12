package com.example.daterangeexporter.calendarExport.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.R
import com.example.daterangeexporter.core.presentation.composables.AppOutlinedButton

@Composable
fun SecondaryActionsRow(
    onAddNewDateRange: () -> Unit,
    hasLabelAssigned: Boolean,
    onLabelAssign: () -> Unit,
    onExportCalendar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val secondActionLabel = if (hasLabelAssigned) {
        R.string.rename_calendar_action_text
    } else R.string.assign_calendar_label_action_text

    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
        ) {
            AppOutlinedButton(
                icon = Icons.Default.Add,
                text = stringResource(R.string.add_other_date_range_action_text),
                onClick = onAddNewDateRange,
            )
            Spacer(modifier = Modifier.width(4.dp))
            AppOutlinedButton(
                icon = Icons.AutoMirrored.Outlined.Label,
                text = stringResource(secondActionLabel),
                onClick = onLabelAssign,
            )
            Spacer(modifier = Modifier.width(4.dp))
            AppOutlinedButton(
                icon = Icons.Default.Share,
                text = stringResource(R.string.export_calendar_action_text),
                onClick = onExportCalendar,
            )
        }
    }
}
