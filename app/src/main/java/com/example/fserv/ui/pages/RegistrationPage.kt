package com.example.fserv.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter=painterResource(R.drawable.login_bg),
                    contentScale=ContentScale.FillHeight
                ),
            contentAlignment = Alignment.Center,
        ) {
            ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                Column(
                    modifier =Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(id=R.dimen.corner)))
                        .background(colorResource(id=R.color.action_dark).copy(alpha=0.7f))
                        .blur(
                            radiusX=250.dp,
                            radiusY=500.dp,
                            edgeTreatment=BlurredEdgeTreatment(RoundedCornerShape(dimensionResource(id=R.dimen.corner)))
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter=painterResource(id=R.drawable.fireworks_light),
                        contentDescription=null,
                        contentScale=ContentScale.Fit,
                        modifier=Modifier
                            .padding(dimensionResource(id=R.dimen.logo_padding_medium))
                            .height(150.dp)
                    )
                    Spacer(modifier=Modifier.height(dimensionResource(R.dimen.spacer_height)))

                    Text(
                        text=stringResource(id=R.string.sign_up),
                        textAlign=TextAlign.Center,
                        style=MaterialTheme.typography.h5,
                        color=MaterialTheme.colors.primary,
                    )
                    Spacer(modifier=Modifier.height(8.dp))

                    LoginTextField(
                        value=viewModel.email,
                        errorMsg=viewModel.emailErrorMsg,
                        isError=viewModel.emailErrorMsg != -1,
                        onChange={ viewModel.onEmailChange(it) }
                    )

                    Spacer(modifier=Modifier.padding(3.dp))
                    PasswordTextField(
                        value=viewModel.password,
                        errorMsg=viewModel.passwordErrMsg,
                        isError=viewModel.passwordErrMsg != -1,
                        onChange={ viewModel.onPasswordChange(it) }
                    )


                    Spacer(modifier=Modifier.padding(10.dp))
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
                                    SnackbarResult.Dismissed,SnackbarResult.ActionPerformed -> {
                                        viewModel.actionButtonStatus=true
                                        onExit()
                                    }
                                }
                            }
                        }

                        viewModel.registerNewUser(
                            onFailure={ code,string ->
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
                    if (viewModel.dialogStatus) {
                        DialogBoxLoading()
                    }


                    Spacer(modifier=Modifier.padding(10.dp))
                    TextButton(onClick={
                        navController.navigate("login_page") {
                            navController.popBackStack()
                        }
                    }) {
                        Text(
                            text=stringResource(id=R.string.sign_in),
                            letterSpacing=1.sp,
                            style=MaterialTheme.typography.body1
                        )
                    }


                    Spacer(modifier=Modifier.padding(5.dp))
                    TextButton(onClick={
                        navController.navigate("forgot_password_page") {
                            launchSingleTop=true
                        }

                    }) {
                        Text(
                            text=stringResource(id=R.string.forgot_password_question),
                            letterSpacing=1.sp,
                            style=MaterialTheme.typography.body1,
                        )
                    }
                    Spacer(modifier=Modifier.padding(20.dp))
                }
            }
        }
    }
}



