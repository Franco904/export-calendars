package com.example.daterangeexporter.calendars

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.composables.DropdownField
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
import kotlinx.collections.immutable.toPersistentList

@Composable
fun CalendarsScreen(
    modifier: Modifier = Modifier,
    onCalendarClick: (Int, Int) -> Unit = { _, _ -> },
) {
    val focusManager = LocalFocusManager.current
    var selectedYear by remember { mutableIntStateOf(2025) }

    Scaffold(
        topBar = {
            CalendarsTopBar()
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
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus() }
                }
        ) {
            item {
                DropdownField(
                    placeholderText = "Ano",
                    items = CalendarUtils.getNextYears().map { it.toString() }.toPersistentList(),
                    onItemSelect = { year ->
                        selectedYear = year.toInt()

                        focusManager.clearFocus()
                    },
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
                        showRippleOnCardClick = true,
                        onCardSelect = {
                            onCalendarClick(month, selectedYear)
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
                        showRippleOnCardClick = true,
                        onCardSelect = {
                            onCalendarClick(month, selectedYear)
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarsTopBar(
    modifier: Modifier = Modifier,
) {
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
        modifier = modifier,
    )
}

@Preview
@Composable
fun CalendarsScreenPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CalendarsScreen(modifier = modifier)
    }
}
