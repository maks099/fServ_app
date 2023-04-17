package com.example.fserv.ui.controls.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.fserv.R
import kotlinx.coroutines.delay


@Composable
fun ConfirmationDialog(
    question: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var seconds by remember {
        mutableStateOf(5) // default value = 1 sec
    }

    var isEnabled by remember {
        mutableStateOf(false) // default value = 1 sec
    }
    LaunchedEffect(key1 = Unit, block = {
        while (seconds > 0){
            delay(1000)
            seconds--
        }
        isEnabled = true
    })
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.outlinedButtonColors(colorResource(id=R.color.action_orange)),
                onClick = {
                    onConfirm()
                },
                enabled = isEnabled
            )
            { if(!isEnabled) Text(text = "OK $seconds") else Text(text = "OK") }
        },
        dismissButton = {
            TextButton(
                colors = ButtonDefaults.outlinedButtonColors(colorResource(id=R.color.action_orange)),
                onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        contentColor = colorResource(id=R.color.action_orange),
        title = { Text(text = stringResource(id = R.string.confirmation)) },
        text = { Text(text = stringResource(id = question)) }
    )
}