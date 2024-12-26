package com.example.daterangeexporter.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel

@Composable
fun <VM : ViewModel> VM.invokeAfterComposition(onViewModel: VM.() -> Unit) = also {
    LaunchedEffect(Unit) { onViewModel() }
}
