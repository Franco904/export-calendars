package com.example.daterangeexporter.core.composeModels

import androidx.compose.runtime.Stable

@Stable
data class MonthYear(
    val id: Int,
    val month: Int,
    val year: Int,
)
