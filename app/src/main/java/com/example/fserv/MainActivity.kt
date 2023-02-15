package com.example.fserv

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fserv.ui.LoginPage
import com.example.fserv.ui.RegisterPage
import com.example.fserv.ui.theme.FservTheme
import com.google.accompanist.insets.ProvideWindowInsets
private const val TAG = "MainActivity";

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.data

        // checking if the uri is null or not.

        // checking if the uri is null or not.
        if (uri != null) {

            // if the uri is not null then we are getting
            // the path segments and storing it in list.
            val parameters: List<String> = uri.getPathSegments()

            // after that we are extracting string
            // from that parameters.
            val param = parameters[parameters.size - 1]
            Log.d(TAG, "hello")

            Log.d(TAG, param)
        }
        setContent {
            ProvideWindowInsets {
                FservTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "register_page",
                        builder = {
                            composable(
                                "login_page",
                                content = { LoginPage(navController = navController) })
                    composable("register_page", content = { RegisterPage(navController = navController) })
//                    composable("reset_page", content = { ResetPage(navController = navController) })
                        })
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FservTheme {
        Greeting("Android")
    }
}