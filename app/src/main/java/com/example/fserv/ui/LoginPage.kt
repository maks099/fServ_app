package com.example.fserv.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fserv.R
import com.example.fserv.view_models.LoginViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding

@Preview(showBackground = true)
@Composable
fun PreviewLoginPage(){
    LoginPage(navController = rememberNavController())
}


@Composable
fun LoginPage(navController: NavController, viewModel: LoginViewModel = viewModel()) {
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
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),

                )
            ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
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
                        text = "Sign In",
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
                        onChange = { viewModel.onUserNameChange(it) }
                    )

                    Spacer(modifier = Modifier.padding(3.dp))
                    PasswordTextField(
                        value = viewModel.password,
                        onChange = { viewModel.onPasswordChange(it) })

                    val gradientColor = listOf(Color(0xFF484BF1), Color(0xFF673AB7))
                    val cornerRadius = 16.dp


                    Spacer(modifier = Modifier.padding(10.dp))
                    Button(
                        onClick = { viewModel.makeTestRequest() },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                    ) {
                        Text(text = "Login", fontSize = 20.sp)
                    }


                    Spacer(modifier = Modifier.padding(10.dp))
                    TextButton(onClick = {

                        navController.navigate("register_page") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }

                    }) {
                        Text(
                            text = "Create An Account",
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
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it) },
        label = { Text("Label") }
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it) },
        label = { Text("Enter password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}
