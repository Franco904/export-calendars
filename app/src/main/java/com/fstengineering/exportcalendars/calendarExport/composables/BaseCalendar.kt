package com.fstengineering.exportcalendars.calendarExport.composables

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.fstengineering.exportcalendars.calendarExport.models.CalendarMonthYear
import com.fstengineering.exportcalendars.calendarExport.models.CalendarSelectedDate
import com.fstengineering.exportcalendars.calendarExport.models.RangeSelectionLabel
import com.fstengineering.exportcalendars.core.application.theme.AppTheme
import com.fstengineering.exportcalendars.core.presentation.utils.CalendarUtils
import com.fstengineering.exportcalendars.core.presentation.utils.CalendarUtils.getMonthLabelByNumber
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun BaseCalendar(
    month: Int,
    year: Int,
    modifier: Modifier = Modifier,
    clientNameLabel: String? = null,
    selectedDatesWithMonthYear: Pair<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>? = null,
    isConvertingToBitmap: Boolean = false,
    onConvertedToBitmap: (Bitmap) -> Unit = {},
) {
    val context = LocalContext.current

    val graphicsLayer = rememberGraphicsLayer()

    val monthLabel = remember(month) { context.getMonthLabelByNumber(monthNumber = month) }
    val numberOfDaysOfMonth =
        remember(month, year) { CalendarUtils.getNumberOfDaysOfMonth(month, year) }
    val firstDayOfWeek =
        remember(month, year) { CalendarUtils.getFirstDayOfWeekOfMonth(month, year) }

    val daysOfWeekLabels = remember {
        CalendarUtils.daysOfWeek.map { context.getString(it) }.toImmutableList()
    }
    val daysOfMonth = remember(numberOfDaysOfMonth) {
        List(numberOfDaysOfMonth) { day -> (day + 1).toString() }.toImmutableList()
    }

    LaunchedEffect(isConvertingToBitmap) {
        if (isConvertingToBitmap) {
            try {
                onConvertedToBitmap(graphicsLayer.toImageBitmap().asAndroidBitmap())
            } catch (e: Exception) {
                Log.e("Calendar $month/$year", e.message.toString())
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .widthIn(max = 400.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.small,
            )
            .clip(RoundedCornerShape(8.dp))
            .drawWithCache {
                onDrawWithContent {
                    graphicsLayer.record { this@onDrawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                }
            }
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = if (!clientNameLabel.isNullOrBlank()) 8.dp else 16.dp,
                    end = 16.dp,
                    bottom = 24.dp,
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MonthLabelSection(
                    monthLabel = monthLabel,
                    year = year,
                    modifier = Modifier
                        .weight(1f)
                )
                AnimatedVisibility(!clientNameLabel.isNullOrBlank()) {
                    ClientNameLabelChip(label = clientNameLabel ?: "")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            DatesSection(
                daysOfWeekLabels = daysOfWeekLabels.toPersistentList(),
                firstDayOfWeek = firstDayOfWeek,
                daysOfMonth = daysOfMonth.toPersistentList(),
                selectedDatesWithMonthYear = selectedDatesWithMonthYear,
            )
        }
    }
}

@Composable
fun MonthLabelSection(
    monthLabel: String,
    year: Int?,
    modifier: Modifier = Modifier,
) {
    Text(
        if (year != null) "$monthLabel $year" else monthLabel,
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
fun DatesSection(
    daysOfWeekLabels: ImmutableList<String>,
    firstDayOfWeek: Int,
    daysOfMonth: ImmutableList<String>,
    selectedDatesWithMonthYear: Pair<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>?,
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
            CalendarDate(
                dayText = dayOfWeek,
                isWeekDayLabel = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 24.dp)
            )
        }

        items(firstDayOfWeek - 1) {
            CalendarDate(
                dayText = "N",
                mustHideText = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 24.dp)
            )
        }

        // TODO: Improve logic to add a isSelected attr to CalendarSelectedDate and use it here
        items(daysOfMonth) { day ->
            val selectedDaysOfMonth = selectedDatesWithMonthYear?.second?.map { it.dayOfMonth }

            if (selectedDatesWithMonthYear == null || day !in selectedDaysOfMonth!!) {
                val paddingBottom = if (day in daysOfMonth.takeLast(7)) 0.dp else 24.dp

                CalendarDate(
                    dayText = day,
                    modifier = Modifier
                        .padding(bottom = paddingBottom)
                        .offset(
                            x = if (day.length == 1) (-0.5).dp else 0.dp,
                        )
                )
            } else {
                val selectedDate = selectedDatesWithMonthYear.second.find { it.dayOfMonth == day }

                val paddingBottom = when {
                    day in daysOfMonth.takeLast(7) -> 0.dp
                    selectedDate != null -> 16.dp
                    else -> 24.dp
                }

                CalendarDate(
                    dayText = day,
                    selectedDate = selectedDate,
                    modifier = Modifier
                        .padding(bottom = paddingBottom)
                        .offset(
                            x = if (day.length == 1) (-0.5).dp else 0.dp,
                            y = if (selectedDate != null) (-5.5).dp else 0.dp,
                        )
                )
            }
        }
    }
}

@Composable
fun CalendarDate(
    dayText: String,
    modifier: Modifier = Modifier,
    mustHideText: Boolean = false,
    isWeekDayLabel: Boolean = false,
    selectedDate: CalendarSelectedDate? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AnimatedVisibility(selectedDate != null) {
            if (selectedDate == null) return@AnimatedVisibility

            SelectedDateCircle(
                selectionRange = selectedDate.rangeSelectionLabel,
                isRangeEnd = selectedDate.isRangeEnd,
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
fun SelectedDateCircle(
    modifier: Modifier = Modifier,
    selectionRange: Pair<RangeSelectionLabel, RangeSelectionLabel>,
    isRangeEnd: Boolean,
) {
    val (firstCircleHalfRange, secondCircleHalfRange) = selectionRange

    val firstCircleHalfColor = firstCircleHalfRange.color()
    val secondCircleHalfColor = secondCircleHalfRange.color()

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        SelectedDateSubCircle(
            size = 28.dp,
            firstCircleHalfColor = firstCircleHalfColor,
            secondCircleHalfColor = secondCircleHalfColor,
        )
        SelectedDateSubCircle(
            size = 22.dp,
            firstCircleHalfColor = if (firstCircleHalfRange == RangeSelectionLabel.First) {
                firstCircleHalfColor
            } else backgroundColor,
            secondCircleHalfColor = if (secondCircleHalfRange == RangeSelectionLabel.First) {
                secondCircleHalfColor
            } else backgroundColor,
        )
        if (firstCircleHalfRange.count > RangeSelectionLabel.First.count && isRangeEnd) {
            SelectedDateMiddleDivider(
                firstCircleHalfColor = firstCircleHalfColor,
            )
        }
    }
}

@Composable
fun SelectedDateSubCircle(
    size: Dp,
    firstCircleHalfColor: Color,
    secondCircleHalfColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // arc 0° angle starts at 3 o'clock
            drawArc(
                color = firstCircleHalfColor,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
                style = Fill,
            )
            drawArc(
                color = secondCircleHalfColor,
                startAngle = 270f,
                sweepAngle = 180f,
                useCenter = true,
                style = Fill,
            )
        }
    }
}

@Composable
fun SelectedDateMiddleDivider(
    firstCircleHalfColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(28.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawLine(
                color = firstCircleHalfColor,
                start = Offset(x = canvasWidth / 2, y = 0f), // Start from the top
                end = Offset(x = canvasWidth / 2, y = canvasHeight), // End at the bottom
                strokeWidth = 8f,
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
            selectedDatesWithMonthYear = Pair(
                CalendarMonthYear(-1, 1, 2025),
                persistentListOf(
//                    CalendarSelectedDate("1", rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First),
//                    CalendarSelectedDate("2", rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First),
//                    CalendarSelectedDate("3", rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First),
                    CalendarSelectedDate(
                        "4",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "5",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "6",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "7",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "8",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "9",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "10",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "11",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "12",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "13",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "14",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "15",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "16",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "17",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "18",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "19",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "20",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "21",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "22",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "23",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "24",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "25",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "26",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "27",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "28",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "29",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "30",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                    CalendarSelectedDate(
                        "31",
                        rangeSelectionLabel = RangeSelectionLabel.First to RangeSelectionLabel.First
                    ),
                ),
            ),
            clientNameLabel = "Franco",
            isConvertingToBitmap = false,
            onConvertedToBitmap = {},
            modifier = modifier,
        )
    }
}
