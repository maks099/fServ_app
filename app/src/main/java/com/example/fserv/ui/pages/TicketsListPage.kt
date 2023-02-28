package com.example.fserv.ui.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fserv.R
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.Ticket
import com.example.fserv.ui.controls.*
import com.example.fserv.view_models.TicketsListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun TicketsListPage(viewModel: TicketsListViewModel) {
    Scaffold(
        topBar = {
            TopAppBar {
                Text("${stringResource(id = R.string.ticketsFor)} ${viewModel.event.name}")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(paddingValues)
                .background(
                    color = Color.Transparent ,
                )
        ) {
            Column {

                val tickets = viewModel.getTickets().collectAsLazyPagingItems()
                val isRefreshing by viewModel.isRefreshing.collectAsState()

                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing) ,
                    onRefresh = { tickets.refresh() } ,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ) {

                        itemsIndexed(tickets) { index, ticket,  ->
                            if (ticket != null) {
                                TicketCard(
                                    index = index,
                                    ticket = ticket
                                )
                            }
                        }

                        tickets.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item { LoadingView(modifier = Modifier.fillParentMaxSize()) }
                                }
                                loadState.append is LoadState.Loading -> {
                                    item { LoadingItem() }
                                }
                                loadState.refresh is LoadState.Error -> {
                                    val e = tickets.loadState.refresh as LoadState.Error
                                    item {
                                        ErrorItem(
                                            message = e.error.localizedMessage!! ,
                                            modifier = Modifier.fillParentMaxSize() ,
                                            onClickRetry = { retry() }
                                        )
                                    }
                                }
                                loadState.append is LoadState.Error -> {
                                    val e = tickets.loadState.append as LoadState.Error
                                    item {
                                        ErrorItem(
                                            message = e.error.localizedMessage!! ,
                                            onClickRetry = { retry() }
                                        )
                                    }
                                }
                                itemCount == 0 -> {
                                    item {
                                        EmptyListItem(modifier = Modifier.fillParentMaxSize())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}





@Composable
private fun TicketCard(index: Int, ticket: Ticket){
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp) ,
        modifier = Modifier
            .padding(
                horizontal = 16.dp ,
                vertical = 8.dp
            )

        ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("# $index")
        }
    }
}

