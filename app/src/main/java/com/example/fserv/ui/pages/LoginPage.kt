package com.example.fserv.ui

import android.app.Application
import android.util.Log
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fserv.R
import com.example.fserv.view_models.AuthorizationViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



@Composable
fun LoginPage(navController: NavController , application: Application, login: String? = "") {
    val viewModel: AuthorizationViewModel = viewModel()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    if (login != null) {
        viewModel.email = login
    }

    Scaffold(
        scaffoldState = scaffoldState,
        content = { padding ->
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
                    modifier = Modifier
                        /*.background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(25.dp, 5.dp, 25.dp, 5.dp)
                )*/
                        .align(Alignment.BottomCenter) ,
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

                            Spacer(modifier = Modifier.padding(3.dp))
                            PasswordTextField(
                                value = viewModel.password ,
                                errorMsg = -1 ,
                                isError = false ,
                                onChange = { viewModel.password = it })


                            Spacer(modifier = Modifier.padding(10.dp))
                            SubmitButton(
                                R.string.login ,
                                viewModel.actionButtonStatus ,
                            ) {
                                    if (viewModel.validate()) {

                                        focusManager.clearFocus()
                                        viewModel.loginClient(application).enqueue(
                                            object : Callback<String> {
                                                override fun onFailure(
                                                    call: Call<String> ,
                                                    t: Throwable
                                                ) {
                                                    Log.d(
                                                        TAG ,
                                                        "failure22 " + t.message
                                                    )
                                                    val message =
                                                        "${mContext.getText(R.string.server_error)} ${t.message.toString()}"
                                                    showSnackbar(message)


                                                }

                                                override fun onResponse(
                                                    call: Call<String> ,
                                                    response: Response<String>
                                                ) {


                                                    if (response.isSuccessful) {
                                                        Log.d(
                                                            TAG ,
                                                            "success "
                                                        )


                                                        showSnackbar("success")


                                                        /* */
                                                        /* todo
                                                      get link onto the post
                                                      send link to post
                                                      open link on the post and navigation to main screen
                                                   */
                                                    } else {

                                                        showSnackbar("${mContext.getText(R.string.server_error)} ${response.errorBody()?.string().toString()}")
                                                    }
                                                }

                                                fun showSnackbar(message: String) {
                                                    viewModel.actionButtonStatus = false
                                                    coroutineScope.launch {
                                                        val snackbarResult =
                                                            scaffoldState.snackbarHostState.showSnackbar(
                                                                message = message ,
                                                                actionLabel = mContext.getText(R.string.close_caps)
                                                                    .toString()
                                                            )
                                                        when (snackbarResult) {
                                                            SnackbarResult.Dismissed , SnackbarResult.ActionPerformed -> viewModel.actionButtonStatus =
                                                                true
                                                        }
                                                    }
                                                }


                                            }
                                        )
                                    } else {
                                        viewModel.actionButtonStatus = false
                                        coroutineScope.launch {
                                            val snackbarResult =
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    message = mContext.getText(R.string.error_form)
                                                        .toString() ,
                                                    actionLabel = mContext.getText(R.string.close_caps)
                                                        .toString()
                                                )
                                            when (snackbarResult) {
                                                SnackbarResult.Dismissed , SnackbarResult.ActionPerformed -> viewModel.actionButtonStatus =
                                                    true
                                            }
                                        }
                                    }
                                }


                            Spacer(modifier = Modifier.padding(10.dp))
                            TextButton(onClick = {

                                navController.navigate("register_page") {
                                    popUpTo(navController.graph.startDestinationId)
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

                                navController.navigate("reset_page") {
                                    popUpTo(navController.graph.startDestinationId)
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
        })


}


