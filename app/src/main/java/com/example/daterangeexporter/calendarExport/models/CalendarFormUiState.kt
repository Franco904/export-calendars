package com.example.daterangeexporter.calendarExport.models

import androidx.annotation.StringRes

data class CalendarFormUiState(
    val label: String? = null,
    @StringRes val labelError: Int? = null,
)
