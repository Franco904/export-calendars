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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.daterangeexporter.calendarExport.localComposables.CalendarDropDownMenu
import com.example.daterangeexporter.calendarExport.localComposables.ClientNameLabelChip
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.localModels.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.localModels.RangeSelectionLabel
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
import com.example.daterangeexporter.core.utils.CalendarUtils.getMonthLabelByNumber
import com.example.daterangeexporter.core.utils.snapshotStateListSaver
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch


@Composable
fun BaseCalendar(
    month: Int,
    year: Int,
    modifier: Modifier = Modifier,
    showYearLabel: Boolean = true,
    clientNameLabel: String? = null,
    selectedDatesWithMonthYear: Pair<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>? = null,
    hasDropDownMenu: Boolean = false,
    mustShowAddNewDateRangeMenuOption: Boolean = false,
    onCardSelect: () -> Unit = {},
    onAddNewDateRange: () -> Unit = {},
    onBeforeExportCalendar: suspend () -> Unit = {},
    onExportCalendar: (ImageBitmap) -> Unit = {},
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    val monthLabel by rememberSaveable { mutableStateOf(context.getMonthLabelByNumber(monthNumber = month)) }
    val numberOfDaysOfMonth by rememberSaveable {
        mutableIntStateOf(
            CalendarUtils.getNumberOfDaysOfMonth(month, year)
        )
    }
    val firstDayOfWeek by rememberSaveable {
        mutableIntStateOf(
            CalendarUtils.getFirstDayOfWeekOfMonth(month, year)
        )
    }

    val daysOfWeekLabels = rememberSaveable(saver = snapshotStateListSaver()) {
        CalendarUtils.daysOfWeek.map { context.getString(it) }.toMutableStateList()
    }
    val daysOfMonth = rememberSaveable(saver = snapshotStateListSaver()) {
        List(numberOfDaysOfMonth) { day -> (day + 1).toString() }.toMutableStateList()
    }

    var assignedClientNameLabel by rememberSaveable { mutableStateOf(clientNameLabel) }

    var isMenuDropDownVisible by remember { mutableStateOf(false) }

    var pointerOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
    var cardHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(clientNameLabel) {
        assignedClientNameLabel = clientNameLabel
    }

    CalendarCard(
        onSelect = { offset ->
            pointerOffset = DpOffset(
                x = offset.x,
                y = offset.y - cardHeight,
            )

            onCardSelect()

            if (hasDropDownMenu && !isMenuDropDownVisible) {
                isMenuDropDownVisible = true
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
                    top = if (!assignedClientNameLabel.isNullOrBlank()) 8.dp else 16.dp,
                    end = 16.dp,
                    bottom = 24.dp,
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MonthLabelSection(
                    monthLabel = monthLabel,
                    year = if (showYearLabel) year else null,
                    modifier = Modifier
                        .weight(1f)
                )
                AnimatedVisibility(!assignedClientNameLabel.isNullOrBlank()) {
                    ClientNameLabelChip(label = assignedClientNameLabel ?: "")
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
        CalendarDropDownMenu(
            isVisible = isMenuDropDownVisible,
            offset = pointerOffset,
            onDismiss = { isMenuDropDownVisible = false },
            mustShowAddNewDateRangeOption = mustShowAddNewDateRangeMenuOption,
            onAddNewDateRange = {
                onAddNewDateRange()

                isMenuDropDownVisible = false
            },
            onExportCalendar = {
                coroutineScope.launch {
                    onBeforeExportCalendar()

                    // Save a screenshot of the selected calendar composable
                    val imageBitmap = graphicsLayer.toImageBitmap()

                    onExportCalendar(imageBitmap)
                }

                isMenuDropDownVisible = false
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

        items(daysOfMonth) { day ->
            val calendarDayModifier =
                if (day.length == 1) Modifier.offset(x = (-0.5).dp) else Modifier

            val paddingBottom =
                if (day in daysOfMonth.takeLast(7)) 0.dp else 24.dp

            if (selectedDatesWithMonthYear == null) {
                CalendarDate(
                    dayText = day,
                    modifier = calendarDayModifier
                        .wrapContentSize()
                        .padding(bottom = paddingBottom)
                )
            } else {
                val selectedDates = selectedDatesWithMonthYear.second
                val selectedDate = selectedDates.find { it.dayOfMonth == day }

                CalendarDate(
                    dayText = day,
                    selectedDate = selectedDate,
                    modifier = calendarDayModifier
                        .wrapContentSize()
                        .padding(
                            bottom = if (selectedDate != null) 16.dp else paddingBottom
                        )
                        .offset(y = if (selectedDate != null) (-5.5).dp else 0.dp)
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
            clientNameLabel = "Franco Saravia Tavares",
            modifier = modifier,
        )
    }
}
