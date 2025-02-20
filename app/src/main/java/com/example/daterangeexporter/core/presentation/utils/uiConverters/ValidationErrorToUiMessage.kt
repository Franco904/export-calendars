package com.example.daterangeexporter.core.presentation.utils.uiConverters

import com.example.daterangeexporter.R
import com.example.daterangeexporter.core.domain.utils.ValidationError

fun ValidationError.toUiMessage() = when (this) {
    ValidationError.CalendarLabel.IsBlank -> {
        R.string.inline_calendar_label_is_blank
    }

    ValidationError.CalendarLabel.LengthIsGreaterThan25Chars -> {
        R.string.inline_calendar_label_too_big
    }
}
