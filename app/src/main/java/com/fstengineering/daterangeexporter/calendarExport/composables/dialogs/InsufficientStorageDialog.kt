package com.fstengineering.daterangeexporter.calendarExport.composables.dialogs

import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.daterangeexporter.R

@Composable
fun InsufficientStorageDialog(
    freeSpaceLeft: Long,
    totalSpace: Long,
    onFreeSpace: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val freeSpaceText = remember(freeSpaceLeft, totalSpace) {
        val freeSpacePercent = (freeSpaceLeft.toDouble() / totalSpace.toDouble() * 100).toInt()
        "$freeSpacePercent%"
    }

    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.dialog_internal_storage_io_error_title),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.dialog_internal_storage_io_error_message_with_remaining_space,
                    freeSpaceText,
                ),
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        confirmButton = {
            TextButton(onClick = onFreeSpace) {
                Text(
                    text = stringResource(id = R.string.dialog_internal_storage_io_error_primary_action),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(id = R.string.dialog_internal_storage_io_error_secondary_action),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        onDismissRequest = {},
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .width(450.dp)
    )
}
