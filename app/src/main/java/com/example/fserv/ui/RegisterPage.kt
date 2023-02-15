package com.example.fserv.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fserv.R
import com.example.fserv.view_models.LoginViewModel
import com.example.fserv.view_models.RegisterViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding

@Composable
fun RegisterPage(navController: NavController, viewModel: RegisterViewModel = viewModel()){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color.Transparent,
            )
    ) {


        Box(
            modifier = Modifier
                /*.background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(25.dp, 5.dp, 25.dp, 5.dp)
                )*/
                .align(Alignment.BottomCenter),
        ) {
            val mContext = LocalContext.current


            ProvideWindowInsets(windowInsetsAnimationsEnabled = true){
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),

                    )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .navigationBarsWithImePadding()
                        .verticalScroll(rememberScrollState()),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    //.........................Spacer
                    Spacer(modifier = Modifier.height(50.dp))

                    //.........................Text: title
                    Text(
                        text = "Sign Up",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 130.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LoginTextField(
                        value = viewModel.username,
                        errorMsg = viewModel.loginErrMsg,
                        isError = viewModel.loginError,
                        onChange = { viewModel.onUserNameChange(it) }
                    )

                    Spacer(modifier = Modifier.padding(3.dp))
                    PasswordTextField(
                        value = viewModel.password,
                        errorMsg = viewModel.passwordErrMsg,
                        isError = viewModel.passwordError,
                        onChange = { viewModel.onPasswordChange(it) })

                    val gradientColor = listOf(Color(0xFF484BF1), Color(0xFF673AB7))
                    val cornerRadius = 16.dp


                    Spacer(modifier = Modifier.padding(10.dp))
                    Button(
                        onClick = {
                            if(viewModel.validate()){
                                viewModel.registerNewUser()
                            } else {
                                Toast.makeText(mContext, "check your fields", Toast.LENGTH_LONG).show()

                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                    ) {
                        Text(text = "Create An Account", fontSize = 20.sp)
                    }


                    Spacer(modifier = Modifier.padding(10.dp))
                    TextButton(onClick = {

                        navController.navigate("login_page") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }

                    }) {
                        Text(
                            text = "Sign In",
                            letterSpacing = 1.sp,
                            style = MaterialTheme.typography.caption
                        )
                    }


                    Spacer(modifier = Modifier.padding(5.dp))
                    TextButton(onClick = {

                        navController.navigate("reset_page") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }

                    }) {
                        Text(
                            text = "Reset Password",
                            letterSpacing = 1.sp,
                            style = MaterialTheme.typography.caption,
                        )
                    }
                    Spacer(modifier = Modifier.padding(20.dp))
                }
            }
        }
    }
}


@Composable
fun LoginTextField(
    value: String,
    isError: Boolean,
    errorMsg: String,
    onChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            isError = isError,
            onValueChange = { onChange(it) },
            label = { Text("Label") }
        )
        if(isError){
            Text(
                text = errorMsg,
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
    errorMsg: String,
    onChange: (String) -> Unit
) {
    Column{
        OutlinedTextField(
            value = value,
            isError  = isError,
            onValueChange = { onChange(it) },
            label = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if(isError){
            Text(
                text = errorMsg,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

}
