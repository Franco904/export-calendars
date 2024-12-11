package com.example.daterangeexporter.calendarExport.localComposables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.daterangeexporter.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarExportTopBar(
    onUpNavigation: () -> Boolean,
    onEditCalendar: () -> Unit,
    onClearSelectedDates: () -> Unit,
    isSelectedDatesEmpty: Boolean,
    modifier: Modifier = Modifier,
) {
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.export_calendar_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = { onUpNavigation() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        actions = {
            AnimatedContent(
                targetState = isSelectedDatesEmpty,
                label = "isSelectedDatesEmpty",
                transitionSpec = {
                    (fadeIn(tween(300)) + scaleIn(initialScale = 0.8f)) togetherWith
                            (fadeOut(tween(300)) + scaleOut(targetScale = 0.8f))
                }
            ) { isEmpty ->
                val icon = if (isEmpty) Icons.Default.Edit else Icons.Default.Close
                val contentDescription = if (isEmpty) {
                    R.string.edit_calendar_selected_dates_action_text
                } else R.string.clear_calendar_selected_dates_action_text

                IconButton(
                    onClick = if (isEmpty) onEditCalendar else onClearSelectedDates,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(contentDescription),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        modifier = modifier
            .drawBehind {
                drawLine(
                    color = outlineVariantColor,
                    start = Offset(x = 0f, y = size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx(),
                )
            }
    )
}
