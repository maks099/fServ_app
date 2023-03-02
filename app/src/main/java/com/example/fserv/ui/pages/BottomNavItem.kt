package com.example.fserv.ui.pages

import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.fserv.R
import com.example.fserv.view_models.ActivitiesListViewModel
import com.example.fserv.view_models.EventsListViewModel
import com.example.fserv.view_models.TicketsListViewModel

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){
    object Events : BottomNavItem("Events", R.drawable.baseline_share_24,"events_page")
    object Account: BottomNavItem("Account",R.drawable.baseline_file_open_24,"account_page")
}

@Composable
fun BottomNavigationPanel(navController: NavController) {
    val items = listOf(
        BottomNavItem.Events,
        BottomNavItem.Account
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title,
                    fontSize = 9.sp) },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = false,
                onClick = {
                    navController.navigate(item.screen_route) {
                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavController,
                    bottomNavHost: NavHostController) {
  //  val eventsListState = rememberLazyListState()
    val activityListState = rememberLazyListState()

    EventsListViewModel.initialize()
    TicketsListViewModel.initialize()

    val activities = TicketsListViewModel.get().getTickets().collectAsLazyPagingItems()

 //   val listViewModel: EventsListViewModel = EventsListViewModel.get()
    val activitiesViewModel: TicketsListViewModel = TicketsListViewModel.get()

   //// val events = listViewModel.getEvents().collectAsLazyPagingItems()

    NavHost(navController = bottomNavHost, startDestination = BottomNavItem.Account.screen_route) {
        /*composable(BottomNavItem.Events.screen_route) {
            EventsPage(navController, eventsListState, events,
                onEventCardClick = {
                    navController.navigate("event_page/${it}") {
                        launchSingleTop = true
                    }
            })
        }*/
        composable(BottomNavItem.Account.screen_route) {
            AccountPage(
                navController,
                activityListState = activityListState,
                activities
            )
        }
    }
}