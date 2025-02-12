package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.R
import com.example.daterangeexporter.calendarExport.composables.BaseCalendar
import com.example.daterangeexporter.calendarExport.composables.CalendarExportTopBar
import com.example.daterangeexporter.calendarExport.composables.CalendarLabelAssignDialog
import com.example.daterangeexporter.calendarExport.composables.DateRangePickerDialog
import com.example.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.models.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.models.RangeSelectionLabel
import com.example.daterangeexporter.calendarExport.utils.CalendarExportUtils
import com.example.daterangeexporter.core.application.theme.AppTheme
import com.example.daterangeexporter.core.presentation.composables.AppFilledButton
import com.example.daterangeexporter.core.presentation.composables.AppFilledTonalButton
import com.example.daterangeexporter.core.presentation.composables.AppOutlinedButton
import com.example.daterangeexporter.core.presentation.utils.IMAGE_PNG_TYPE
import com.example.daterangeexporter.core.presentation.utils.itemsIndexed
import com.example.daterangeexporter.core.presentation.utils.showShareSheet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

private const val FIXED_VISIBLE_LIST_ITEMS = 4

@Composable
fun CalendarExportScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarExportViewModel = koinViewModel(),
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val initialCalendar = remember {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        CalendarMonthYear(
            id = currentMonth + currentYear,
            month = currentMonth,
            year = currentYear,
        )
    }

    val currentDayOfMonth = remember { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) }

    val context = LocalContext.current

    val lazyListState = rememberLazyListState()

    var rangeSelectionLabel by remember { mutableIntStateOf(RangeSelectionLabel.First.count) }

    var selectedDates by remember {
        mutableStateOf<ImmutableMap<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>>(
            persistentMapOf()
        )
    }

    val isDateSelectionEmpty =
        selectedDates.isEmpty() || rangeSelectionLabel >= RangeSelectionLabel.Second.count

    var calendarLabelInput by remember { mutableStateOf<String?>(null) }

    var mustShowDateRangePickerDialog by remember { mutableStateOf(false) }
    var mustShowLabelAssignDialog by remember { mutableStateOf(false) }

    var isConvertingToBitmap by remember {
        mutableStateOf<ImmutableMap<CalendarMonthYear, Boolean>>(
            persistentMapOf()
        )
    }

    val visibleItems by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.map { it.index - FIXED_VISIBLE_LIST_ITEMS }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is CalendarExportViewModel.UiEvents.DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))

                    isConvertingToBitmap = persistentMapOf()
                }

                is CalendarExportViewModel.UiEvents.SaveCalendarsBitmapsSuccess -> {
                    context.exportCalendars(
                        contentUris = uiEvent.calendarsContentUris,
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.calendarBitmaps
            .collect { calendarBitmaps ->
                isConvertingToBitmap = isConvertingToBitmap
                    .filterValues { isConverting -> isConverting }
                    .toImmutableMap()

                if (isConvertingToBitmap.isNotEmpty()) {
                    val firstMissingCalendar =
                        selectedDates.keys.indexOfFirst { missingCalendarBitmap ->
                            missingCalendarBitmap == isConvertingToBitmap.keys.first()
                        }

                    delay(150.milliseconds)
                    lazyListState.animateScrollToItem(firstMissingCalendar + FIXED_VISIBLE_LIST_ITEMS)
                } else {
                    viewModel.saveCalendarsBitmaps(bitmaps = calendarBitmaps)
                }
            }
    }

    Scaffold(
        topBar = {
            CalendarExportTopBar()
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
            .fillMaxSize()
    ) { contentPadding ->
        LazyColumn(
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
                .padding(horizontal = 16.dp)
        ) {
            item {
                PrimaryActionRow(
                    selectedDates = selectedDates,
                    onDateRangeSelect = {
                        mustShowDateRangePickerDialog = true
                    },
                    onClearSelection = {
                        rangeSelectionLabel = RangeSelectionLabel.First.count
                        selectedDates = persistentMapOf()
                    },
                )
            }

            item {
                AnimatedVisibility(
                    visible = selectedDates.isNotEmpty(),
                    enter = fadeIn(),
                ) {
                    SecondaryActionsRow(
                        onAddNewDateRange = {
                            mustShowDateRangePickerDialog = true
                        },
                        hasLabelAssigned = !calendarLabelInput.isNullOrBlank(),
                        onLabelAssign = {
                            mustShowLabelAssignDialog = true
                        },
                        onExportCalendar = {
                            isConvertingToBitmap = selectedDates
                                .mapValues { true }
                                .toImmutableMap()
                        },
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                if (selectedDates.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(vertical = 32.dp, horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum período selecionado",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Comece clicando no botão \"Selecionar datas\".",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.outline,
                                fontStyle = FontStyle.Italic,
                            ),
                        )
                    }
                }
            }

            itemsIndexed(
                selectedDates,
                key = { i, _ -> i },
                contentType = { _, entryType -> entryType }
            ) { i, (calendarMonthYear, monthSelectedDates) ->
                val isConverting = remember(isConvertingToBitmap, visibleItems) {
                    isConvertingToBitmap[calendarMonthYear] ?: false && visibleItems
                        .find { it == i } != null
                }

                BaseCalendar(
                    month = calendarMonthYear.month,
                    year = calendarMonthYear.year,
                    clientNameLabel = calendarLabelInput,
                    selectedDatesWithMonthYear = Pair(calendarMonthYear, monthSelectedDates),
                    isConvertingToBitmap = isConverting,
                    onConvertedToBitmap = { bitmap: Bitmap ->
                        isConvertingToBitmap = isConvertingToBitmap
                            .mapValues { (monthYear, isCurrentConverting) ->
                                if (monthYear == calendarMonthYear) false else isCurrentConverting
                            }
                            .toImmutableMap()

                        viewModel.emitCalendarBitmap(
                            calendarMonthYear = calendarMonthYear,
                            bitmap = bitmap,
                        )
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (mustShowDateRangePickerDialog) {
            DateRangePickerDialog(
                initialDayOfMonth = selectedDates.values.lastOrNull()
                    ?.lastOrNull()?.dayOfMonth
                    ?: currentDayOfMonth.toString(),
                initialMonthYear = if (rangeSelectionLabel == RangeSelectionLabel.First.count) {
                    initialCalendar
                } else {
                    initialCalendar.copy(
                        month = selectedDates.keys.last().month,
                        year = selectedDates.keys.last().year,
                    )
                },
                onDateRangeSelected = { (startDateTimeMillis, endDateTimeMillis) ->
                    if (startDateTimeMillis == null || endDateTimeMillis == null) {
                        return@DateRangePickerDialog
                    }

                    selectedDates = CalendarExportUtils.getSelectedDates(
                        startDateTimeMillis = startDateTimeMillis,
                        endDateTimeMillis = endDateTimeMillis,
                        currentRangeCount = rangeSelectionLabel,
                        currentSelectedDates = selectedDates,
                    )

                    rangeSelectionLabel += 1
                    mustShowDateRangePickerDialog = false
                },
                onDismiss = {
                    mustShowDateRangePickerDialog = false
                },
                isDateSelectionEmpty = isDateSelectionEmpty,
            )
        }

        if (mustShowLabelAssignDialog) {
            CalendarLabelAssignDialog(
                input = calendarLabelInput,
                onSave = { input ->
                    calendarLabelInput = input
                    mustShowLabelAssignDialog = false
                },
                onCancel = {
                    mustShowLabelAssignDialog = false
                },
            )
        }
    }
}

@Composable
fun PrimaryActionRow(
    selectedDates: ImmutableMap<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>,
    onDateRangeSelect: () -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = selectedDates.isEmpty(),
        label = "PrimaryActionRow",
        modifier = modifier
    ) { isSelectedDatesEmpty ->
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            if (isSelectedDatesEmpty) {
                AppFilledButton(
                    icon = Icons.Default.DateRange,
                    text = stringResource(R.string.select_calendar_dates_action_text),
                    onClick = onDateRangeSelect,
                )
            } else {
                AppFilledTonalButton(
                    icon = Icons.Default.Close,
                    text = stringResource(R.string.clear_calendar_selected_dates_action_text),
                    onClick = onClearSelection,
                )
            }
        }
    }
}

@Composable
fun SecondaryActionsRow(
    onAddNewDateRange: () -> Unit,
    hasLabelAssigned: Boolean,
    onLabelAssign: () -> Unit,
    onExportCalendar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val secondActionLabel = if (hasLabelAssigned) {
        R.string.rename_calendar_action_text
    } else R.string.assign_calendar_label_action_text

    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
        ) {
            AppOutlinedButton(
                icon = Icons.Default.Add,
                text = stringResource(R.string.add_other_date_range_action_text),
                onClick = onAddNewDateRange,
            )
            Spacer(modifier = Modifier.width(4.dp))
            AppOutlinedButton(
                icon = Icons.AutoMirrored.Outlined.Label,
                text = stringResource(secondActionLabel),
                onClick = onLabelAssign,
            )
            Spacer(modifier = Modifier.width(4.dp))
            AppOutlinedButton(
                icon = Icons.Default.Share,
                text = stringResource(R.string.export_calendar_action_text),
                onClick = onExportCalendar,
            )
        }
    }
}

private fun Context.exportCalendars(
    contentUris: ArrayList<Uri>,
) {
    showShareSheet(action = Intent.ACTION_SEND_MULTIPLE) {
        type = IMAGE_PNG_TYPE
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris)
    }
}

@Preview
@Composable
fun CalendarExportScreenPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CalendarExportScreen(
            modifier = modifier
        )
    }
}
