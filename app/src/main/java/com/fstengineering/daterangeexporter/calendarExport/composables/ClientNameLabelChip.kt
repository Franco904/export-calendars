package com.fstengineering.daterangeexporter.calendarExport.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fstengineering.daterangeexporter.core.application.theme.AppTheme

@Composable
fun ClientNameLabelChip(
    label: String,
    modifier: Modifier = Modifier,
) {
    SuggestionChip(
        onClick = { },
        label = { Text(label.uppercase()) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.primary,
        ),
        border = null,
        modifier = modifier
    )
}

@Preview
@Composable
fun ClientNameLabelChipPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        ClientNameLabelChip(
            label = "Franco Saravia Tavares",
            modifier = modifier
        )
    }
}
