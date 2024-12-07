package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.composeModels.MonthYear
import com.example.daterangeexporter.core.infra.InternalStorageHandler.deleteAllFiles
import com.example.daterangeexporter.core.infra.InternalStorageHandler.saveImage
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
import com.example.daterangeexporter.core.utils.IMAGE_PNG_TYPE
import com.example.daterangeexporter.core.utils.items
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
    val graphicsLayer = rememberGraphicsLayer()

    var mustShowDateRangePickerDialog by remember { mutableStateOf(false) }

    var selectedDates by remember {
        mutableStateOf<Map<MonthYear, List<String>>>(
            mapOf(
                MonthYear(
                    id = selectedMonth + selectedYear,
                    month = selectedMonth,
                    year = selectedYear,
                ) to emptyList()
            )
        )
    }

    Scaffold(
        topBar = {
            CalendarExportTopBar { onUpNavigation() }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
            .fillMaxSize()
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
                .padding(horizontal = 16.dp)
                .drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                }
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
                        },
                        onDismiss = {
                            mustShowDateRangePickerDialog = false
                        },
                    )
                }
            }
            item {
                CalendarButtonsSection(
                    onEditCalendar = { mustShowDateRangePickerDialog = true },
                    onShareCalendar = {
                        coroutineScope.launch {
                            delay(100.milliseconds)

                            val bitmap = graphicsLayer.toImageBitmap()
                            context.shareCalendarImage(bitmap.asAndroidBitmap())
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(
                selectedDates,
                key = { it.key.id },
            ) { (monthYear, dates) ->
                val (_, month, year) = monthYear

                BaseCalendar(
                    month = month,
                    year = year,
                    hasTheStartDate = monthYear == selectedDates.keys.first(),
                    hasTheEndDate = monthYear == selectedDates.keys.last(),
                    selectedDates = dates,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarExportTopBar(
    modifier: Modifier = Modifier,
    onUpNavigation: () -> Boolean,
) {
    TopAppBar(
        title = {
            Text(
                "Calendário",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onUpNavigation().takeIf { wasSucceeded -> !wasSucceeded }?.run {
                    Log.e("CalendarExportScreen", "Navigation up failed!")
                }
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
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
    onEditCalendar: () -> Unit,
    onShareCalendar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        IconButton(onClick = onEditCalendar) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Editar calendário",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onShareCalendar) {
            Icon(
                Icons.Default.Share,
                contentDescription = "Compartilhar calendário",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

private fun Context.shareCalendarImage(bitmap: Bitmap) {
    val currentTimestamp = Calendar.getInstance().timeInMillis

    deleteAllFiles(on = { file -> file.name.startsWith("calendar-") })
    val file = saveImage(bitmap, fileName = "calendar-$currentTimestamp.png")

    val contentUri = FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        file
    )

    showShareSheet(
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
