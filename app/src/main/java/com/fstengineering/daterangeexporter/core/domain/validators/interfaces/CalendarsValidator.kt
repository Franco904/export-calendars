package com.fstengineering.daterangeexporter.core.domain.validators.interfaces

import com.fstengineering.daterangeexporter.core.domain.utils.Result
import com.fstengineering.daterangeexporter.core.domain.utils.ValidationError

interface CalendarsValidator {
    fun validateLabel(label: String?): Result<Unit, ValidationError.CalendarLabel>
}
