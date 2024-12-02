package com.example.daterangeexporter.calendars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun CalendarsScreen(
    modifier: Modifier = Modifier,
    onCalendarClick: (Calendar) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    color = Color.Cyan,
                    shape = MaterialTheme.shapes.small,
                )
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    color = Color.LightGray,
                    shape = MaterialTheme.shapes.small,
                )
        )
    }
}

@Preview
@Composable
fun CalendarsScreenPreview(
    modifier: Modifier = Modifier,
) {
    CalendarsScreen(modifier = modifier)
}
