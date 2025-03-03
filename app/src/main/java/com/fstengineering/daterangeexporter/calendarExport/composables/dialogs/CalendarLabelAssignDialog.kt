package com.fstengineering.daterangeexporter.calendarExport.composables.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fstengineering.daterangeexporter.R
import com.fstengineering.daterangeexporter.core.application.theme.AppTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CalendarLabelAssignDialog(
    label: String?,
    onLabelChanged: () -> Unit,
    labelError: Int?,
    onSave: (String?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fieldFocusRequester = remember { FocusRequester() }

    var labelInput by rememberSaveable(label) { mutableStateOf(label) }

    val titleResId = if (!label.isNullOrBlank()) {
        R.string.calendar_label_assign_dialog_title_rename
    } else R.string.calendar_label_assign_dialog_title_assign

    LaunchedEffect(Unit) {
        delay(250.milliseconds)
        fieldFocusRequester.requestFocus()
    }

    AlertDialog(
        title = {
            Text(
                text = stringResource(titleResId),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            OutlinedTextField(
                value = labelInput ?: "",
                onValueChange = { input ->
                    labelInput = input
                    onLabelChanged()
                },
                isError = labelError != null,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.calendar_label_assign_dialog_field_placeholder),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Label,
                        contentDescription = stringResource(titleResId),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                supportingText = {
                    if (labelError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(labelError),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = { onSave(label) }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp, bottom = 4.dp)
                    .focusRequester(fieldFocusRequester)
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(labelInput) },
            ) {
                Text(
                    text = stringResource(id = R.string.calendar_label_assign_dialog_primary_button),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onCancel() },
            ) {
                Text(
                    text = stringResource(id = R.string.calendar_label_assign_dialog_secondary_button),
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

@Preview
@Composable
fun CalendarLabelAssignDialogPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CalendarLabelAssignDialog(
            label = "Franco Saravia Tavares",
            labelError = R.string.inline_calendar_label_is_blank,
            onLabelChanged = {},
            onSave = {},
            onCancel = {},
            modifier = modifier,
        )
    }
}
