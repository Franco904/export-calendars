package com.example.daterangeexporter.calendarExport.localComposables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.core.theme.AppTheme

@Composable
fun CalendarDropDownMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
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
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Exportar calendário",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            text = {
                Text(
                    text = "Compartilhar",
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
            onExportCalendar = {},
            modifier = modifier
        )
    }
}
