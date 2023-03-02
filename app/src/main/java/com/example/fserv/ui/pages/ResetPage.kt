package com.example.fserv.ui.pages

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fserv.R
import com.example.fserv.ui.LoginTextField
import com.example.fserv.ui.PasswordTextField
import com.example.fserv.ui.SubmitButton
import com.example.fserv.view_models.AuthorizationViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ResetPage(navController: NavController) {
    val viewModel: AuthorizationViewModel = viewModel()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color = Color.Transparent ,
                )
        ) {
            val focusManager = LocalFocusManager.current
            val mContext = LocalContext.current

            Box(
                modifier = Modifier.align(Alignment.BottomCenter) ,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background) ,
                    contentDescription = null ,
                    contentScale = ContentScale.Fit ,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth() ,

                    )
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Column(
                        modifier = Modifier
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
                            text = "Sign In" ,
                            textAlign = TextAlign.Center ,
                            modifier = Modifier
                                .padding(top = 130.dp)
                                .fillMaxWidth() ,
                            style = MaterialTheme.typography.h5 ,
                            color = MaterialTheme.colors.primary ,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LoginTextField(
                            value = viewModel.email ,
                            errorMsg = viewModel.emailErrorMsg ,
                            isError = viewModel.emailError ,
                            onChange = { viewModel.onEmailChange(it) }
                        )


                        val errorTitle = stringResource(id = R.string.server_error)
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

                                            message = "${mContext.getText(code)} $message" ,
                                            actionLabel = mContext.getText(R.string.close_caps)
                                                .toString()
                                        )
                                    when (snackbarResult) {
                                        SnackbarResult.Dismissed , SnackbarResult.ActionPerformed -> onExit()
                                    }
                                }
                            }

                            fun backNavigation() {
                                navController.navigate("login_page/${viewModel.email}") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }


                            viewModel.resetPassword(
                                onFailure = { code , message ->
                                    showSnackbar(
                                        code ,
                                        message
                                    )
                                } ,
                                onSuccess = { code , message ->
                                    showSnackbar(
                                        code ,
                                        message ,
                                        { backNavigation() })
                                }
                            )
                        }


                        Spacer(modifier = Modifier.padding(10.dp))


                        Spacer(modifier = Modifier.padding(5.dp))

                    }
                }


            }

        }

    }


}





