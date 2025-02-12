package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.daterangeexporter.calendarExport.composables.BaseCalendar
import com.example.daterangeexporter.calendarExport.composables.CalendarExportTopBar
import com.example.daterangeexporter.calendarExport.composables.CalendarLabelAssignDialog
import com.example.daterangeexporter.calendarExport.composables.DateRangePickerDialog
import com.example.daterangeexporter.calendarExport.composables.NoSelectedDatesEmptySection
import com.example.daterangeexporter.calendarExport.composables.PrimaryActionRow
import com.example.daterangeexporter.calendarExport.composables.SecondaryActionsRow
import com.example.daterangeexporter.calendarExport.models.RangeSelectionLabel
import com.example.daterangeexporter.core.application.theme.AppTheme
import com.example.daterangeexporter.core.presentation.utils.IMAGE_PNG_TYPE
import com.example.daterangeexporter.core.presentation.utils.itemsIndexed
import com.example.daterangeexporter.core.presentation.utils.showShareSheet
import org.koin.androidx.compose.koinViewModel

private const val FIXED_VISIBLE_LIST_ITEMS = 4

@Composable
fun CalendarExportScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarExportViewModel = koinViewModel(),
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current

    val lazyListState = rememberLazyListState()

    val rangeSelectionLabel by viewModel.rangeSelectionLabel.collectAsStateWithLifecycle()
    val selectedDates by viewModel.selectedDates.collectAsStateWithLifecycle()
    val isDateSelectionEmpty =
        selectedDates.isEmpty() || rangeSelectionLabel >= RangeSelectionLabel.Second.count

    val calendarLabelInput by viewModel.calendarLabelInput.collectAsStateWithLifecycle()

    val isConvertingToBitmap by viewModel.isConvertingToBitmap.collectAsStateWithLifecycle()
    val visibleItems by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.map { it.index - FIXED_VISIBLE_LIST_ITEMS }
        }
    }

    var mustShowDateRangePickerDialog by remember { mutableStateOf(false) }
    var mustShowLabelAssignDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is CalendarExportViewModel.UiEvents.DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is CalendarExportViewModel.UiEvents.SaveCalendarsBitmapsSuccess -> {
                    context.exportCalendars(
                        contentUris = uiEvent.calendarsContentUris,
                    )
                }

                is CalendarExportViewModel.UiEvents.MissingCalendarBitmap -> {
                    lazyListState.animateScrollToItem(
                        index = uiEvent.firstMissingBitmapIndex + FIXED_VISIBLE_LIST_ITEMS,
                    )
                }
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
                    onDateRangeSelect = { mustShowDateRangePickerDialog = true },
                    onClearSelection = viewModel::onClearDateRangeSelection,
                )
            }

            item {
                AnimatedVisibility(
                    visible = selectedDates.isNotEmpty(),
                    enter = fadeIn(),
                ) {
                    SecondaryActionsRow(
                        onAddNewDateRange = { mustShowDateRangePickerDialog = true },
                        hasLabelAssigned = !calendarLabelInput.isNullOrBlank(),
                        onLabelAssign = { mustShowLabelAssignDialog = true },
                        onExportCalendars = viewModel::onStartCalendarsExport,
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
                    NoSelectedDatesEmptySection()
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
                        viewModel.onConvertedCalendarToBitmap(
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
                    ?: viewModel.currentDayOfMonth.toString(),
                initialMonthYear = if (rangeSelectionLabel == RangeSelectionLabel.First.count) {
                    viewModel.initialCalendar
                } else {
                    viewModel.initialCalendar.copy(
                        month = selectedDates.keys.last().month,
                        year = selectedDates.keys.last().year,
                    )
                },
                onDateRangeSelected = { (startDateTimeMillis, endDateTimeMillis) ->
                    if (startDateTimeMillis == null || endDateTimeMillis == null) {
                        return@DateRangePickerDialog
                    }

                    viewModel.onDateRangeSelected(
                        startDateTimeMillis = startDateTimeMillis,
                        endDateTimeMillis = endDateTimeMillis,
                    )

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
                onSave = { label ->
                    viewModel.onCalendarLabelAssign(label = label ?: "")
                    mustShowLabelAssignDialog = false
                },
                onCancel = {
                    mustShowLabelAssignDialog = false
                },
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
