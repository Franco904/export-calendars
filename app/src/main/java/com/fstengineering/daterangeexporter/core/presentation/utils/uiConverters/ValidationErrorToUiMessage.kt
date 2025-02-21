package com.fstengineering.daterangeexporter.core.presentation.utils.uiConverters

import com.fstengineering.daterangeexporter.R
import com.fstengineering.daterangeexporter.core.domain.utils.ValidationError

fun ValidationError.toUiMessage() = when (this) {
    ValidationError.CalendarLabel.IsBlank -> {
        R.string.inline_calendar_label_is_blank
    }

    ValidationError.CalendarLabel.LengthIsGreaterThan25Chars -> {
        R.string.inline_calendar_label_too_big
    }
}
