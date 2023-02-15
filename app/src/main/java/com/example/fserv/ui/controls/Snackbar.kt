package com.example.fserv.ui.controls

import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Composable
fun Snackbar(data: SnackbarData) {
    Text(text = data.message)
}

@Composable
fun SnackbarHost(
    hostState: SnackbarHostState ,
    modifier: Modifier = Modifier ,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) }
) {}


@Stable
class SnackbarHostState {
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ){}
}