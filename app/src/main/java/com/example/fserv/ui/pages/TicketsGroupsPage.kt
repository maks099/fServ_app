package com.example.fserv.ui.pages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fserv.R
import com.example.fserv.api.DataRepository
import com.example.fserv.model.app.DownloadType
import com.example.fserv.model.server.TicketGroup
import com.example.fserv.ui.controls.HorizontalNumberPicker
import com.example.fserv.ui.controls.dialogs.ConfirmationDialog
import com.example.fserv.view_models.TicketsGroupsListViewModel
import kotlinx.coroutines.delay




@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TicketsGroups(navController: NavController, viewModel: TicketsGroupsListViewModel){


    var errorAlertIsVisible by remember { mutableStateOf(false) }
    var confirmAlertIsVisible by remember { mutableStateOf(false) }
    val ticketCount: MutableState<Int> = rememberSaveable {
        mutableStateOf(1)
    }
    var selectedValue by rememberSaveable { mutableStateOf(viewModel.ticketsGroups[0]) }
    val toPay = ticketCount.value * selectedValue.price



    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally ,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            LazyColumn(
                modifier = Modifier
                    .padding(10.dp)
                    .border(
                        1.dp ,
                        Color.Gray ,
                        RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp))
                //  verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {



                items(viewModel.ticketsGroups) {ticketGroup ->
                    Spacer(
                        modifier = Modifier
                            .height(0.5.dp)
                            .background(Color.Gray)
                            .fillMaxWidth()
                    )
                    TicketGroupRow(
                        ticketGroup,
                        {
                            selectedValue = it
                            if(selectedValue.count == 0){
                                ticketCount.value = 0

                            } else {
                                ticketCount.value = 1

                            }
                        },
                        { selectedValue._id == it._id}
                    )
                }
            }

            HorizontalNumberPicker(
                min = 0,
                max = selectedValue.count,
                default = ticketCount ,
                onValueChange = {
                    if(it > 0) ticketCount.value = it
                    else ticketCount.value = 1
                }
            )

            Button(onClick = {
                             Log.d("ACCOUNT", viewModel.account.toString())

                if (toPay > viewModel.account) {
                    errorAlertIsVisible = true

                } else {
                    confirmAlertIsVisible = true
                }
            },
            enabled = selectedValue.count > 0) {

                Text(text = stringResource(id = R.string.to_pay) + toPay)
            }
            if (errorAlertIsVisible) {
                ErrorDialog(onDismiss = { errorAlertIsVisible = false })
            }
            if (confirmAlertIsVisible) {
                ConfirmationDialog(
                    question = R.string.payment_confirmation,
                    onDismiss = { confirmAlertIsVisible = false } ,
                    onConfirm = {
                        confirmAlertIsVisible = false
                        viewModel.buyTickets(ticketCount.value, selectedValue._id)

                    }
                )
            }
        }
        when(viewModel.transactionStatus){
            DownloadType.SUCCESS -> {
                navController.popBackStack()
                navController.navigate("tickets_list_page/${viewModel.event._id}") {
                    launchSingleTop = true
                }
            }
            DownloadType.FAIL -> Log.d("WWW", "fail")
            DownloadType.PREVIEW -> { }
        }
    }
}

@Composable
private fun TicketGroupRow(
    ticketGroup: TicketGroup,
    onClick: (TicketGroup) -> Unit,
    isSelectedItem: (TicketGroup) -> Boolean){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .selectable(
                selected = isSelectedItem(ticketGroup) ,
                onClick = { onClick(ticketGroup) } ,
                role = Role.RadioButton
            )
            .fillMaxWidth()
            .background(if (isSelectedItem(ticketGroup)) Color.Blue else Color.White)
            .padding(
                horizontal = 10.dp ,
                vertical = 20.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = ticketGroup.name
        )
        Text(
            text = "${ticketGroup.price}"
        )
    }
}

@Composable
private fun ErrorDialog(
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() })
            { Text(text = "OK") }
        },
       
        title = { Text(text = stringResource(id = R.string.error)) },
        text = { Text(text = stringResource(id = R.string.not_enough_funds)) }
    )
}
