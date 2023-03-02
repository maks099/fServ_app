package com.example.fserv.ui.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fserv.R
import com.example.fserv.model.server.UserActivityObj
import com.example.fserv.view_models.ActivitiesListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun AccountPage(
    navController: NavController,
    activityListState: LazyListState,
    activities: LazyPagingItems<UserActivityObj>,
    onUserActivityClick: (UserActivityObj) -> Unit
){
    val viewModel = ActivitiesListViewModel.get()

    val amount = 45

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = stringResource(id = R.string.on_your_account),
                fontSize = 32.sp
            )
            AutoSizeText(
                text ="${viewModel.account}₴",
                textStyle = TextStyle(
                    fontSize = 68.sp
                )
            )
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
        }
        Spacer(modifier = Modifier
            .height(30.dp))

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
                            InfoCard(activity, onUserActivityClick)
                        }

                }
            }
        }
    }
}

@Composable
fun AutoSizeText(
    text: String ,
    textStyle: TextStyle ,
    modifier: Modifier = Modifier
) {
    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style = scaledTextStyle,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle =
                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
private fun InfoCard(
    activityObj: UserActivityObj,
    onUserActivityClick: (UserActivityObj) -> Unit) {
    Log.d("AccountPage", "${activityObj.gallery} ${activityObj._id}$" )
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp) ,
        modifier = Modifier
            .padding(
                horizontal = 16.dp ,
                vertical = 8.dp
            )
            .fillMaxWidth()
            .clickable {
                onUserActivityClick(activityObj)
            },
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 10.dp)
        ){
            val imagePath = "https://fserv.onrender.com/photo/" + activityObj.gallery.first()
            Log.d("TICKET", imagePath)
            Text(
                activityObj.name,
                modifier = Modifier
                    .weight(1f)
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imagePath)
                    .error(R.drawable.placeholder)
                    .crossfade(true)
                    .placeholder(R.drawable.placeholder)
                    .build(),

                contentDescription = stringResource(R.string.event_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

        }
    }

}
