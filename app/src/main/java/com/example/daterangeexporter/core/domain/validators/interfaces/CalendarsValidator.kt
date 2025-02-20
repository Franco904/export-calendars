package com.example.daterangeexporter.core.domain.validators.interfaces

import com.example.daterangeexporter.core.domain.utils.Result
import com.example.daterangeexporter.core.domain.utils.ValidationError

interface CalendarsValidator {
    fun validateLabel(label: String?): Result<Unit, ValidationError.CalendarLabel>
}
