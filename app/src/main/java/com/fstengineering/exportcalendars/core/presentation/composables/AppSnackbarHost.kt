package com.fstengineering.exportcalendars.core.presentation.composables

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AppSnackbarHost(
    snackbarHostState: SnackbarHostState,
    snackbarContainerColor: Color,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                snackbarData = data,
                containerColor = snackbarContainerColor,
            )
        },
        modifier = modifier
    )
}
