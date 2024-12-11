package com.example.daterangeexporter.core.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.daterangeexporter.R
import com.example.daterangeexporter.calendarExport.localComposables.CalendarDropDownMenu
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
import com.example.daterangeexporter.core.utils.CalendarUtils.getMonthLabelByNumber
import kotlinx.coroutines.launch

private val daysOfWeek = listOf(
    R.string.sunday_label,
    R.string.monday_label,
    R.string.tuesday_label,
    R.string.wednesday_label,
    R.string.thursday_label,
    R.string.friday_label,
    R.string.saturday_label,
)

@Composable
fun BaseCalendar(
    month: Int,
    year: Int,
    modifier: Modifier = Modifier,
    showYearLabel: Boolean = true,
    selectedDates: List<String> = emptyList(),
    hasTheStartDate: Boolean = false,
    hasTheEndDate: Boolean = false,
    hasDropDownMenu: Boolean = false,
    onCardSelect: () -> Unit = {},
    onBeforeExportCalendar: suspend () -> Unit = {},
    onExportCalendar: (ImageBitmap) -> Unit = {},
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    val monthLabel = context.getMonthLabelByNumber(monthNumber = month)
    val numberOfDaysOfMonth = CalendarUtils.getNumberOfDaysOfMonth(month, year)
    val firstDayOfWeek = CalendarUtils.getFirstDayOfWeekOfMonth(month, year)

    val daysOfWeekLabels by remember { mutableStateOf(daysOfWeek.map { context.getString(it) }) }
    val days = List(numberOfDaysOfMonth) { day -> (day + 1).toString() }

    var isDropDownVisible by remember { mutableStateOf(false) }

    var pointerOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
    var cardHeight by remember { mutableStateOf(0.dp) }

    CalendarCard(
        onSelect = { offset ->
            pointerOffset = DpOffset(
                x = offset.x,
                y = offset.y - cardHeight,
            )

            onCardSelect()

            if (hasDropDownMenu && !isDropDownVisible) {
                isDropDownVisible = true
            }
        },
        modifier = modifier
            .drawWithContent {
                graphicsLayer.record { this@drawWithContent.drawContent() }
                drawLayer(graphicsLayer)
            }
            .onGloballyPositioned {
                cardHeight = with(density) { it.size.height.toDp() }
            }
    ) {
        Column(
            modifier = modifier
                .padding(
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp,
                )
        ) {
            MonthLabelSection(
                monthLabel = monthLabel,
                year = if (showYearLabel) year else null,
            )
            Spacer(modifier = Modifier.height(20.dp))
            DatesSection(
                daysOfWeekLabels = daysOfWeekLabels,
                firstDayOfWeek = firstDayOfWeek,
                days = days,
                selectedDates = selectedDates,
                hasTheStartDate = hasTheStartDate,
                hasTheEndDate = hasTheEndDate,
            )
        }
        CalendarDropDownMenu(
            isVisible = isDropDownVisible,
            offset = pointerOffset,
            onDismiss = { isDropDownVisible = false },
            onExportCalendar = {
                coroutineScope.launch {
                    onBeforeExportCalendar()

                    // Save a screenshot of the selected calendar composable
                    val imageBitmap = graphicsLayer.toImageBitmap()

                    onExportCalendar(imageBitmap)
                }

                isDropDownVisible = false
            },
            modifier = Modifier
        )
    }
}

@Composable
fun CalendarCard(
    onSelect: (DpOffset) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit),
) {
    val interactionSource = remember { MutableInteractionSource() }

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
            .clip(RoundedCornerShape(8.dp))
            .indication(interactionSource, ripple())
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val pressInteraction = PressInteraction.Press(offset)

                        interactionSource.emit(pressInteraction)
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(pressInteraction))
                    },
                    onTap = { offset ->
                        val dpOffset = DpOffset(
                            offset.x.toDp(),
                            offset.y.toDp(),
                        )

                        onSelect(dpOffset)
                    }
                )
            }
    ) {
        Column {
            content()
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

        items(days) { day ->
            val isSelected = day in selectedDates
            val calendarDayModifier =
                if (day.length == 1) Modifier.offset(x = (-0.5).dp) else Modifier

            val paddingBottom = when {
                day in days.takeLast(7) -> 0.dp
                isSelected -> 16.dp
                else -> 24.dp
            }

            CalendarDate(
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
fun CalendarDate(
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
        AnimatedVisibility(isSelected) {
            SelectedDateCircle(
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
fun SelectedDateCircle(
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
            hasTheStartDate = true,
            hasTheEndDate = true,
            selectedDates = listOf("27", "28", "29", "30", "31"),
            modifier = modifier,
        )
    }
}
