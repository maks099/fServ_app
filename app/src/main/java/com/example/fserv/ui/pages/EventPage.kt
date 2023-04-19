package com.example.fserv.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.fserv.R
import com.example.fserv.model.app.DownloadType
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.TicketsGroupsContainer
import com.example.fserv.ui.SubmitButton
import com.example.fserv.ui.controls.AddressControl
import com.example.fserv.ui.controls.ImageSlider
import com.example.fserv.ui.controls.SimpleTags
import com.example.fserv.view_models.EventViewModel
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch
import java.util.*


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EventPage(navController: NavController,event: Event) {
    val viewModel=EventViewModel()
    val scrollState=rememberScrollState()
    val scaffoldState: ScaffoldState=rememberScaffoldState()

    Scaffold(
        scaffoldState=scaffoldState
    ) { paddingValues ->
        Column(
            modifier=Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colorResource(id=R.color.action_orange),
                            colorResource(id=R.color.action_dark)
                        )
                    )
                ),
            horizontalAlignment=Alignment.CenterHorizontally
        ) {
            Text(
                text=event.name,
                color = colorResource(id=R.color.text_light),
                style = MaterialTheme.typography.h1,
                modifier = Modifier.padding(12.dp)
            )
            Row(
                horizontalArrangement=Arrangement.SpaceBetween,
                modifier=Modifier
                    .padding(12.dp)
                    .fillMaxWidth()

            ) {
                RowItem(
                    text="${stringResource(id=R.string.organizer)}\r\n${event.trademark}"
                )

                val configuration=LocalConfiguration.current
                val currentLocale=ConfigurationCompat.getLocales(configuration).get(0)
                RowItem(
                    text="${stringResource(id=R.string.date)}\r\n${currentLocale?.let { event.getParsedDate(it) }}"
                )
            }

            ImageSlider(urlList=event.gallery)
            FlowRow(
                modifier = Modifier.padding(12.dp)
            ) {
                event.tags.forEach { tag ->
                    if (tag.trim().isNotEmpty()) {
                        SimpleTags(
                            text=tag,
                            isActive=false,
                            onClick={ }
                        )
                    }
                }
            }
            Text(
                text=event.description,
                textAlign=TextAlign.Justify,
                color = colorResource(id=R.color.text_light),
                modifier=Modifier
                    .padding(12.dp)
            )

            AddressControl(
                address=event.address,
                latitude=event.latitude,
                longitude=event.longitude
            )
            if (event.isPaid) {
                SubmitButton(
                    onClick={
                        println("buy on click")
                        viewModel.getTickets(event._id)
                    },
                    enabled = true,
                    text = stringResource(id=R.string.buy_ticket)
                )
            } else {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = stringResource(id=R.string.free_entrance),
                    color = colorResource(id=R.color.action_orange),
                    style = MaterialTheme.typography.h3
                )
            }


            when (viewModel.downloadType) {
                DownloadType.SUCCESS -> {
                    println("on success")
                    val listContainer=TicketsGroupsContainer(list=viewModel.ticketsGroups.toList())
                    navController.navigate("tickets_groups/${event}/${listContainer}") {
                        launchSingleTop=true
                    }
                }
                DownloadType.FAIL -> {
                    viewModel.downloadType=DownloadType.PREVIEW
                    viewModel.actionButtonStatus=false
                    viewModel.viewModelScope.launch {
                        val snackRes=scaffoldState.snackbarHostState.showSnackbar(
                            message=viewModel.downloadType.message
                        )
                        when (snackRes) {
                            SnackbarResult.Dismissed,SnackbarResult.ActionPerformed -> viewModel.actionButtonStatus=
                                true
                        }
                    }
                }
                else -> {}
            }

        }

    }
}


@Composable
private fun RowScope.RowItem(text: String) {
    Box(
        modifier=Modifier
            .weight(1.0f)
            .width(IntrinsicSize.Max)
    ) {
        Text(
            text=text,
            color = colorResource(id=R.color.text_light),
            textAlign=TextAlign.Center,
            modifier=Modifier
                .align(Alignment.Center)
        )
    }
}


