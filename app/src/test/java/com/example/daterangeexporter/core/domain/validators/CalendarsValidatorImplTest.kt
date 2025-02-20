package com.example.daterangeexporter.core.domain.validators

import com.example.daterangeexporter.core.domain.utils.Result
import com.example.daterangeexporter.core.domain.utils.ValidationError
import com.example.daterangeexporter.testUtils.faker
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CalendarsValidatorImplTest {
    private lateinit var sut: CalendarsValidatorImpl

    @BeforeEach
    fun setUp() {
        sut = CalendarsValidatorImpl()
    }

    @Nested
    @DisplayName("validateLabel")
    inner class ValidateLabelTests {
        @Test
        fun `Should return 'is blank' error when the label is null or blank`() =
            runTest {
                val errorResult = Result.Error<Unit, ValidationError>(
                    error = ValidationError.CalendarLabel.IsBlank,
                )

                sut.validateLabel(null) shouldBeEqualTo errorResult
                sut.validateLabel("") shouldBeEqualTo errorResult
                sut.validateLabel("   ") shouldBeEqualTo errorResult
            }

        @Test
        fun `Should return 'length is greater than 25 chars' error when the label length is greater than 25 chars`() =
            runTest {
                val errorResult = Result.Error<Unit, ValidationError>(
                    error = ValidationError.CalendarLabel.LengthIsGreaterThan25Chars,
                )

                sut.validateLabel("franco saravia tavares 777") shouldBeEqualTo errorResult
                sut.validateLabel(
                    label = faker.random.randomString(min = 26, max = 100),
                ) shouldBeEqualTo errorResult
            }

        @Test
        fun `Should return success result if all validation checks pass`() =
            runTest {
                val successResult = Result.Success<Unit, ValidationError>(data = Unit)

                sut.validateLabel("Carina") shouldBeEqualTo successResult
                sut.validateLabel("franco") shouldBeEqualTo successResult
                sut.validateLabel("Julio") shouldBeEqualTo successResult
                sut.validateLabel("carlos") shouldBeEqualTo successResult
                sut.validateLabel("henrique hernandez de luz") shouldBeEqualTo successResult
            }
    }
}