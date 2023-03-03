package com.example.fserv.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.fserv.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fserv.view_models.AuthorizationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun ConfirmPage(navController: NavController, viewModel: AuthorizationViewModel, token: String){
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val isConfirming = rememberSaveable{ mutableStateOf(true) }
    val context = LocalContext.current

    Scaffold(scaffoldState = scaffoldState) {
        padding ->
        Box(modifier =Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier =Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()) ,
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                if(isConfirming.value){
                    Column {
                        CircularProgressIndicator(
                            modifier =Modifier
                                .width(200.dp)
                                .height(200.dp),
                            color = Color.Green ,
                            strokeWidth = 10.dp ,
                        )
                        Text(stringResource(id=R.string.check_in_progress))
                    }
                } else {
                    Text(stringResource(id=R.string.error))
                }


                Spacer(modifier = Modifier.height(20.dp))
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
                            SnackbarResult.Dismissed , SnackbarResult.ActionPerformed -> onExit()
                        }
                    }
                }

                viewModel.confirmAccount(
                    token = token,
                    onSuccess = {
                        navController.navigate("main_page") {
                            popUpTo("login_page") {
                                inclusive = true
                            }
                            navController.clearBackStack("login_page")
                            navController.clearBackStack("confirm_page")
                        }
                    },
                    onFailure = {
                        code, string ->
                        showSnackbar(code, string)
                        isConfirming.value = false
                    }
                )


            }
        }
    }
}
