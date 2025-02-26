package com.fstengineering.daterangeexporter.calendarExport

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.storage.StorageManager
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.daterangeexporter.calendarExport.composables.BaseCalendar
import com.fstengineering.daterangeexporter.calendarExport.composables.CalendarExportTopBar
import com.fstengineering.daterangeexporter.calendarExport.composables.NoSelectedDatesEmptySection
import com.fstengineering.daterangeexporter.calendarExport.composables.PrimaryActionRow
import com.fstengineering.daterangeexporter.calendarExport.composables.SecondaryActionsRow
import com.fstengineering.daterangeexporter.calendarExport.composables.dialogs.CalendarLabelAssignDialog
import com.fstengineering.daterangeexporter.calendarExport.composables.dialogs.DateRangePickerDialog
import com.fstengineering.daterangeexporter.calendarExport.composables.dialogs.InsufficientStorageDialog
import com.fstengineering.daterangeexporter.calendarExport.models.RangeSelectionLabel
import com.fstengineering.daterangeexporter.core.application.theme.AppTheme
import com.fstengineering.daterangeexporter.core.domain.utils.DataSourceError
import com.fstengineering.daterangeexporter.core.presentation.utils.IMAGE_PNG_TYPE
import com.fstengineering.daterangeexporter.core.presentation.utils.itemsIndexed
import com.fstengineering.daterangeexporter.core.presentation.utils.showShareSheet
import com.fstengineering.daterangeexporter.core.presentation.utils.uiConverters.toUiMessage
import org.koin.androidx.compose.koinViewModel

private const val FIXED_VISIBLE_LIST_ITEMS = 4

@Composable
fun CalendarExportScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarExportViewModel = koinViewModel(),
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val storageStatsManager = remember { context.getSystemService<StorageStatsManager>() }

    val lazyListState = rememberLazyListState()

    val rangeSelectionCount by viewModel.rangeSelectionCount.collectAsStateWithLifecycle()
    val selectedDates by viewModel.selectedDates.collectAsStateWithLifecycle()
    val isDateSelectionEmpty =
        selectedDates.isEmpty() || rangeSelectionCount >= RangeSelectionLabel.Second.count

    val calendarFormUiState by viewModel.calendarFormUiState.collectAsStateWithLifecycle()

    val calendarsBitmaps by viewModel.calendarsBitmaps.collectAsStateWithLifecycle()
    val visibleItems by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.map { it.index - FIXED_VISIBLE_LIST_ITEMS }
        }
    }

    var mustShowDateRangePickerDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowLabelAssignDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowInsufficientStorageDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is CalendarExportViewModel.UiEvents.DataSourceErrorEvent -> {
                    val uiMessage = uiEvent.error.toUiMessage()

                    when (uiEvent.error) {
                        DataSourceError.InternalStorageError.IOError -> {
                            mustShowInsufficientStorageDialog = true
                        }

                        else -> showSnackbar(context.getString(uiMessage))
                    }
                }

                is CalendarExportViewModel.UiEvents.CalendarLabelAssigned -> {
                    mustShowLabelAssignDialog = false
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
                        hasLabelAssigned = !calendarFormUiState.label.isNullOrBlank(),
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
                val isConvertingToBitmap = remember(calendarsBitmaps, visibleItems) {
                    val isBitmapMissing = calendarsBitmaps[calendarMonthYear] == null
                    val isItemVisible = visibleItems.find { it == i } != null

                    calendarsBitmaps.isNotEmpty() && isBitmapMissing && isItemVisible
                }

                BaseCalendar(
                    month = calendarMonthYear.month,
                    year = calendarMonthYear.year,
                    clientNameLabel = calendarFormUiState.label,
                    selectedDatesWithMonthYear = Pair(calendarMonthYear, monthSelectedDates),
                    isConvertingToBitmap = isConvertingToBitmap,
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
                initialMonthYear = if (rangeSelectionCount == RangeSelectionLabel.First.count) {
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
                label = calendarFormUiState.label,
                onLabelChanged = viewModel::onCalendarLabelChange,
                labelError = calendarFormUiState.labelError,
                onSave = viewModel::onCalendarLabelAssign,
                onCancel = {
                    viewModel.onCalendarLabelInputCancel()
                    mustShowLabelAssignDialog = false
                },
            )
        }

        if (mustShowInsufficientStorageDialog) {
            InsufficientStorageDialog(
                context = context,
                freeSpaceLeft = storageStatsManager?.getFreeBytes(StorageManager.UUID_DEFAULT),
                totalSpace = storageStatsManager?.getTotalBytes(StorageManager.UUID_DEFAULT),
                onFreeSpace = {
                    val storageInsufficientIntent = Intent(Intent.ACTION_MANAGE_PACKAGE_STORAGE)
                    context.startActivity(storageInsufficientIntent)
                },
                onCancel = {
                    mustShowInsufficientStorageDialog = false
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
