package com.example.fserv.ui.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.fserv.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fserv.view_models.AuthorizationViewModel
import com.example.fserv.view_models.ConfirmViewModel
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Preview
@Composable
fun ConfirmPageWrapper(){
    ConfirmPage(
        navController = rememberNavController(),
        token = "123token123"
    )
}
const val TAG = "ConfirmPage"
@Composable
fun ConfirmPage(navController: NavController, token: String){
    val viewModel: ConfirmViewModel = viewModel()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(scaffoldState = scaffoldState) {
        padding ->
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()

                    .verticalScroll(rememberScrollState()) ,

                horizontalAlignment = Alignment.CenterHorizontally,


            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp),
                    color = Color.Green ,
                    strokeWidth = 10.dp ,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Spacer(modifier = Modifier.height(200.dp))

                Log.d(TAG, "before")
                    viewModel.confirmAccount(token).enqueue(

                        object : Callback<String> {

                            override fun onFailure(call: Call<String> , t: Throwable) {
                                showSnackbar("${context.getText(R.string.server_error)} ${t.message.toString()}")
                            }

                            override fun onResponse(
                                call: Call<String> , response: Response<String>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d(
                                        "ConfirmPage" ,
                                        response.body().toString()
                                    )
                                    /*navController.navigate("events_page") {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }*/
                                } else showSnackbar(
                                    "${context.getText(R.string.server_error)} ${
                                        response.errorBody()?.string().toString()
                                    }"
                                )

                            }

                            fun showSnackbar(message: String) {
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = message ,
                                        actionLabel = context.getText(R.string.close_caps)
                                            .toString()
                                    )
                                }
                            }
                        }
                    )


            }
        }
    }
}
