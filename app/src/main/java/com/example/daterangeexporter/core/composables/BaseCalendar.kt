package com.example.daterangeexporter.core.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BaseCalendar(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier)
}

@Preview
@Composable
fun BaseCalendarPreview(
    modifier: Modifier = Modifier,
) {
    BaseCalendar(modifier = modifier)
}
