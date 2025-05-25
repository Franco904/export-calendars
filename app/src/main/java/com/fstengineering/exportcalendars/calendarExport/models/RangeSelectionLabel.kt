package com.fstengineering.exportcalendars.calendarExport.models

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.fstengineering.exportcalendars.core.application.theme.AppCustomColors

enum class RangeSelectionLabel(
    val count: Int,
    val color: @Composable () -> Color,
) {
    None(count = 0, color = { MaterialTheme.colorScheme.background }),
    First(count = 1, color = { MaterialTheme.colorScheme.primaryContainer }),
    Second(count = 2, color = { MaterialTheme.colorScheme.primaryContainer }),
    Third(count = 3, color = { AppCustomColors.blue400 });

    fun isMax() = this == Third

    companion object {
        fun fromCount(count: Int): RangeSelectionLabel {
            return entries.find { it.count == count } ?: None
        }
    }
}
