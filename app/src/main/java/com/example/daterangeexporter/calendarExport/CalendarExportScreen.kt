package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.example.daterangeexporter.calendarExport.localComposables.CalendarExportTopBar
import com.example.daterangeexporter.calendarExport.localComposables.CalendarLabelAssignDialog
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.calendarExport.localModels.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.localModels.RangeSelectionLabel
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.composables.DateRangePickerDialog
import com.example.daterangeexporter.core.infra.InternalStorageHandler.deleteAllFiles
import com.example.daterangeexporter.core.infra.InternalStorageHandler.saveImage
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.IMAGE_PNG_TYPE
import com.example.daterangeexporter.core.utils.itemsIndexed
import com.example.daterangeexporter.core.utils.showShareSheet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun CalendarExportScreen(
    selectedMonth: Int,
    selectedYear: Int,
    modifier: Modifier = Modifier,
    onUpNavigation: () -> Boolean = { true },
) {
    val lazyListState = rememberLazyListState()

    var rangeSelectionLabel by remember { mutableIntStateOf(RangeSelectionLabel.First.count) }

    val initialCalendar = CalendarMonthYear(
        id = selectedMonth + selectedYear,
        month = selectedMonth,
        year = selectedYear,
    )
    val emptySelectedDates: Map<CalendarMonthYear, ImmutableList<CalendarSelectedDate>> =
        mapOf(initialCalendar to persistentListOf())

    var selectedDates by remember { mutableStateOf(emptySelectedDates) }

    var calendarLabelInput by remember { mutableStateOf<String?>(null) }

    var mustShowDateRangePickerDialog by remember { mutableStateOf(false) }
    var mustShowLabelAssignDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CalendarExportTopBar(
                onUpNavigation = onUpNavigation,
                onEditCalendar = {
                    mustShowDateRangePickerDialog = true
                },
                onClearSelectedDates = {
                    rangeSelectionLabel = RangeSelectionLabel.First.count
                    selectedDates = emptySelectedDates
                },
                onLabelAssign = {
                    mustShowLabelAssignDialog = true
                },
                isSelectedDatesEmpty = selectedDates.entries.first().value.isEmpty(),
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
                    val isDateSelectionEmpty =
                        selectedDates == emptySelectedDates || rangeSelectionLabel >= RangeSelectionLabel.Second.count

                    DateRangePickerDialog(
                        initialDayOfMonth = selectedDates.values.last().lastOrNull()?.dayOfMonth
                            ?: "1",
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
            ) { i, (calendarMonthYear, monthSelectedDates) ->
                var mustShowAddNewDateRangeMenuOption by remember { mutableStateOf(false) }

                LaunchedEffect(selectedDates) {
                    val isLastMonth = calendarMonthYear == selectedDates.keys.last()
                    val hasAnyDateSelected = selectedDates.values.first().isNotEmpty()
                    val hasReachedMaxSelectionCount =
                        rangeSelectionLabel <= RangeSelectionLabel.entries.last().count

                    mustShowAddNewDateRangeMenuOption =
                        isLastMonth && hasAnyDateSelected && hasReachedMaxSelectionCount
                }

                SelectableDatesCalendar(
                    lazyListState = lazyListState,
                    index = i,
                    calendarMonthYear = calendarMonthYear,
                    selectedDatesWithMonthYear = Pair(calendarMonthYear, monthSelectedDates),
                    assignedClientNameLabel = calendarLabelInput,
                    mustShowAddNewDateRangeMenuOption = mustShowAddNewDateRangeMenuOption,
                    onAddNewDateRange = { mustShowDateRangePickerDialog = true }
                )
            }
        }
    }
}

@Composable
fun SelectableDatesCalendar(
    lazyListState: LazyListState,
    index: Int,
    calendarMonthYear: CalendarMonthYear,
    selectedDatesWithMonthYear: Pair<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>,
    assignedClientNameLabel: String?,
    mustShowAddNewDateRangeMenuOption: Boolean,
    onAddNewDateRange: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (index == 0) {
        Spacer(modifier = Modifier.height(16.dp))
    }
    BaseCalendar(
        month = calendarMonthYear.month,
        year = calendarMonthYear.year,
        clientNameLabel = assignedClientNameLabel,
        selectedDatesWithMonthYear = selectedDatesWithMonthYear,
        hasDropDownMenu = true,
        mustShowAddNewDateRangeMenuOption = mustShowAddNewDateRangeMenuOption,
        onAddNewDateRange = onAddNewDateRange,
        onBeforeExportCalendar = suspend {
            lazyListState.animateScrollToItem(index)
            delay(250.milliseconds)
        },
        onExportCalendar = { imageBitmap: ImageBitmap ->
            coroutineScope.launch {
                // Export to other apps in the device
                context.exportCalendarImage(imageBitmap)
            }
        },
    )
    Spacer(modifier = Modifier.height(16.dp))
}

private fun Context.exportCalendarImage(bitmap: ImageBitmap) {
    deleteAllFiles(on = { file -> file.name.startsWith("calendar-") })

    val currentTimestamp = Calendar.getInstance().timeInMillis
    val file = saveImage(bitmap.asAndroidBitmap(), fileName = "calendar-$currentTimestamp.png")

    val contentUri = FileProvider.getUriForFile(
        this, // context
        "${packageName}.fileprovider", // authority of the file provider
        file
    )

    showShareSheet(
        action = Intent.ACTION_SEND,
        intentData = contentUri,
        intentType = IMAGE_PNG_TYPE,
        intentFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION,
        extras = bundleOf(Intent.EXTRA_STREAM to contentUri),
    )
}

@Preview
@Composable
fun CalendarExportScreenPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CalendarExportScreen(
            selectedMonth = 1,
            selectedYear = 2025,
            modifier = modifier
        )
    }
}
