package com.example.daterangeexporter.core.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils


@Composable
fun BaseCalendar(
    month: Int,
    year: Int,
    modifier: Modifier = Modifier,
    selectedDates: List<String> = emptyList(),
    hasTheStartDate: Boolean = false,
    hasTheEndDate: Boolean = false,
) {
    val monthLabel = CalendarUtils.getMonthLabelByNumber(monthNumber = month)
    val daysOfWeekLabels = listOf("D", "S", "T", "Q", "Q", "S", "S")

    val numberOfDaysOfMonth = CalendarUtils.getNumberOfDaysOfMonth(month, year)
    val firstDayOfWeek = CalendarUtils.getFirstDayOfWeekOfMonth(month, year)

    val days = List(numberOfDaysOfMonth) { day -> (day + 1).toString() }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.small,
            )
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp,
                )
        ) {
            MonthLabelSection(
                monthLabel = monthLabel,
                year = year,
            )
            Spacer(modifier = Modifier.height(20.dp))
            CalendarSection(
                daysOfWeekLabels = daysOfWeekLabels,
                firstDayOfWeek = firstDayOfWeek,
                days = days,
                selectedDates = selectedDates,
                hasTheStartDate = hasTheStartDate,
                hasTheEndDate = hasTheEndDate,
            )
        }
    }
}

@Composable
fun MonthLabelSection(
    monthLabel: String,
    year: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        "$monthLabel $year",
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 0.1.em,
            fontWeight = FontWeight.Bold,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .padding(start = 16.dp)
    )
}

@Composable
fun CalendarSection(
    daysOfWeekLabels: List<String>,
    firstDayOfWeek: Int,
    days: List<String>,
    selectedDates: List<String>,
    hasTheStartDate: Boolean,
    hasTheEndDate: Boolean,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(daysOfWeekLabels.size),
        modifier = modifier
            .padding(start = 13.dp)
            .wrapContentSize()
            .heightIn(max = 500.dp)
    ) {
        items(daysOfWeekLabels) { dayOfWeek ->
            CalendarDay(
                dayText = dayOfWeek,
                isWeekDayLabel = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 24.dp)
            )
        }

        items(firstDayOfWeek - 1) {
            CalendarDay(
                dayText = "N",
                mustHideText = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 24.dp)
            )
        }

        items(days) { day ->
            val isSelected = day in selectedDates
            val calendarDayModifier =
                if (day.length == 1) Modifier.offset(x = (-0.5).dp) else Modifier

            val paddingBottom = when {
                isSelected -> 16.dp
                day in days.takeLast(7) -> 0.dp
                else -> 24.dp
            }

            CalendarDay(
                dayText = day,
                isSelected = isSelected,
                isStartSelectedDay = hasTheStartDate && isSelected && day == selectedDates.firstOrNull(),
                isEndSelectedDay = hasTheEndDate && isSelected && day == selectedDates.lastOrNull(),
                modifier = calendarDayModifier
                    .wrapContentSize()
                    .padding(bottom = paddingBottom)
            )
        }
    }
}

@Composable
fun CalendarDay(
    dayText: String,
    modifier: Modifier = Modifier,
    mustHideText: Boolean = false,
    isWeekDayLabel: Boolean = false,
    isSelected: Boolean = false,
    isStartSelectedDay: Boolean = true,
    isEndSelectedDay: Boolean = false,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .offset(y = if (isSelected) (-5.5).dp else 0.dp)
    ) {
        if (isSelected) {
            SelectedCircle(
                isStartSelectedDay = isStartSelectedDay,
                isEndSelectedDay = isEndSelectedDay,
            )
        }
        Text(
            text = dayText,
            color = if (mustHideText) Color.Transparent else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isWeekDayLabel) FontWeight.W500 else null,
            ),
        )
    }
}

@Composable
fun SelectedCircle(
    modifier: Modifier = Modifier,
    isStartSelectedDay: Boolean,
    isEndSelectedDay: Boolean,
) {
    val selectedColor = MaterialTheme.colorScheme.primaryContainer

    Box(modifier = modifier.size(28.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = selectedColor,
                startAngle = when {
                    isStartSelectedDay -> 270f
                    isEndSelectedDay -> 90f
                    else -> 270f
                },
                sweepAngle = when {
                    isStartSelectedDay -> 180f
                    isEndSelectedDay -> 180f
                    else -> 360f
                },
                useCenter = true,
                style = Fill,
            )
        }
    }
}

@Preview
@Composable
fun BaseCalendarPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        BaseCalendar(
            month = 1,
            year = 2025,
            selectedDates = listOf("7", "8", "9", "10"),
            modifier = modifier,
        )
    }
}
