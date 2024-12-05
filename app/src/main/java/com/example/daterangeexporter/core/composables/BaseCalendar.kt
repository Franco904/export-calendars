package com.example.daterangeexporter.core.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.daterangeexporter.core.getFirstDayOfWeekOfMonth
import com.example.daterangeexporter.core.getMonthLabelByNumber
import com.example.daterangeexporter.core.getNumberOfDaysOfMonth

@Composable
fun BaseCalendar(
    modifier: Modifier = Modifier,
    month: Int = 1,
    year: Int = 2025,
) {
    val monthLabel = getMonthLabelByNumber(monthNumber = month)
    val daysOfWeekLabels = listOf("D", "S", "T", "Q", "Q", "S", "S")

    val numberOfDaysOfMonth = getNumberOfDaysOfMonth(month, year)
    val firstDayOfWeek = getFirstDayOfWeekOfMonth(month, year)

    val days = List(numberOfDaysOfMonth) { day -> (day + 1).toString() }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEFEFE),
        ),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
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
            Text(
                "$monthLabel $year",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.1.em,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color(0xB3000000),
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(daysOfWeekLabels.size),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(start = 13.dp)
                    .wrapContentSize()
            ) {
                items(daysOfWeekLabels) { dayOfWeek ->
                    CalendarDay(
                        dayText = dayOfWeek,
                        isWeekDayLabel = true,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(bottom = 8.dp)
                    )
                }

                items(firstDayOfWeek - 1) {
                    CalendarDay("N", mustHideText = true, modifier = Modifier.wrapContentSize())
                }

                items(days) { day ->
                    val calendarDayModifier =
                        if (day.length == 1) Modifier.offset(x = (-0.5).dp) else Modifier
                    CalendarDay(day, modifier = calendarDayModifier.wrapContentSize())
                }
            }
        }
    }
}

@Composable
fun CalendarDay(
    dayText: String,
    modifier: Modifier = Modifier,
    mustHideText: Boolean = false,
    isWeekDayLabel: Boolean = false,
) {
    Text(
        text = dayText,
        color = if (mustHideText) Color.Transparent else Color(0xFF212121),
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = if (isWeekDayLabel) FontWeight.W500 else null,
        ),
        modifier = modifier
    )
}

@Preview
@Composable
fun BaseCalendarPreview(
    modifier: Modifier = Modifier,
) {
    BaseCalendar(modifier = modifier)
}
