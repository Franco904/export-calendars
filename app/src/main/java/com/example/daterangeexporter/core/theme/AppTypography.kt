package com.example.daterangeexporter.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.daterangeexporter.R

val interFontFamily = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
)

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 28.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
    ),
)

//val baseline = Typography()
//
//val AppTypography = Typography(
//    displayLarge = com.example.ui.theme.baseline.displayLarge.copy(fontFamily = displayFontFamily),
//    displayMedium = com.example.ui.theme.baseline.displayMedium.copy(fontFamily = displayFontFamily),
//    displaySmall = com.example.ui.theme.baseline.displaySmall.copy(fontFamily = displayFontFamily),
//    headlineLarge = com.example.ui.theme.baseline.headlineLarge.copy(fontFamily = displayFontFamily),
//    headlineMedium = com.example.ui.theme.baseline.headlineMedium.copy(fontFamily = displayFontFamily),
//    headlineSmall = com.example.ui.theme.baseline.headlineSmall.copy(fontFamily = displayFontFamily),
//    titleLarge = com.example.ui.theme.baseline.titleLarge.copy(fontFamily = displayFontFamily),
//    titleMedium = com.example.ui.theme.baseline.titleMedium.copy(fontFamily = displayFontFamily),
//    titleSmall = com.example.ui.theme.baseline.titleSmall.copy(fontFamily = displayFontFamily),
//    bodyLarge = com.example.ui.theme.baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
//    bodyMedium = com.example.ui.theme.baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
//    bodySmall = com.example.ui.theme.baseline.bodySmall.copy(fontFamily = bodyFontFamily),
//    labelLarge = com.example.ui.theme.baseline.labelLarge.copy(fontFamily = bodyFontFamily),
//    labelMedium = com.example.ui.theme.baseline.labelMedium.copy(fontFamily = bodyFontFamily),
//    labelSmall = com.example.ui.theme.baseline.labelSmall.copy(fontFamily = bodyFontFamily),
//)
