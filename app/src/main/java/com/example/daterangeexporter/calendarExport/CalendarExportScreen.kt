package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.composables.DateRangePickerDialog
import com.example.daterangeexporter.core.infra.InternalStorageHandler.deleteAllFiles
import com.example.daterangeexporter.core.infra.InternalStorageHandler.saveImage
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
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
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    var mustShowDateRangePickerDialog by remember { mutableStateOf(false) }

    val initialCalendar = CalendarMonthYear(
        id = selectedMonth + selectedYear,
        month = selectedMonth,
        year = selectedYear,
    )
    val emptySelectedDates: Map<CalendarMonthYear, ImmutableList<String>> =
        mapOf(initialCalendar to persistentListOf())

    var selectedDates by rememberSaveable { mutableStateOf(emptySelectedDates) }

    Scaffold(
        topBar = {
            CalendarExportTopBar(
                onUpNavigation = onUpNavigation,
                onEditCalendar = { mustShowDateRangePickerDialog = true },
                onClearSelectedDates = { selectedDates = emptySelectedDates },
                isSelectedDatesEmpty = selectedDates.entries.first().value.isEmpty(),
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
                        initialCalendar = initialCalendar,
                        onDateRangeSelected = { (startDateTimeMillis, endDateTimeMillis) ->
                            mustShowDateRangePickerDialog = false

                            if (startDateTimeMillis == null || endDateTimeMillis == null) {
                                return@DateRangePickerDialog
                            }

                            selectedDates = CalendarUtils.getDatesGroupedByMonthAndYear(
                                startDateTimeMillis = startDateTimeMillis,
                                endDateTimeMillis = endDateTimeMillis,
                            )
                        },
                        onDismiss = {
                            mustShowDateRangePickerDialog = false
                        },
                        isDateSelectionEmpty = selectedDates == emptySelectedDates,
                    )
                }
            }
            itemsIndexed(
                selectedDates,
                key = { i, _ -> i },
            ) { i, (calendarMonthYear, dates) ->
                val onBeforeCalendarSelect: suspend () -> Unit = {
                    lazyListState.animateScrollToItem(i)
                    delay(250.milliseconds)
                }

                val onExportCalendar: (ImageBitmap) -> Unit = { imageBitmap: ImageBitmap ->
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
                    hasDropDownMenu = true,
                    selectedDates = dates,
                    onBeforeExportCalendar = onBeforeCalendarSelect,
                    onExportCalendar = onExportCalendar,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
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
