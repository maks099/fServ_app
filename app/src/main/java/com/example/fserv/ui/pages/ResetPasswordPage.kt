package com.example.fserv.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fserv.R
import com.example.fserv.ui.LoginTextField
import com.example.fserv.ui.PasswordTextField
import com.example.fserv.ui.SubmitButton
import com.example.fserv.ui.controls.DialogBoxLoading
import com.example.fserv.view_models.AuthorizationViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordPage(
    navController: NavController,
    viewModel: AuthorizationViewModel,
    token: String
){

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    Scaffold(
        scaffoldState = scaffoldState
    ) { padding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier =Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {

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

                    Image(
                        painter=painterResource(id=R.drawable.ic_launcher_background),
                        contentDescription=null,
                        modifier=Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        contentScale=ContentScale.Fit,

                        )
                    //.........................Spacer

                    //.........................Text: title
                    Text(
                        text = stringResource(id=R.string.reset_password) ,
                        textAlign = TextAlign.Center ,
                        modifier =Modifier
                            .fillMaxWidth() ,
                        style = MaterialTheme.typography.h5 ,
                        color = MaterialTheme.colors.primary ,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    PasswordTextField(
                        value = viewModel.password ,
                        errorMsg = viewModel.passwordErrMsg ,
                        isError = viewModel.passwordErrMsg != -1 ,
                        onChange = { viewModel.onPasswordChange(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SubmitButton(
                        R.string.reset_password ,
                        enabled = viewModel.actionButtonStatus
                    ) {
                        fun showSnackbar(
                            code: Int ,
                            message: String ,
                            onExit: () -> Unit = {}
                        ) {
                            coroutineScope.launch {
                                val snackbarResult =
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "${context.getText(code)} $message" ,
                                        actionLabel = context.getText(R.string.close_caps)
                                            .toString()
                                    )
                                when (snackbarResult) {
                                    SnackbarResult.Dismissed , SnackbarResult.ActionPerformed -> {
                                        viewModel.actionButtonStatus=true
                                        onExit()
                                    }
                                }
                            }
                        }

                        fun backNavigation() {
                            navController.navigate("login_page") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }


                        viewModel.resetPassword(
                            token = token,
                            onFailure = { code, message ->
                                showSnackbar(
                                    code,
                                    message
                                )
                            } ,
                            onSuccess = { code , message ->
                                showSnackbar(
                                    code ,
                                    message ,
                                    {
                                        backNavigation()
                                    }
                                )
                            }
                        )

                    }
                    if(viewModel.dialogStatus){
                        DialogBoxLoading()
                    }


                    Spacer(modifier = Modifier.padding(10.dp))


                    Spacer(modifier = Modifier.padding(5.dp))

                }
            }


        }

    }
}