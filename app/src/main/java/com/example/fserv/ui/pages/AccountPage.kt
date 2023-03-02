package com.example.fserv.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.fserv.R
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.Ticket
import com.example.fserv.model.server.myInfo
import com.example.fserv.view_models.ActivitiesListViewModel
import com.example.fserv.view_models.TicketsListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun AccountPage(
    navController: NavController,
    activityListState: LazyListState,
    activities: LazyPagingItems<Ticket>
){
    val viewModel = TicketsListViewModel.get()


    val amount = 45

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = stringResource(id = R.string.on_your_account),
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier
                .width(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .padding(horizontal = 25.dp),
                ){
                Text(
                    text = amount.toString(),

                    fontSize = 120.sp
                )
                Text(
                    text = stringResource(id = R.string.currency_short),
                    fontSize = 24.sp
                )
            }

        }
        Button(
            onClick = {},
            modifier = Modifier.width(200.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        ){
            Text(stringResource(id = R.string.free_entrance))
        }

        val isRefreshing by viewModel.isRefreshing.collectAsState()
        SwipeRefresh(
            state =  rememberSwipeRefreshState(isRefreshing = isRefreshing),
            onRefresh = { activities.refresh() },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            LazyColumn(
                state = activityListState,
                modifier = Modifier
                    .fillMaxSize()
            ){
                items(activities){
                    activity ->
                        if(activity != null){
                            InfoCard(activity)
                        }

                }
            }
        }
    }
}

@Composable
private fun InfoCard(eventInfo: Ticket) {
    Log.d("AccountPage", "${eventInfo._id} ${eventInfo._id}$" )
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(eventInfo._id)
    }
}
