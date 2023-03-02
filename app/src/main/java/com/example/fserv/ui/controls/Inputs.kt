package com.example.fserv.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fserv.R


@Composable
fun LoginTextField(
    value: String,
    isError: Boolean,
    errorMsg: Int,
    onChange: (String) -> Unit
) {
    Column {
        val localFocusManager = LocalFocusManager.current
        OutlinedTextField(
            value = value,
            isError = isError,
            maxLines = 1,

            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { localFocusManager.moveFocus(FocusDirection.Down) },
                onDone = { localFocusManager.clearFocus() }
            ),
            onValueChange = { onChange(it) },
            label = { Text(stringResource(id = R.string.enter_email)) }
        )
        if(isError){
            Text(
                text = stringResource(id = errorMsg),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

}


@Composable
fun PasswordTextField(
    value: String,
    isError: Boolean,
    errorMsg: Int,
    onChange: (String) -> Unit
) {
    Column{
        val localFocusManager = LocalFocusManager.current

        OutlinedTextField(
            value = value,
            isError  = isError,
            maxLines = 1,
            onValueChange = { onChange(it) },
            label = { Text(stringResource(id = R.string.enter_password)) },
            visualTransformation = PasswordVisualTransformation() ,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ) ,
            keyboardActions = KeyboardActions(
                onNext = { localFocusManager.moveFocus(FocusDirection.Down) },
                onDone = { localFocusManager.clearFocus() }
            ),
        )
        if(isError){
            Text(
                text = stringResource(id = errorMsg),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}



@Composable
fun ClientNameTextField(
    value: String,
    isError: Boolean,
    errorMsg: Int,
    inputMessage: Int,
    onChange: (String) -> Unit
) {
    Column{
        val localFocusManager = LocalFocusManager.current

        OutlinedTextField(
            value = value,
            isError  = isError,
            maxLines = 1,
            onValueChange = { onChange(it) },
            label = { Text(stringResource(id = inputMessage)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ) ,
            keyboardActions = KeyboardActions(
                onNext = { localFocusManager.moveFocus(FocusDirection.Down) },
                onDone = { localFocusManager.clearFocus() }
            ),
        )
        if(isError){
            Text(
                text = stringResource(id = errorMsg),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}


@Composable
fun SubmitButton(
    textResource: Int,
    enabled: Boolean = true,
    onClick: () -> Unit) {
    val localFocusManager = LocalFocusManager.current
    Button(
        onClick = {
            localFocusManager.clearFocus()
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp)
    ) {
        Text(
            text = stringResource(id = textResource),
            fontSize = 20.sp
        )
    }
}