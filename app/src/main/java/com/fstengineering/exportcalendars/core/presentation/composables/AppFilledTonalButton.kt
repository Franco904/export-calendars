package com.fstengineering.exportcalendars.core.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
fun AppFilledTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.extraSmall,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .padding(vertical = 8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            )
        }
    }
}

@Preview
@Composable
fun AppFilledTonalButtonPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppFilledTonalButton(
            icon = Icons.Default.Close,
            text = stringResource(R.string.clear_calendar_selected_dates_action_text),
            onClick = {},
        )
    }
}
