package com.example.daterangeexporter.calendars

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.composables.DropdownField
import com.example.daterangeexporter.core.constants.DEFAULT_SELECTED_YEAR
import com.example.daterangeexporter.core.constants.SELECTED_YEAR
import com.example.daterangeexporter.core.infra.dataStore
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun CalendarsScreen(
    modifier: Modifier = Modifier,
    onCalendarSelect: (Int, Int) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()

    var selectedYear by rememberSaveable { mutableIntStateOf(DEFAULT_SELECTED_YEAR) }

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        context.dataStore.data.first()[SELECTED_YEAR]?.let { storedSelectedYear ->
            selectedYear = storedSelectedYear
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            CalendarsTopBar()
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { contentPadding ->
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = contentPadding.calculateTopPadding())
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .width(64.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = contentPadding.calculateTopPadding())
                    .padding(horizontal = 16.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { focusManager.clearFocus() }
                    }
            ) {
                calendarsContent(
                    selectedYear = selectedYear,
                    onYearSelect = { year ->
                        selectedYear = year

                        coroutineScope.launch {
                            context.dataStore.edit { prefs -> prefs[SELECTED_YEAR] = year.toInt() }
                        }

                        focusManager.clearFocus()
                    },
                    onCalendarSelect = onCalendarSelect,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarsTopBar(
    modifier: Modifier = Modifier,
) {
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    TopAppBar(
        title = {
            Text(
                text = "Calendários",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
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

fun LazyListScope.calendarsContent(
    selectedYear: Int,
    onYearSelect: (Int) -> Unit,
    onCalendarSelect: (Int, Int) -> Unit,
) {
    item {
        Spacer(modifier = Modifier.height(16.dp))
        DropdownField(
            placeholderText = "Ano",
            items = CalendarUtils.getNextYears().map { it.toString() }.toPersistentList(),
            onItemSelect = { year -> onYearSelect(year.toInt()) },
            defaultItem = selectedYear.toString(),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (selectedYear == CalendarUtils.getCurrentYear()) {
        val currentMonth = CalendarUtils.getCurrentMonth()

        items(12 - currentMonth) { i ->
            val month = i + currentMonth + 1

            if (i == 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            BaseCalendar(
                month = month,
                year = selectedYear,
                showYearLabel = false,
                onCardSelect = {
                    onCalendarSelect(month, selectedYear)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    } else {
        items(12) { i ->
            val month = i + 1

            if (i == 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            BaseCalendar(
                month = month,
                year = selectedYear,
                showYearLabel = false,
                onCardSelect = {
                    onCalendarSelect(month, selectedYear)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun CalendarsScreenPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CalendarsScreen(
            modifier = modifier
        )
    }
}
