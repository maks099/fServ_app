package com.example.fserv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
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
            textStyle = MaterialTheme.typography.body1,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                errorBorderColor = colorResource(id=R.color.error),
                errorLabelColor = colorResource(id=R.color.error),
                errorCursorColor = colorResource(id=R.color.error),
                focusedBorderColor = colorResource(id=R.color.action_orange),
                unfocusedBorderColor = colorResource(id=R.color.text_light),
                unfocusedLabelColor = colorResource(id=R.color.text_light),
                placeholderColor = colorResource(id=R.color.text_light),
                textColor = colorResource(id=R.color.text_light)
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
                color = colorResource(id=R.color.error),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                errorBorderColor = colorResource(id=R.color.error),
                errorLabelColor = colorResource(id=R.color.error),
                focusedBorderColor = colorResource(id=R.color.action_orange),
                unfocusedBorderColor = colorResource(id=R.color.text_light),
                placeholderColor = colorResource(id=R.color.text_light),
                unfocusedLabelColor = colorResource(id=R.color.text_light),
                textColor = colorResource(id=R.color.text_light)
            ),
            onValueChange = { onChange(it) },
            textStyle = MaterialTheme.typography.body1,
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
    enabled: Boolean,
    onClick: () -> Unit) {
    val localFocusManager = LocalFocusManager.current

    Button(
        onClick = {
            if(enabled){
                localFocusManager.clearFocus()
                onClick()
            }
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id=R.color.action_orange)),
        modifier =Modifier
            .fillMaxWidth(0.6f)
            .height(50.dp)
    ) {
        Text(
            text = stringResource(id = textResource),
            fontSize = 20.sp,
            style = MaterialTheme.typography.body1
        )
    }
}