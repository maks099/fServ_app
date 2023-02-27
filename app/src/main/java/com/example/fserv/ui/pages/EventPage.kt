package com.example.fserv.ui.pages

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fserv.R
import com.example.fserv.model.app.DownloadType
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.TicketsGroupsContainer
import com.example.fserv.ui.controls.AddressControl
import com.example.fserv.ui.controls.ImageSlider
import com.example.fserv.ui.controls.SimpleTags
import com.example.fserv.view_models.EventViewModel
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Preview(showSystemUi = true)
@Composable
fun EventPagePreview(){
    val event = Event(
        _id = "id",
        name = "Name",
        address = "Address",
        gallery = listOf(
            "https://www.w3schools.com/howto/img_forest.jpg",
            "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg",
            "https://images.ctfassets.net/hrltx12pl8hq/5596z2BCR9KmT1KeRBrOQa/4070fd4e2f1a13f71c2c46afeb18e41c/shutterstock_451077043-hero1.jpg"
        ),
        latitude = 48.621025,
        longitude =  22.288229,
        isPaid = false,
        organizerName = "organizer",
        date = "2023-02-23T22:46:00.000+00:00",
        tags = listOf("concert", "rock", "heavy metal", "powerwolf", "gocha bee"),
        description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
    )
    EventPage(navController = rememberNavController(), event = event)

}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EventPage(navController: NavController, event: Event){
    val viewModel = EventViewModel()
    val scrollState = rememberScrollState()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(
                    color = Color.Transparent ,
                ) ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = event.name ,
                style = TextStyle(
                    fontSize = 24.sp ,

                    ) ,
                modifier = Modifier
                    .padding(15.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween ,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                val configuration = LocalConfiguration.current
                val currentLocale = ConfigurationCompat.getLocales(configuration).get(0)
                RowItem(
                    text = "${stringResource(id = R.string.organizer)}\r\n${event.organizerName}"
                )
                RowItem(
                    text = "${stringResource(id = R.string.date)}\r\n${
                        parseJSDate(
                            event.date,
                            currentLocale
                        )
                    }"
                )
            }

            ImageSlider(urlList = event.gallery)
            Text(
                text = event.description,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .padding(15.dp)
            )
            FlowRow {
                event.tags.forEach {
                        tag ->
                    if(tag.trim().isNotEmpty()){
                        SimpleTags(
                            text = tag,
                            isActive = false,
                            textStyle = TextStyle(
                                fontSize = 12.sp
                            ),
                            onClick = { }
                        )
                    }

                }
            }
            AddressControl(
                address = event.address ,
                latitude = event.latitude ,
                longitude = event.longitude
            )
            if(event.isPaid){
                Button(
                    onClick = {
                        viewModel.getTickets(event._id)
                    },
                    enabled = viewModel.actionButtonStatus,
                    modifier = Modifier.width(200.dp),
                    elevation = ButtonDefaults.elevation(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    )
                ){
                    Text(stringResource(id = R.string.buy_ticket))
                }
            } else {
                Button(
                    onClick = {},
                    modifier = Modifier.width(200.dp),
                    elevation = ButtonDefaults.elevation(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    )
                ){
                    Text(stringResource(id = R.string.free_entrance))
                }
            }

            when(viewModel.downloadType){
                DownloadType.SUCCESS -> {
                    val listContainer = TicketsGroupsContainer(list = viewModel.ticketsGroups.toList())
                    navController.navigate("tickets_groups/${listContainer}") {
                        launchSingleTop = true
                    }
                }
                DownloadType.FAIL -> {
                    viewModel.downloadType = DownloadType.PREVIEW
                    viewModel.actionButtonStatus = false
                    coroutineScope.launch {
                        val snackRes = scaffoldState.snackbarHostState.showSnackbar(
                            message = viewModel.downloadType.message
                        )
                        when (snackRes) {
                            SnackbarResult.Dismissed , SnackbarResult.ActionPerformed -> viewModel.actionButtonStatus =
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
private fun RowScope.RowItem(text: String){
    Box(
        modifier = Modifier
            .weight(1.0f)
            .width(IntrinsicSize.Max)
            .background(Color.Blue)
    ){
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(15.dp)
        )
    }
}


private fun parseJSDate(dateString: String, locale: Locale?): String{
    Log.d("DATE", dateString)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val odt = OffsetDateTime.parse(dateString)

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", locale);
        dtf.format(odt);
    } else {
        val dateTimeParts = dateString.split("T")
        val datePart = dateTimeParts[0].split("-")
        val dateRes = "${datePart[2]}/${datePart[1]}/${datePart[0]}"

        val timePart = dateTimeParts[1].split(":")
        val timeRes = "${timePart[0]}:${timePart[1]}"

        return "$dateRes $timeRes"
    }

}