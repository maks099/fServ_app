package com.example.fserv.ui.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fserv.R
import com.example.fserv.model.app.FilterType
import com.example.fserv.model.app.SearchType
import com.example.fserv.model.app.SortType
import com.example.fserv.model.server.Event
import com.example.fserv.ui.controls.*
import com.example.fserv.view_models.EventsListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/*
    search:
        - by tags.


 */




private lateinit var dialogState: MutableState<Boolean>
private lateinit var viewModel: EventsListViewModel

@Composable
fun EventsPage(navController: NavController){
    viewModel = viewModel()

    dialogState = remember {
        mutableStateOf(false)
    }

    Scaffold (
        topBar = {
            TopAppBar {
                Text("App bar")
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
                SearchPanel(viewModel.searchTerm,
                    { viewModel.updateSearch(it) })


                LazyRow {
                   items(viewModel.categories){
                       category ->
                       SimpleTags(
                           text = stringResource(id = category.tagName),
                           isActive = category.status,
                           onClick = {
                               viewModel.updatePickedCategory(category)

                           }
                       )
                   }


                }


                val events = viewModel.getEvents().collectAsLazyPagingItems()
                val isRefreshing by viewModel.isRefreshing.collectAsState()

                //Log.d(TAG, "events are downloaded ${events.itemCount}")

                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    onRefresh = { events.refresh() },
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {


                    LazyColumn (
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            ){

                        items(events) { event ->
                            if (event != null) {
                                EventCard(event = event, navController = navController)
                            }
                        }

                        events.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item { LoadingView(modifier = Modifier.fillParentMaxSize()) }
                                }
                                loadState.append is LoadState.Loading -> {
                                    item { LoadingItem() }
                                }
                                loadState.refresh is LoadState.Error -> {
                                    val e = events.loadState.refresh as LoadState.Error
                                    item {
                                        ErrorItem(
                                            message = e.error.localizedMessage!! ,
                                            modifier = Modifier.fillParentMaxSize() ,
                                            onClickRetry = { retry() }
                                        )
                                    }
                                }
                                loadState.append is LoadState.Error -> {
                                    val e = events.loadState.append as LoadState.Error
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
fun EventCard(event: Event , navController: NavController){
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(
                horizontal = 16.dp ,
                vertical = 8.dp
            )
            .clickable {
                navController.navigate("event_page/${event}") {
                    launchSingleTop = true
                }
            },

    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Log.d(TAG, "${event.name} ")
            val imagePath = "https://fserv.onrender.com/photo/" + event.gallery.first()
            Log.d(TAG, imagePath)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imagePath)
                    .error(R.drawable.placeholder)
                    .crossfade(true)
                    .build(),

                contentDescription = stringResource(R.string.event_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Column {
                Text(event.name, modifier = Modifier
                    .fillMaxWidth()
                   )
            }
        }
    }
}

@Composable
fun SearchPanel(

    value: String,
    onChange: (String) -> Unit
){
    Row{
        OutlinedTextField(
            value = value,
            maxLines = 1,
            onValueChange = { onChange(it) },
            modifier = Modifier
                .padding(5.dp)
                .weight(1.0f)
                .fillMaxWidth()
        )
        IconButton(
            onClick = { dialogState.value = true },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_upward_24) ,
                contentDescription = ""
            )
        }
    }
    if (dialogState.value) {

        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                dialogState.value = false
            },
            title = {
                Text(text = "Dialog Title")
            },
            text = {
                Column {
                    SearchBlock()
                    SortBlock()
                    FilterBlock()
                }
            },
            buttons = {}
        )
    }

}








@Composable
fun SearchBlock(){
    Text(text = stringResource(id = R.string.search_by))
    SearchType.values().forEach { item ->
        val isSelectedItem: (SearchType) -> Boolean = { viewModel.searchType == it }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .selectable(
                    selected = isSelectedItem(item) ,
                    onClick = { viewModel.searchType = item } ,
                    role = Role.RadioButton
                )
                .padding(8.dp)
        ) {
            RadioButton(
                selected = isSelectedItem(item),
                onClick = null
            )
            Text(
                text = stringResource(id = item.typeName),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SortBlock(){
    Text(text = stringResource(id = R.string.sort_by))
    SortType.values().forEach { item ->
        val isSelectedItem: (SortType) -> Boolean = { viewModel.sortType == it }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .selectable(
                    selected = isSelectedItem(item) ,
                    onClick = { viewModel.sortType = item } ,
                    role = Role.RadioButton
                )
                .padding(8.dp)
        ) {
            RadioButton(
                selected = isSelectedItem(item),
                onClick = null
            )
            Text(
                text = stringResource(id = item.typeName),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun FilterBlock(){
    Text(text = stringResource(id = R.string.sort_by))
    FilterType.values().forEach { item ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
//                .selectable(
//                    selected = isSelectedItem(item) ,
//                    onClick = { viewModel.sortType = item } ,
//                    role = Role.RadioButton
//                )
                .padding(8.dp)
        ) {
            val isChecked = viewModel.filters.contains(item)

            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                                  if(it){
                                      viewModel.filters += item

                                  } else {
                                      viewModel.filters -= item

                                  }
                                  },
                enabled = true,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Magenta,
                    uncheckedColor = Color.DarkGray,
                    checkmarkColor = Color.Cyan
                )
            )
            Text(text = stringResource(id = item.typeName))
        }
    }
}

