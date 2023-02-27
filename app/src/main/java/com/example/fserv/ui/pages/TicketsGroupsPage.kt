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
import com.example.fserv.model.server.TicketGroup
import com.example.fserv.ui.controls.HorizontalNumberPicker
import com.example.fserv.view_models.TicketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update


val groups = listOf<TicketGroup>(
    TicketGroup(
        "1",
        "first group",
        40,
        10,
    "id"
    ),
    TicketGroup(
        "2",
        "second group",
        20,
        20,
        "id"
    ),
    TicketGroup(
        "3",
        "third group",
        30,
        3,
        "id"
    ),
    TicketGroup(
        "4",
        "fourth group",
        50,
        1,
        "id"
    ),
)

@Preview(showSystemUi = true)
@Composable
fun TicketGroupPreview(){
    TicketsGroups(
        navController = rememberNavController() ,
        viewModel = TicketViewModel(groups)
    )
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TicketsGroups(navController: NavController, viewModel: TicketViewModel){


    var errorAlertIsVisible by remember { mutableStateOf(false) }
    var confirmAlertIsVisible by remember { mutableStateOf(false) }
    val ticketCount: MutableState<Int> = rememberSaveable {
        mutableStateOf(1)
    }
    var selectedValue by rememberSaveable { mutableStateOf(viewModel.ticketsGroups[0]) }



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
                        { selectedValue = it
                            ticketCount.value = 1},
                        { selectedValue._id == it._id}
                    )
                }
            }

            HorizontalNumberPicker(
                min = 1 ,
                max = selectedValue.count,
                default = ticketCount ,
                onValueChange = {
                    ticketCount.value = it
                }
            )
            val toPay = ticketCount.value * selectedValue.price
            val clientAccount = DataRepository.get().getClient().account
            Button(onClick = {
                if (toPay > clientAccount) {
                    errorAlertIsVisible = true

                } else {
                    confirmAlertIsVisible = true
                }
            }) {

                Text(text = stringResource(id = R.string.to_pay) + toPay)
            }
            if (errorAlertIsVisible) {
                ErrorDialog(onDismiss = { errorAlertIsVisible = false })
            }
            if (confirmAlertIsVisible) {
                ConfirmationDialog(
                    onDismiss = { confirmAlertIsVisible = false } ,
                    onConfirm = {
                        confirmAlertIsVisible = false

                    }
                )
            }
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

@Composable
private fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var seconds by remember {
        mutableStateOf(5) // default value = 1 sec
    }

    var isEnabled by remember {
        mutableStateOf(false) // default value = 1 sec
    }
    LaunchedEffect(key1 = Unit, block = {
        while (seconds > 0){
            delay(1000)
            seconds--
        }
        isEnabled = true
    })
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                },
                enabled = isEnabled
            )
            { if(!isEnabled) Text(text = "OK $seconds") else Text(text = "OK") }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },

        title = { Text(text = stringResource(id = R.string.confirmation)) },
        text = { Text(text = stringResource(id = R.string.payment_confirmation)) }
    )
}