package com.example.fserv.ui.controls.dialogs

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
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
        title = {
            Text(
                text = stringResource(id = R.string.confirmation),
                style = MaterialTheme.typography.h3,
                color = colorResource(id=R.color.text_light)
            )
        },
        backgroundColor = colorResource(id=R.color.action_orange).copy(alpha=0.925f),
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = colorResource(id=R.color.action_dark),
                    disabledContentColor = Color.LightGray
                ),
                onClick = {
                    onConfirm()
                },
                enabled = isEnabled
            )
            { if(!isEnabled) Text(text = "OK $seconds") else Text(text = "OK") }
        },
        dismissButton = {
            TextButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    colorResource(id=R.color.action_dark)
                ),
                onClick = { onDismiss() }) {
                Text(
                    stringResource(id=R.string.cancel),
                )
            }
        },
        contentColor = colorResource(id=R.color.text_light),
        text = {
            Text(
                text = stringResource(id = question),
                color = colorResource(id=R.color.text_light),
                style = MaterialTheme.typography.body1,
            )
        }
    )
}