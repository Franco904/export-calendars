package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.infra.InternalStorageHandler.deleteAllFiles
import com.example.daterangeexporter.core.infra.InternalStorageHandler.saveImage
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
import com.example.daterangeexporter.core.utils.IMAGE_PNG_TYPE
import com.example.daterangeexporter.core.utils.itemsIndexed
import com.example.daterangeexporter.core.utils.showShareSheet
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    var mustShowDateRangePickerDialog by remember { mutableStateOf(false) }

    val initialCalendar = CalendarMonthYear(
        id = selectedMonth + selectedYear,
        month = selectedMonth,
        year = selectedYear,
    )
    var selectedDates by rememberSaveable {
        mutableStateOf<Map<CalendarMonthYear, List<String>>>(mapOf(initialCalendar to emptyList()))
    }

    Scaffold(
        topBar = {
            CalendarExportTopBar(
                onUpNavigation = onUpNavigation,
                onEditCalendar = { mustShowDateRangePickerDialog = true },
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
                    DateRangePickerModal(
                        initialCalendar = initialCalendar,
                        onDateRangeSelected = { (startDateTimeMillis, endDateTimeMillis) ->
                            mustShowDateRangePickerDialog = false

                            if (startDateTimeMillis == null || endDateTimeMillis == null) {
                                return@DateRangePickerModal
                            }

                            selectedDates = CalendarUtils.getDatesGroupedByMonthAndYear(
                                startDateTimeMillis = startDateTimeMillis,
                                endDateTimeMillis = endDateTimeMillis,
                            )
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
                    lazyListState.scrollBy(4f)
                    delay(250.milliseconds)
                }

                val onCalendarSelect: (ImageBitmap) -> Unit = { imageBitmap: ImageBitmap ->
                    coroutineScope.launch {
                        // Export to other apps in the device
                        context.exportCalendarImage(imageBitmap)
                    }
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
    onUpNavigation: () -> Boolean,
    onEditCalendar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    TopAppBar(
        title = {
            Text(
                text = "Calendário",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = { onUpNavigation() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        actions = {
            IconButton(onClick = onEditCalendar) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar calendário",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        modifier = modifier
            .drawBehind {
                drawLine(
                    color = outlineVariantColor,
                    start = Offset(x = 0f, y = size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx(),
                )
            }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    initialCalendar: CalendarMonthYear,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialDisplayedMonthMillis = CalendarUtils.getMonthTimestamp(
            month = initialCalendar.month,
            year = initialCalendar.year,
        )
    )

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
                    modifier = Modifier
                        .padding(16.dp)
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                    selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                    displayMode = dateRangePickerState.displayMode,
                    dateFormatter = DatePickerDefaults.dateFormatter(),
                    modifier = Modifier
                        .padding(4.dp)
                        .scale(0.9f)
                )
            },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxWidth()
        )
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
