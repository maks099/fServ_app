package com.example.fserv.ui.pages

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.fserv.R
import com.example.fserv.view_models.EventsListViewModel

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
    val lazyListState = rememberLazyListState()
    EventsListViewModel.initialize()
    val listViewModel: EventsListViewModel = EventsListViewModel.get()
    val items = listViewModel.getEvents().collectAsLazyPagingItems()
    NavHost(navController = bottomNavHost, startDestination = BottomNavItem.Events.screen_route) {
        composable(BottomNavItem.Events.screen_route) {
            EventsPage(navController, lazyListState, items, onEventCardClick = {
                navController.navigate("event_page/${it}") {
                    launchSingleTop = true
                }
            })
        }
        composable(BottomNavItem.Account.screen_route) {
            AccountPage(navController)
        }
    }
}