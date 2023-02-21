package com.example.fserv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fserv.ui.LoginPage
import com.example.fserv.ui.RegisterPage
import com.example.fserv.ui.pages.ConfirmPage
import com.example.fserv.ui.pages.EventsPage
import com.example.fserv.ui.pages.SplashPage
import com.example.fserv.ui.theme.FservTheme
import com.example.fserv.utils.PreferencesRepository
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "MainActivity123";

class MainActivity : ComponentActivity() {

    lateinit var startDestination: String
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startDestination = "splash_page"

        checkForToken() // if user open app by link for confirm his account
        setContent {
            ProvideWindowInsets {
                FservTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        builder = {
                            composable(
                                "splash_page",
                                content = {
                                    SplashPage(
                                        navController = navController
                                    )
                                }
                            )
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
                                }
                            )
                            composable(
                                "confirm_page",
                                content = {
                                    ConfirmPage(
                                        navController = navController,
                                        token
                                    )
                                }
                            )
                            composable(
                                "events_page",
                                content = {
                                    EventsPage(
                                        navController = navController
                                    )
                                }
                            )
                        })
                }
            }
        }


    }



    private fun checkForToken() {
        val uri = intent.data
        if (uri != null) {
            val parameters: List<String> = uri.getPathSegments()
            startDestination = "confirm_page"
            token = parameters[parameters.size - 1]
        }
    }



}
