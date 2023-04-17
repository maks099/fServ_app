package com.example.fserv.ui.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
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

 class BottomNavItem(var title:String, var icon:ImageVector, var screen_route:String){}

private fun prepareBottomMenu(ctx: Context): List<BottomNavItem>{
    val items = listOf(
        BottomNavItem(ctx.getString(R.string.events),  Icons.Default.Menu,"events_page"),
        BottomNavItem(ctx.getString(R.string.account),  Icons.Default.AccountCircle,"account_page")
    )
    return items;
}

@Composable
fun BottomNavigationPanel(navController: NavController) {

    BottomNavigation(
        backgroundColor = colorResource(id = R.color.action_dark),
    ) {
        prepareBottomMenu(LocalContext.current).forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription ="",
                        tint = colorResource(id=R.color.action_orange)
                    )
               },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.body1,
                        fontSize = 16.sp,
                        color = colorResource(id=R.color.text_light)
                    )
                },
                alwaysShowLabel = true,
                selected = true,
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
                },

            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavController,
                    bottomNavHost: NavHostController) {
    val eventsListState = rememberLazyListState()
    val activityListState = rememberLazyListState()

    EventsListViewModel.initialize()
    ActivitiesListViewModel.initialize()

    val activities = ActivitiesListViewModel.get().getCustomInfos().collectAsLazyPagingItems()
    val events = EventsListViewModel.get().getEvents().collectAsLazyPagingItems()

    val menu = prepareBottomMenu(LocalContext.current)

    NavHost(navController = bottomNavHost, startDestination = menu.first().screen_route) {
        composable(menu.first().screen_route) {
            EventsPage(navController, eventsListState, events,
                onEventCardClick = {
                    navController.navigate("event_page/${it}") {
                        launchSingleTop = true
                    }
            })
        }
        composable(menu.last().screen_route) {
            AccountPage(
                activityListState = activityListState,
                activities,
                onUserActivityClick = {
                    navController.navigate("tickets_list_page/${it._id}") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}