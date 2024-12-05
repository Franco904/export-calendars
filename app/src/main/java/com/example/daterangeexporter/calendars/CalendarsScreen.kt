package com.example.daterangeexporter.calendars


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CalendarsScreen(
    modifier: Modifier = Modifier,
    onCalendarClick: ((Int, Int) -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable { onCalendarClick?.invoke(1, 2024) }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = contentPadding.calculateTopPadding())
        ) {
            // empty
        }
    }
}

@Preview
@Composable
fun CalendarsScreenPreview(
    modifier: Modifier = Modifier,
) {
    CalendarsScreen(modifier = modifier)
}
