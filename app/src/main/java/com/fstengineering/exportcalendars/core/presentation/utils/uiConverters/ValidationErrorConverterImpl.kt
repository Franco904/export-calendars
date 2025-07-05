package com.fstengineering.exportcalendars.core.presentation.utils.uiConverters

import com.fstengineering.exportcalendars.R
import com.fstengineering.exportcalendars.core.domain.utils.ValidationError

class ValidationErrorConverterImpl : ErrorConverter<ValidationError> {
    override fun toUiMessage(error: ValidationError) = when (error) {
        ValidationError.CalendarLabel.IsBlank -> {
            R.string.inline_calendar_label_is_blank
        }

        ValidationError.CalendarLabel.LengthIsGreaterThan25Chars -> {
            R.string.inline_calendar_label_too_big
        }
    }
}
