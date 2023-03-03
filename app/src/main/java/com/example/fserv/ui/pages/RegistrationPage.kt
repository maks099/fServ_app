package com.example.fserv.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

const val TAG = "RegisterPage"

@Composable
fun RegistrationPage(
    navController: NavController,
    viewModel: AuthorizationViewModel
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        scaffoldState = scaffoldState
    ) { padding ->
        Box(
            modifier =Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color=Color.Transparent,
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter) ,
            ) {

                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background) ,
                        contentDescription = null ,
                        contentScale = ContentScale.Fit ,
                        modifier =Modifier
                            .height(150.dp)
                            .fillMaxWidth() ,

                        )
                    Column(
                        modifier =Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .navigationBarsWithImePadding()
                            .verticalScroll(rememberScrollState()) ,

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        //.........................Spacer
                        Spacer(modifier = Modifier.height(50.dp))

                        //.........................Text: title
                        Text(
                            text = "Sign Up" ,
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
                            errorMsg = viewModel.passwordErrMsg ,
                            isError = viewModel.passwordErrMsg != -1 ,
                            onChange = { viewModel.onPasswordChange(it) }
                        )

                        //UserNameFieldsBlock()

                        Spacer(modifier = Modifier.padding(10.dp))
                        SubmitButton(
                            R.string.registration,
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
                                            viewModel.actionButtonStatus = true
                                            onExit()
                                        }
                                    }
                                }
                            }

                            viewModel.registerNewUser(
                                onFailure={ code, string ->
                                    showSnackbar(
                                        code,
                                        string
                                    )
                                },
                                onSuccess={
                                    showSnackbar(
                                        R.string.confirm_email,
                                        ""
                                    ) {
                                        navController.navigate("login_page/${viewModel.email}") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive=true
                                            }
                                            launchSingleTop=true
                                        }
                                    }
                                }
                            )
                        }
                        if(viewModel.dialogStatus){
                            DialogBoxLoading()
                        }


                        Spacer(modifier = Modifier.padding(10.dp))
                        TextButton(onClick = {
                            navController.navigate("login_page") {
                                popUpTo(navController.graph.startDestinationId){
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }) {
                            Text(
                                text = "Sign In" ,
                                letterSpacing = 1.sp ,
                                style = MaterialTheme.typography.caption
                            )
                        }


                        Spacer(modifier = Modifier.padding(5.dp))
                        TextButton(onClick = {

                            navController.navigate("reset_page") {
                                popUpTo(navController.graph.startDestinationId){
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }

                        }) {
                            Text(
                                text = "Reset Password" ,
                                letterSpacing = 1.sp ,
                                style = MaterialTheme.typography.caption ,
                            )
                        }
                        Spacer(modifier = Modifier.padding(20.dp))
                    }
                }
            }
        }
    }
}




@Composable
private fun UserNameFieldsBlock() {
//                        user firstname and last name for printing on tickets
//                        ClientNameTextField(
//                            value = viewModel.firstName ,
//                            errorMsg = viewModel.firstNameErrMsg ,
//                            isError = viewModel.firstNameError ,
//                            onChange = { viewModel.onFirsNameChange(it) },
//                            inputMessage = R.string.input_first_name
//                        )
//                        Spacer(modifier = Modifier.padding(3.dp))
//                        ClientNameTextField(
//                            value = viewModel.lastName ,
//                            errorMsg = viewModel.lastNameErrMsg ,
//                            isError = viewModel.lastNameError ,
//                            onChange = { viewModel.onLastNameChange(it) },
//                            inputMessage = R.string.input_last_name
//                        )
}


