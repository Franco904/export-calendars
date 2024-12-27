package com.example.daterangeexporter.calendarExport.localModels

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.daterangeexporter.core.theme.AppCustomColors

enum class RangeSelectionLabel(
    val count: Int,
    val color: @Composable () -> Color,
) {
    None(count = 0, color = { MaterialTheme.colorScheme.background }),
    First(count = 1, color = { MaterialTheme.colorScheme.primaryContainer }),
    Second(count = 2, color = { AppCustomColors.purple200 }),
    Third(count = 3, color = { AppCustomColors.orange400 });

    companion object {
        fun fromId(id: Int): RangeSelectionLabel {
            return entries.find { it.count == id } ?: None
        }
    }
}
