package com.example.fserv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fserv.model.server.*
import com.example.fserv.ui.LoginPage
import com.example.fserv.ui.RegisterPage
import com.example.fserv.ui.pages.*
import com.example.fserv.ui.theme.FservTheme
import com.example.fserv.view_models.TicketsGroupsListViewModel
import com.example.fserv.view_models.TicketsListViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.gson.Gson

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

                            composable(
                                route="event_page/{event}",
                                arguments = listOf(navArgument("event"){
                                    type = EventArgType()
                                })
                            ) { navBackStackEntry->
                                val event = navBackStackEntry.arguments?.getString("event")?.let { Gson().fromJson(it, Event::class.java) }
                                if (event != null) {
                                    EventPage(
                                        navController = navController,
                                        event = event
                                    )
                                }
                            }

                            composable(
                                route="tickets_groups/{event}/{ticketsGroups}",
                                arguments = listOf(
                                    navArgument("event"){
                                        type = EventArgType()
                                    },
                                    navArgument("ticketsGroups"){
                                        type = TicketGroupContainerArgType()
                                    }
                                )
                            ) { navBackStackEntry->
                                val event = navBackStackEntry.arguments?.getString("event")?.let { Gson().fromJson(it, Event::class.java) }
                                val ticketGroup = navBackStackEntry.arguments?.getString("ticketsGroups")?.let { Gson().fromJson(it, TicketsGroupsContainer::class.java) }
                                if (ticketGroup != null && event != null) {
                                    TicketsGroups(
                                        navController = navController,
                                        viewModel = TicketsGroupsListViewModel(ticketsGroups = ticketGroup.list, event = event)
                                    )
                                }
                            }

                            composable(
                                route="tickets_list_page/{event}",
                                arguments = listOf(navArgument("event"){
                                    type = EventArgType()
                                })
                            ) { navBackStackEntry->
                                val event = navBackStackEntry.arguments?.getString("event")?.let { Gson().fromJson(it, Event::class.java) }
                                if (event != null) {
                                    TicketsListPage(
                                        TicketsListViewModel(event)
                                    )
                                }
                            }
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
