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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.example.daterangeexporter.R
import com.example.daterangeexporter.calendars.localComposables.CalendarsTopBar
import com.example.daterangeexporter.core.composables.BaseCalendar
import com.example.daterangeexporter.core.composables.DropdownField
import com.example.daterangeexporter.core.constants.DEFAULT_SELECTED_YEAR
import com.example.daterangeexporter.core.constants.SELECTED_YEAR
import com.example.daterangeexporter.core.infra.dataStore
import com.example.daterangeexporter.core.theme.AppTheme
import com.example.daterangeexporter.core.utils.CalendarUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CalendarsScreen(
    modifier: Modifier = Modifier,
    onCalendarSelect: (Int, Int) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()

    val years by remember {
        mutableStateOf(
            CalendarUtils.getNextYears().map { it.toString() }.toPersistentList()
        )
    }
    val currentYear by remember { mutableIntStateOf(CalendarUtils.getCurrentYear()) }
    val currentMonth by remember { mutableIntStateOf(CalendarUtils.getCurrentMonth()) }

    var selectedYear by rememberSaveable { mutableIntStateOf(DEFAULT_SELECTED_YEAR) }

    var isLoading by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        context.dataStore.data.first()[SELECTED_YEAR]?.let { storedSelectedYear ->
            selectedYear = storedSelectedYear
        }

        delay(150.milliseconds)
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
                    years = years,
                    selectedYear = selectedYear,
                    currentYear = currentYear,
                    currentMonth = currentMonth,
                    onYearSelect = { year ->
                        selectedYear = year

                        coroutineScope.launch {
                            context.dataStore.edit { prefs -> prefs[SELECTED_YEAR] = year }
                        }

                        focusManager.clearFocus()
                    },
                    onCalendarSelect = onCalendarSelect,
                )
            }
        }
    }
}

fun LazyListScope.calendarsContent(
    years: ImmutableList<String>,
    currentYear: Int,
    currentMonth: Int,
    selectedYear: Int,
    onYearSelect: (Int) -> Unit,
    onCalendarSelect: (Int, Int) -> Unit,
) {
    item {
        Spacer(modifier = Modifier.height(16.dp))
    }

    item {
        DropdownField(
            placeholderText = stringResource(R.string.year_dropdown_field_placeholder),
            items = years,
            onItemSelect = { year -> onYearSelect(year.toInt()) },
            defaultItem = selectedYear.toString(),
        )
    }

    item {
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (selectedYear == currentYear) {
        items(
            key = { i -> i },
            count = 12 - currentMonth,
        ) { i ->
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
        items(
            key = { i -> i },
            count = 12,
        ) { i ->
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
