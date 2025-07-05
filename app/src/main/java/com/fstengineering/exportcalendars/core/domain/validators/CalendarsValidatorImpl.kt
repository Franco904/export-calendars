package com.fstengineering.exportcalendars.core.domain.validators

import com.fstengineering.exportcalendars.core.domain.utils.Result
import com.fstengineering.exportcalendars.core.domain.utils.ValidationError
import com.fstengineering.exportcalendars.core.domain.validators.interfaces.CalendarsValidator

class CalendarsValidatorImpl : CalendarsValidator {
    override fun validateLabel(label: String?): Result<Unit, ValidationError.CalendarLabel> {
        return when {
            label.isNullOrBlank() -> Result.Error(error = ValidationError.CalendarLabel.IsBlank)
            label.length > 25 -> Result.Error(error = ValidationError.CalendarLabel.LengthIsGreaterThan25Chars)
            else -> Result.Success(data = Unit)
        }
    }
}
