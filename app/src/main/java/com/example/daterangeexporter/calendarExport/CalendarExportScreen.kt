package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.composeModels.CalendarMonthYear
import com.example.daterangeexporter.core.infra.InternalStorageHandler.deleteAllFiles
import com.example.daterangeexporter.core.infra.InternalStorageHandler.saveImage
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.BaseCalendarState
import com.example.daterangeexporter.core.utils.CalendarUtils
import com.example.daterangeexporter.core.utils.ComposableToBitmapConverter.captureCalendarComposablesAsBitmap
import com.example.daterangeexporter.core.utils.IMAGE_PNG_TYPE
import com.example.daterangeexporter.core.utils.itemsIndexed
import com.example.daterangeexporter.core.utils.showShareSheet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun CalendarExportScreen(
    selectedMonth: Int,
    selectedYear: Int,
    modifier: Modifier = Modifier,
    onUpNavigation: () -> Boolean = { true },
) {
    val calendars = mutableListOf(BaseCalendarState(selectedMonth, selectedYear))

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var mustShowDateRangePickerDialog by remember { mutableStateOf(false) }

    val initialCalendar = CalendarMonthYear(
        id = selectedMonth + selectedYear,
        month = selectedMonth,
        year = selectedYear,
    )
    var selectedDates by remember {
        mutableStateOf<Map<CalendarMonthYear, List<String>>>(mapOf(initialCalendar to emptyList()))
    }

    var selectedCalendarsForExport by remember {
        mutableStateOf<PersistentList<CalendarMonthYear>>(persistentListOf())
    }

    var isSelectingForExport by remember { mutableStateOf(false) }

    var selectedCalendar by remember { mutableStateOf<CalendarMonthYear?>(null) }
    var selectedCalendarImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    Scaffold(
        topBar = {
            CalendarExportTopBar(
                isAnyCalendarSelected = selectedCalendar != null,
                onUpNavigation = onUpNavigation,
                onEditCalendar = { mustShowDateRangePickerDialog = true },
                onExportCalendar = {
                    if (selectedCalendarImageBitmap == null) return@CalendarExportTopBar

                    coroutineScope.launch {
                        context.exportCalendarImage(bitmap = selectedCalendarImageBitmap!!)
                    }
                },
                onCancelExport = {
                    isSelectingForExport = false
                    selectedCalendarsForExport = persistentListOf()
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
            .fillMaxSize()
    ) { contentPadding ->
        val lazyListState = rememberLazyListState()

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
                .padding(horizontal = 16.dp)
        ) {
            if (mustShowDateRangePickerDialog) {
                item {
                    DateRangePickerModal(
                        onDateRangeSelected = { (startDateTimeMillis, endDateTimeMillis) ->
                            mustShowDateRangePickerDialog = false

                            if (startDateTimeMillis == null || endDateTimeMillis == null) {
                                return@DateRangePickerModal
                            }

                            selectedDates = CalendarUtils.getDatesGroupedByMonthAndYear(
                                startDateTimeMillis = startDateTimeMillis,
                                endDateTimeMillis = endDateTimeMillis,
                            )

                            selectedDates.forEach { (calendarMonthYear, dates) ->
                                calendars.add(
                                    BaseCalendarState(
                                        month = calendarMonthYear.month,
                                        year = calendarMonthYear.year,
                                        hasTheStartDate = calendarMonthYear == selectedDates.keys.first(),
                                        hasTheEndDate = calendarMonthYear == selectedDates.keys.last(),
                                        selectedDates = dates,
                                    ),
                                )
                            }
                        },
                        onDismiss = {
                            mustShowDateRangePickerDialog = false
                        },
                    )
                }
            }
            itemsIndexed(
                selectedDates,
                key = { i, _ -> i },
            ) { i, (calendarMonthYear, dates) ->
                val onBeforeCalendarSelect: suspend () -> Unit = {
                    lazyListState.animateScrollToItem(i)
                    delay(100.milliseconds)
                }

                val onCalendarSelect = { imageBitmap: ImageBitmap ->
                    // Save screenshot of the selected calendar composable
                    selectedCalendarImageBitmap =
                        if (imageBitmap == selectedCalendarImageBitmap) null else imageBitmap
                    selectedCalendar =
                        if (imageBitmap == selectedCalendarImageBitmap) null else calendarMonthYear
                }

                if (i == 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                BaseCalendar(
                    month = calendarMonthYear.month,
                    year = calendarMonthYear.year,
                    hasTheStartDate = calendarMonthYear == selectedDates.keys.first(),
                    hasTheEndDate = calendarMonthYear == selectedDates.keys.last(),
                    selectedDates = dates,
                    isCardSelected = selectedCalendar == calendarMonthYear,
                    onBeforeCardSelect = onBeforeCalendarSelect,
                    onCardSelect = onCalendarSelect,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarExportTopBar(
    isAnyCalendarSelected: Boolean,
    onUpNavigation: () -> Boolean,
    onEditCalendar: () -> Unit,
    onExportCalendar: () -> Unit,
    onCancelExport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                if (isAnyCalendarSelected) "Compartilhar" else "Calendário",
                style = MaterialTheme.typography.titleLarge,
                color = if (isAnyCalendarSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        navigationIcon = {
            if (isAnyCalendarSelected) {
                IconButton(onClick = onCancelExport) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            } else {
                IconButton(onClick = { onUpNavigation() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        actions = {
            CalendarButtonsSection(
                isAnyCalendarSelected = isAnyCalendarSelected,
                onEditCalendar = onEditCalendar,
                onExportCalendar = onExportCalendar,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isAnyCalendarSelected) {
                MaterialTheme.colorScheme.primary
            } else MaterialTheme.colorScheme.background,
        ),
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        modifier = modifier
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Selecione o período da estadia",
                    Modifier
                        .padding(start = 16.dp)
                )
            },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
fun CalendarButtonsSection(
    isAnyCalendarSelected: Boolean,
    onEditCalendar: () -> Unit,
    onExportCalendar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        if (!isAnyCalendarSelected) {
            IconButton(onClick = onEditCalendar) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar calendário",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
        if (isAnyCalendarSelected) {
            IconButton(onClick = onExportCalendar) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Compartilhar calendário",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

private fun Context.exportCalendarImage(bitmap: ImageBitmap) {
    deleteAllFiles(on = { file -> file.name.startsWith("calendar-") })

    val currentTimestamp = Calendar.getInstance().timeInMillis
    val file = saveImage(bitmap.asAndroidBitmap(), fileName = "calendar-$currentTimestamp.png")

    val contentUri = FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
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
