package com.example.fserv

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fserv.ui.LoginPage
import com.example.fserv.ui.RegisterPage
import com.example.fserv.ui.pages.ConfirmPage
import com.example.fserv.ui.theme.FservTheme
import com.google.accompanist.insets.ProvideWindowInsets

private const val TAG = "MainActivity123";

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.data
        var startDestination = "register_page"
        var token: String = ""

        if (uri != null) {
            val parameters: List<String> = uri.getPathSegments()
            startDestination = "confirm_page"
            token = parameters[parameters.size - 1]
            Log.d(TAG, token + " hell")

        }
        setContent {
            ProvideWindowInsets {
                FservTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        builder = {

                            composable(
                                "login_page/{login}",
                                arguments = listOf(navArgument("login") { type = NavType.StringType; defaultValue = "" })
                                ){
                                    backStackEntry -> LoginPage(navController = navController, application = application, backStackEntry.arguments?.getString("login"))

                                }
                            composable(
                                "login_page"
                            ){
                                    _ -> LoginPage(navController = navController, application = application)

                            }
                            composable(
                                "register_page",
                                content = {
                                    RegisterPage(
                                        navController = navController,
                                        application = application
                                    )
                                })
                            composable(
                                "confirm_page",
                                content = {
                                    ConfirmPage(
                                        navController = navController,
                                        token
                                    )
                                })
                        })
                }
            }
        }
    }
}
