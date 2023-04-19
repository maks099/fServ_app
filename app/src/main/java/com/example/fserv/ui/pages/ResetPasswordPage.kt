package com.example.fserv.ui.pages

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
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
) {

    val scaffoldState: ScaffoldState=rememberScaffoldState()
    val coroutineScope: CoroutineScope=rememberCoroutineScope()
    val context=LocalContext.current


    Scaffold(
        scaffoldState=scaffoldState
    ) { padding ->
        Box(
            modifier=Modifier
                .fillMaxSize()
                .paint(
                    painter=painterResource(R.drawable.login_bg),
                    contentScale=ContentScale.FillHeight
                ),
            contentAlignment=Alignment.Center,
        ) {

            Column(
                modifier=Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .navigationBarsWithImePadding()
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(dimensionResource(id=R.dimen.corner)))
                    .background(colorResource(id=R.color.action_dark).copy(alpha=0.7f))
                    .blur(
                        radiusX=250.dp,
                        radiusY=500.dp,
                        edgeTreatment=BlurredEdgeTreatment(RoundedCornerShape(dimensionResource(id=R.dimen.corner)))
                    ),
                horizontalAlignment=Alignment.CenterHorizontally
            ) {
                Image(
                    painter=painterResource(id=R.drawable.fireworks_light),
                    contentDescription=null,
                    contentScale=ContentScale.Fit,
                    modifier=Modifier
                        .padding(dimensionResource(id=R.dimen.logo_padding_medium))
                        .height(150.dp)

                )

                Text(
                    text=stringResource(id=R.string.reset_password),
                    textAlign=TextAlign.Center,
                    modifier=Modifier
                        .fillMaxWidth(),
                    style=MaterialTheme.typography.h2,
                    color=MaterialTheme.colors.primary,
                )
                Spacer(modifier=Modifier.height(8.dp))
                ProvideWindowInsets(windowInsetsAnimationsEnabled=true) {

                    PasswordTextField(
                        value=viewModel.password,
                        errorMsg=viewModel.passwordErrMsg,
                        isError=viewModel.passwordErrMsg != -1,
                        onChange={ viewModel.onPasswordChange(it) }
                    )

                    Spacer(modifier=Modifier.height(10.dp))

                    SubmitButton(
                        text = stringResource(R.string.reset_password),
                        enabled=viewModel.actionButtonStatus
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

                        fun backNavigation() {
                            navController.navigate("login_page") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive=true
                                }
                                launchSingleTop=true
                            }
                        }


                        viewModel.resetPassword(
                            token=token,
                            onFailure={ code,message ->
                                showSnackbar(
                                    code,
                                    message
                                )
                            },
                            onSuccess={ code,message ->
                                showSnackbar(
                                    code,
                                    message,
                                    {
                                        backNavigation()
                                    }
                                )
                            }
                        )
                    }
                    if (viewModel.dialogStatus) {
                        DialogBoxLoading()
                    }
                    Spacer(modifier=Modifier.padding(20.dp))
                }
            }
        }
    }
}