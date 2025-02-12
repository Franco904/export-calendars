package com.example.daterangeexporter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.example.daterangeexporter.calendarExport.CalendarExportScreen
import com.example.daterangeexporter.calendarExport.CalendarExportViewModel
import com.example.daterangeexporter.core.application.theme.AppTheme
import com.example.daterangeexporter.core.presentation.composables.AppSnackbarHost
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val coroutineScope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    snackbarHost = {
                        AppSnackbarHost(
                            snackbarHostState = snackbarHostState,
                            snackbarContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        )
                    },
                ) { contentPadding ->

                    CalendarExportScreen(
                        viewModel = koinViewModel<CalendarExportViewModel>(),
                        showSnackbar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = message)
                            }
                        },
                        modifier = Modifier
                            .padding(
                                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
                            )
                    )
                }
            }
        }
    }
}
