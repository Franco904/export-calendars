package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daterangeexporter.calendarExport.localComposables.CalendarExportTopBar
import com.example.daterangeexporter.calendarExport.localComposables.CalendarLabelAssignDialog
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.localModels.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.localModels.RangeSelectionLabel
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.composables.DateRangePickerDialog
import com.example.daterangeexporter.core.infra.InternalStorageHandler.saveImage
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.IMAGE_PNG_TYPE
import com.example.daterangeexporter.core.utils.itemsIndexed
import com.example.daterangeexporter.core.utils.showShareSheet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

private const val FIXED_VISIBLE_LIST_ITEMS = 0

@Composable
fun CalendarExportScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarExportViewModel = viewModel(),
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
                    context.exportCalendars(calendarsBitmaps = calendarBitmaps)
                    isConvertingToBitmap = persistentMapOf()
                }
            }
    }

    Scaffold(
        topBar = {
            CalendarExportTopBar(
                onEditCalendar = {
                    mustShowDateRangePickerDialog = true
                },
                onClearSelectedDates = {
                    rangeSelectionLabel = RangeSelectionLabel.First.count
                    selectedDates = persistentMapOf()

                    viewModel.clearCalendarBitmaps()
                },
                onAddNewDateRange = {
                    mustShowDateRangePickerDialog = true
                },
                onLabelAssign = {
                    mustShowLabelAssignDialog = true
                },
                onExportCalendar = {
                    isConvertingToBitmap = selectedDates
                        .mapValues { true }
                        .toImmutableMap()
                },
                isSelectedDatesEmpty = selectedDates.isEmpty(),
                calendarHasLabelAssigned = !calendarLabelInput.isNullOrBlank(),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
            .fillMaxSize()
    ) { contentPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
                .padding(horizontal = 16.dp)
        ) {
            if (mustShowDateRangePickerDialog) {
                item {
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
            }

            if (mustShowLabelAssignDialog) {
                item {
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

            itemsIndexed(
                selectedDates,
                key = { i, _ -> i },
                contentType = { _, entryType -> entryType }
            ) { i, (calendarMonthYear, monthSelectedDates) ->
                val isConverting = remember(isConvertingToBitmap, visibleItems) {
                    isConvertingToBitmap[calendarMonthYear] ?: false && visibleItems
                        .find { it == i } != null
                }

                if (i == 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                BaseCalendar(
                    month = calendarMonthYear.month,
                    year = calendarMonthYear.year,
                    clientNameLabel = calendarLabelInput,
                    selectedDatesWithMonthYear = Pair(calendarMonthYear, monthSelectedDates),
                    isConvertingToBitmap = isConverting,
                    onConvertedToBitmap = { imageBitmap: ImageBitmap ->
                        isConvertingToBitmap = isConvertingToBitmap
                            .mapValues { (monthYear, isCurrentConverting) ->
                                if (monthYear == calendarMonthYear) false else isCurrentConverting
                            }
                            .toImmutableMap()

                        viewModel.emitCalendarBitmap(
                            calendarMonthYear = calendarMonthYear,
                            imageBitmap = imageBitmap,
                        )
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private suspend fun Context.exportCalendars(
    calendarsBitmaps: Map<CalendarMonthYear, ImageBitmap>,
) {
    val contentUris = arrayListOf<Uri>()

    calendarsBitmaps.forEach { (calendarMonthYear, calendarBitmap) ->
        val currentTimestamp = Calendar.getInstance().timeInMillis
        val monthYearString = "${calendarMonthYear.month}${calendarMonthYear.year}"

        val file = saveImage(
            bitmap = calendarBitmap.asAndroidBitmap(),
            fileName = "calendar-$monthYearString-$currentTimestamp.png",
            folder = cacheDir,
        )

        val contentUri = FileProvider.getUriForFile(
            /* context = */ this,
            /* authority = */ "${packageName}.fileprovider",
            file,
        )

        contentUris.add(contentUri)
    }

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
