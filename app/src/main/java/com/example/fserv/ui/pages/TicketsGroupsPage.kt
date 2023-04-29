package com.example.fserv.ui.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fserv.R
import com.example.fserv.model.app.DownloadType
import com.example.fserv.model.server.TicketGroup
import com.example.fserv.ui.SubmitButton
import com.example.fserv.ui.controls.HorizontalNumberPicker
import com.example.fserv.ui.controls.dialogs.ConfirmationDialog
import com.example.fserv.view_models.TicketsGroupsListViewModel


@Composable
fun TicketsGroups(navController: NavController,viewModel: TicketsGroupsListViewModel) {
    var errorAlertIsVisible by remember { mutableStateOf(false) }
    var confirmAlertIsVisible by remember { mutableStateOf(false) }
    val ticketCount: MutableState<Int> = rememberSaveable {
        mutableStateOf(1)
    }
    var selectedValue by rememberSaveable { mutableStateOf(viewModel.ticketsGroups[0]) }
    val toPay=ticketCount.value * selectedValue.price


    val scrollState=rememberScrollState()
    Scaffold { paddingValues ->

        Column(
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
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
        ) {
            Scena(viewModel.event._id)

            Text(
                text = stringResource(id=R.string.pick_ticket_type),
                color = colorResource(R.color.text_light),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h3,
                modifier = Modifier.padding(12.dp)
            )

            Column(
                modifier =Modifier
                    .padding(horizontal=16.dp)
                    .clip(RoundedCornerShape(16.dp))

                    .background(colorResource(id=R.color.text_light).copy(0.25f))
                    .clip(RoundedCornerShape(16.dp))
            ) {

                viewModel.ticketsGroups.forEach { ticketGroup ->

                    TicketGroupRow(
                        ticketGroup,
                        {
                            selectedValue=it
                            if (selectedValue.count == 0) {
                                ticketCount.value=0

                            } else {
                                ticketCount.value=1

                            }
                        },
                        { selectedValue._id == it._id }
                    )
                }
            }





            HorizontalNumberPicker(
                min=0,
                max=selectedValue.count,
                default=ticketCount,
                onValueChange={
                    if (it > 0) ticketCount.value=it
                    else ticketCount.value=1
                }
            )

            SubmitButton(
                text = "${stringResource(id=R.string.to_pay)} $toPay ${stringResource(R.string.currency_short)}",
                onClick={

                    if (toPay > viewModel.account) {
                        errorAlertIsVisible=true

                    } else {
                        confirmAlertIsVisible=true
                    }
                },
                enabled=selectedValue.count > 0
            )

            if (errorAlertIsVisible) {
                ErrorDialog(onDismiss={ errorAlertIsVisible=false })
            }
            if (confirmAlertIsVisible) {
                ConfirmationDialog(
                    question=R.string.payment_confirmation,
                    onDismiss={ confirmAlertIsVisible=false },
                    onConfirm={
                        confirmAlertIsVisible=false
                        viewModel.buyTickets(
                            ticketCount.value,
                            selectedValue._id
                        )

                    }
                )
            }
        }
        when (viewModel.transactionStatus) {
            DownloadType.SUCCESS -> {
                navController.popBackStack()
                navController.navigate("tickets_list_page/${viewModel.event._id}/${viewModel.event.name}") {
                    launchSingleTop=true
                }
            }
            DownloadType.FAIL -> Log.d(
                "WWW",
                "fail"
            )
            DownloadType.PREVIEW -> {}
        }
    }
}

@Composable
private fun Scena(
    scenaId: String
){
    Column(
        modifier = Modifier
            .padding(horizontal=16.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier =Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(id=R.string.scena),
                color = colorResource(R.color.text_light),
                style = MaterialTheme.typography.h3,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier =Modifier
                    .size(20.dp)
                    .border(
                        1.dp,
                        Color.Black,
                        RoundedCornerShape(5.dp)
                    )
                    .background(Color(android.graphics.Color.parseColor("#D2691E")))
                    .padding(8.dp)
                )
                Text(
                    stringResource(id=R.string.scena_obj),
                    color = colorResource(R.color.text_light),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )

            }
        }

        val imagePath="https://fserv.onrender.com/scena/" + scenaId
        println(imagePath)
        AsyncImage(
            model=ImageRequest.Builder(LocalContext.current)
                .data(imagePath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .crossfade(true).build(),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier =Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(10.dp))
                .height(260.dp)
                .fillMaxSize(),
        )
    }

}

@Composable
private fun TicketGroupRow(
    ticketGroup: TicketGroup,
    onClick: (TicketGroup) -> Unit,
    isSelectedItem: (TicketGroup) -> Boolean
) {
    Row(
        verticalAlignment=Alignment.CenterVertically,
        modifier=Modifier
            .selectable(
                selected=isSelectedItem(ticketGroup),
                onClick={ onClick(ticketGroup) },
                role=Role.RadioButton
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelectedItem(ticketGroup))
                    colorResource(id=R.color.action_dark)
                else
                    Color(android.graphics.Color.parseColor(ticketGroup.color))
            )
            .padding(12.dp),


        horizontalArrangement=Arrangement.SpaceBetween
    ) {
        Text(
            text = ticketGroup.name,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h3,
            color = colorResource(id=R.color.text_light)
        )
        Text(
            text="${ticketGroup.price} ${stringResource(id=R.string.currency_short)}",
            color = colorResource(id=R.color.text_light)
        )
    }
}

@Composable
private fun ErrorDialog(
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest={ onDismiss() },
        title = {
            Text(
                text = stringResource(id = R.string.error),
                style = MaterialTheme.typography.h3,
                color = colorResource(id=R.color.text_light)
            )
        },
        backgroundColor = colorResource(id=R.color.action_orange).copy(alpha=0.925f),
        text= {
            Text(
                text=stringResource(id=R.string.not_enough_funds),
                color = colorResource(id=R.color.text_light),
                style = MaterialTheme.typography.body1,
            )
        },


        confirmButton={
            TextButton(
                onClick={ onDismiss() },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = colorResource(id=R.color.action_dark),
                    disabledContentColor = Color.LightGray
                ),
            )
            { Text(text="OK") }
        },

    )
}
