package com.example.fserv.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fserv.R
import com.example.fserv.ui.controls.DialogBoxLoading
import com.example.fserv.view_models.AuthorizationViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



@Composable
fun LoginPage(navController: NavController, viewModel: AuthorizationViewModel, login: String? = "") {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    if (login != null) {
        viewModel.email = login
    }

    Scaffold(
        scaffoldState = scaffoldState,
        content = { padding ->
            Box(
                modifier =Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        color=Color.Transparent,
                    )
            ) {

                Box(
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background) ,
                        contentDescription = null ,
                        contentScale = ContentScale.Fit ,
                        modifier =Modifier
                            .height(150.dp)
                            .fillMaxWidth() ,

                        )
                    ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                        Column(
                            modifier =Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .navigationBarsWithImePadding()
                                .verticalScroll(rememberScrollState()) ,

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Spacer(modifier = Modifier.height(50.dp))

                            Text(
                                text = "Sign In" ,
                                textAlign = TextAlign.Center ,
                                modifier =Modifier
                                    .padding(top=130.dp)
                                    .fillMaxWidth() ,
                                style = MaterialTheme.typography.h5 ,
                                color = MaterialTheme.colors.primary ,
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LoginTextField(
                                value = viewModel.email ,
                                errorMsg = viewModel.emailErrorMsg ,
                                isError = viewModel.emailErrorMsg != -1 ,
                                onChange = { viewModel.onEmailChange(it) }
                            )

                            Spacer(modifier = Modifier.padding(3.dp))
                            PasswordTextField(
                                value = viewModel.password ,
                                errorMsg = -1 ,
                                isError = false ,
                                onChange = { viewModel.password = it })


                            Spacer(modifier = Modifier.padding(10.dp))
                            SubmitButton(
                                R.string.login,
                                viewModel.actionButtonStatus
                            ) {
                                fun showSnackbar(
                                    code: Int,
                                    message: String,
                                    onExit: () -> Unit={}
                                ) {
                                    coroutineScope.launch {
                                        val snackbarResult=
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                message="${context.getText(code)} $message",
                                                actionLabel=context.getText(R.string.close_caps)
                                                    .toString()
                                            )
                                        when (snackbarResult) {
                                            SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> {
                                                viewModel.actionButtonStatus=true
                                                onExit()
                                            }
                                        }
                                    }
                                }

                                viewModel.loginClient(
                                    onFailure={ code, string ->
                                        showSnackbar(
                                            code,
                                            string
                                        )
                                    },
                                    onSuccess={
                                        navController.navigate("main_page") {
                                            popUpTo("login_page") {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                            if(viewModel.dialogStatus){
                                DialogBoxLoading()
                            }

                            Spacer(modifier = Modifier.padding(10.dp))
                            TextButton(onClick = {

                                navController.navigate("register_page") {
                                    popUpTo(navController.graph.startDestinationId){
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }

                            }) {
                                Text(
                                    text = "Create An Account" ,
                                    letterSpacing = 1.sp ,
                                    style = MaterialTheme.typography.caption
                                )
                            }


                            Spacer(modifier = Modifier.padding(5.dp))
                            TextButton(onClick = {

                                navController.navigate("forgot_password_page") {
                                    popUpTo(navController.graph.startDestinationId){
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }

                            }) {
                                Text(
                                    text = stringResource(id = R.string.forgot_password) ,
                                    letterSpacing = 1.sp ,
                                    style = MaterialTheme.typography.caption ,
                                )
                            }
                            Spacer(modifier = Modifier.padding(20.dp))
                        }
                    }

                }
            }
        })


}


