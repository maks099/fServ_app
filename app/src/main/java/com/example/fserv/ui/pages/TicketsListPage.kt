package com.example.fserv.ui.pages

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.example.fserv.R
import com.example.fserv.model.server.Ticket
import com.example.fserv.ui.controls.*
import com.example.fserv.view_models.TicketsListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


private lateinit var viewModel: TicketsListViewModel
@Composable
fun TicketsListPage(viewModel1: TicketsListViewModel) {
    viewModel = viewModel1
    Scaffold(

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
        shape = RoundedCornerShape(10.dp) ,
        modifier = Modifier
            .padding(
                horizontal = 16.dp ,
                vertical = 8.dp
            )
            .fillMaxWidth()
        ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    horizontal = 25.dp,
                )
        ){
            Text(
                text = "#$index",
                fontSize = 14.sp
            )
            Row{
                val context = LocalContext.current
                IconButton(
                    onClick = {
                        viewModel.checkTicketExisting(ticketId = ticket._id, next = { openPDF(context) })
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_file_open_24) ,
                        contentDescription = stringResource(id = R.string.open_ticket_file)
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.checkTicketExisting(ticketId = ticket._id, next = { shareTicket(context) })
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_share_24) ,
                        contentDescription = stringResource(id = R.string.share_ticket_file)
                    )
                }

            }
        }
    }
}

fun openPDF(context: Context){
    val openingIntent = Intent(Intent.ACTION_VIEW)
    openingIntent.setDataAndType(
        FileProvider.getUriForFile(
            context ,
            context.packageName + ".provider" ,
            viewModel.myFile
        ) ,
        "application/pdf"
    )
    openingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
    context.startActivity(openingIntent)
}

fun shareTicket(context: Context){
    val sharingIntent = Intent(Intent.ACTION_SEND)
    val uri = FileProvider.getUriForFile(
        context ,
        context.packageName + ".provider" ,
        viewModel.myFile
    )
    sharingIntent.putExtra(
        Intent.EXTRA_STREAM ,
        uri
    )
    sharingIntent.type = "application/pdf"
    context.startActivity(sharingIntent)
}

