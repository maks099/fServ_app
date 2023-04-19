package com.example.fserv.ui.pages

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.fserv.ui.controls.dialogs.ConfirmationDialog
import com.example.fserv.view_models.TicketsListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch


private lateinit var viewModel: TicketsListViewModel

@Composable
fun TicketsListPage(viewModel1: TicketsListViewModel) {
    viewModel=viewModel1
    var pickedTicket=remember {
        mutableStateOf(Ticket.getEmpty())
    }
    val scaffoldState: ScaffoldState=rememberScaffoldState()
    val coroutineScope=rememberCoroutineScope()
    val tickets=viewModel.getTickets().collectAsLazyPagingItems()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val context=LocalContext.current;
    Scaffold(
        scaffoldState=scaffoldState,
    ) { paddingValues ->
        Box(
            modifier=Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colorResource(id=R.color.action_orange),
                            colorResource(id=R.color.action_dark)
                        )
                    )
                ),
        ) {
            fun showSnackBar(
                title: Int,
                message: String,
            ) {
                if (!viewModel.snackIsShowing) {
                    viewModel.snackIsShowing=true
                    coroutineScope.launch {
                        val snackbarResult=
                            scaffoldState.snackbarHostState.showSnackbar(
                                message="${context.getString(title)} $message",
                            )
                        when (snackbarResult) {
                            SnackbarResult.Dismissed,SnackbarResult.ActionPerformed -> {
                                viewModel.snackIsShowing=false
                            }
                        }
                    }
                }
            }

            if (pickedTicket.value._id != "") {
                ConfirmationDialog(
                    question=R.string.remove_ticket_confirm,
                    onDismiss={ pickedTicket.value=Ticket.getEmpty() },
                    onConfirm={
                        viewModel.removeTicket(
                            pickedTicket.value._id,
                            onError={
                                showSnackBar(
                                    R.string.error,
                                    it
                                )
                            },
                            onSuccess={
                                tickets.refresh()
                                showSnackBar(
                                    R.string.ticket_is_removed,
                                    ""
                                )
                            }
                        )
                        pickedTicket.value=Ticket.getEmpty()
                    }
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = viewModel.eventName,
                    fontSize = 24.sp,
                    color = colorResource(id=R.color.text_light),
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(12.dp)
                )
                SwipeRefresh(
                    state=rememberSwipeRefreshState(isRefreshing),
                    onRefresh={ tickets.refresh() },
                    modifier=Modifier
                        .fillMaxSize()
                ) {
                    LazyColumn{

                        itemsIndexed(tickets) { index,ticket ->
                            if (ticket != null) {
                                TicketCard(
                                    index=index,
                                    ticket=ticket,
                                ) {
                                    pickedTicket.value=ticket
                                }
                            }
                        }

                        tickets.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item { LoadingView(modifier=Modifier.fillParentMaxSize()) }
                                }
                                loadState.append is LoadState.Loading -> {
                                    item { LoadingItem() }
                                }
                                loadState.refresh is LoadState.Error -> {
                                    val e=tickets.loadState.refresh as LoadState.Error
                                    item {
                                        ErrorItem(
                                            message=e.error.localizedMessage!!,
                                            modifier=Modifier.fillParentMaxSize(),
                                            onClickRetry={ retry() }
                                        )
                                    }
                                }
                                loadState.append is LoadState.Error -> {
                                    val e=tickets.loadState.append as LoadState.Error
                                    item {
                                        ErrorItem(
                                            message=e.error.localizedMessage!!,
                                            onClickRetry={ retry() }
                                        )
                                    }
                                }
                                itemCount == 0 -> {
                                    item {
                                        EmptyListItem(modifier=Modifier.fillParentMaxSize())
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
private fun TicketCard(
    index: Int,
    ticket: Ticket,
    onDelete: () -> Unit,
) {
    Card(
        shape=RoundedCornerShape(10.dp),
        modifier=Modifier
            .padding(
                horizontal=12.dp
            )

            .fillMaxWidth()
            .padding(vertical=6.dp)
    ) {
        Row(
            verticalAlignment=Alignment.CenterVertically,
            horizontalArrangement=Arrangement.SpaceBetween,
            modifier =Modifier
                .background(colorResource(id=R.color.action_dark).copy(0.09f))
                .padding(8.dp)

        ) {
            Column(

            ) {
                Text(
                    text="#${index+1}",
                    fontSize=16.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text="${ticket.groupName} / ${ticket.price} ${stringResource(id=R.string.currency_short)}",
                    fontSize=14.sp,
                    style = MaterialTheme.typography.body1
                )
            }

            Row {
                val context=LocalContext.current
                IconButton(
                    onClick={
                        viewModel.checkTicketExisting(
                            ticketId=ticket._id,
                            next={ openPDF(context) })
                    }) {
                    Icon(
                        painter=painterResource(id=R.drawable.baseline_file_open_24),
                        contentDescription=stringResource(id=R.string.open_ticket_file)
                    )
                }
                IconButton(
                    onClick={
                        viewModel.checkTicketExisting(
                            ticketId=ticket._id,
                            next={ shareTicket(context) })
                    }) {
                    Icon(
                        painter=painterResource(id=R.drawable.baseline_share_24),
                        contentDescription=stringResource(id=R.string.share_ticket_file)
                    )
                }
                IconButton(
                    onClick={
                        onDelete()
                    }) {
                    Icon(
                        painter=painterResource(id=R.drawable.baseline_delete_forever_24),
                        contentDescription=stringResource(id=R.string.delete_ticket)
                    )
                }
            }
        }
    }
}

fun openPDF(context: Context) {
    val openingIntent=Intent(Intent.ACTION_VIEW)
    openingIntent.setDataAndType(
        FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            viewModel.myFile
        ),
        "application/pdf"
    )
    openingIntent.flags=Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
    context.startActivity(openingIntent)
}

fun shareTicket(context: Context) {
    val sharingIntent=Intent(Intent.ACTION_SEND)
    val uri=FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        viewModel.myFile
    )
    sharingIntent.putExtra(
        Intent.EXTRA_STREAM,
        uri
    )
    sharingIntent.type="application/pdf"
    context.startActivity(sharingIntent)
}

