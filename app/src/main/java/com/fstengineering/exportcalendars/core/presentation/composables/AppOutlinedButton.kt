package com.fstengineering.exportcalendars.core.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fstengineering.exportcalendars.R
import com.fstengineering.exportcalendars.core.application.theme.AppTheme

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isEnabled: Boolean = true,
) {
    Surface(
        border = BorderStroke(
            width = 1.dp,
            color = if (isEnabled) {
                MaterialTheme.colorScheme.outline
            } else MaterialTheme.colorScheme.onSurface,
        ),
        shape = MaterialTheme.shapes.extraSmall,
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = if (isEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if (isEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    }
}

@Preview
@Composable
fun AppOutlinedButtonPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppOutlinedButton(
            icon = Icons.Default.Add,
            text = stringResource(R.string.select_calendar_dates_action_text),
            onClick = {},
        )
    }
}
