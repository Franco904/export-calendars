package com.fstengineering.exportcalendars.core.domain.validators.interfaces

import com.fstengineering.exportcalendars.core.domain.utils.Result
import com.fstengineering.exportcalendars.core.domain.utils.ValidationError

interface CalendarsValidator {
    fun validateLabel(label: String?): Result<Unit, ValidationError.CalendarLabel>
}
