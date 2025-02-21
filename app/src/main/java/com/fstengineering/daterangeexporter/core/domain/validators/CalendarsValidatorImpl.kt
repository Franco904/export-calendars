package com.fstengineering.daterangeexporter.core.domain.validators

import com.fstengineering.daterangeexporter.core.domain.utils.Result
import com.fstengineering.daterangeexporter.core.domain.utils.ValidationError
import com.fstengineering.daterangeexporter.core.domain.validators.interfaces.CalendarsValidator

class CalendarsValidatorImpl : CalendarsValidator {
    override fun validateLabel(label: String?): Result<Unit, ValidationError.CalendarLabel> {
        return when {
            label.isNullOrBlank() -> Result.Error(error = ValidationError.CalendarLabel.IsBlank)
            label.length > 25 -> Result.Error(error = ValidationError.CalendarLabel.LengthIsGreaterThan25Chars)
            else -> Result.Success(data = Unit)
        }
    }
}
