package com.example.daterangeexporter.calendarExport.localModels

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.daterangeexporter.core.theme.AppCustomColors

enum class RangeSelectionCount(
    val id: Int,
    val color: @Composable () -> Color,
) {
    NONE(id = 0, color = { Color.Transparent }),
    FIRST(id = 1, color = { MaterialTheme.colorScheme.primaryContainer }),
    SECOND(id = 2, color = { AppCustomColors.purple200 }),
    THIRD(id = 3, color = { AppCustomColors.orange400 });

    companion object {
        fun fromId(id: Int): RangeSelectionCount {
            return entries.find { it.id == id } ?: NONE
        }

        fun isFirstOrLast(id: Int): Boolean {
            return id in listOf(FIRST.id, entries.last().id)
        }
    }
}
