package com.example.fserv.ui.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fserv.R
import com.example.fserv.model.Event
import com.example.fserv.model.SearchType
import com.example.fserv.model.SortType
import com.example.fserv.ui.controls.*
import com.example.fserv.view_models.EventsViewModel

/*
    search:
        - by name (text field with suggestions list);
        - by location;
        - by tags.

    sorting:
        - by date
 */




private lateinit var dialogState: MutableState<Boolean>
private lateinit var viewModel: EventsViewModel

@Preview(showBackground = true)
@Composable
fun EventsPagePreview(){
    EventsPage(navController = null)
}

@Composable
fun EventsPage(navController: NavController?){
    viewModel = viewModel()
    val events = viewModel.getEvents().collectAsLazyPagingItems()
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

                if (dialogState.value) {
                    Dialog(
                        onDismissRequest = { dialogState.value = false },
                        content = {
                            CompleteDialogContent("I am title", dialogState, "OK") { BodyContent() }
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true
                        )
                    )
                }

                SearchPanel(viewModel.searchTerm,
                    { viewModel.updateSearch(it) })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    SearchBlock()
                    SortBlock()
                }


                LazyColumn {

                    items(events){event ->
                        if (event != null) {
                            EventCard(event = event)
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
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun EventCard(event: Event){
    val context = LocalContext.current
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(
                horizontal = 16.dp ,
                vertical = 8.dp
            )
            .clickable {
                Toast
                    .makeText(
                        context ,
                        "toast" ,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            },

    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = value,
                maxLines = 1,
                onValueChange = { onChange(it) },
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = { dialogState.value = true },
                content = {
                    Icon(
                        Icons.Filled.List,
                        "",
                        modifier = Modifier
                            .size(36.dp)
                    )
                },
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .padding(5.dp)
            )
        }

}







@Composable
fun SearchBlock(){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            var expanded by remember { mutableStateOf(false) }

            TextButton(
                onClick = { expanded = true } ,
                modifier = Modifier.padding(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.search_by) + " " + stringResource(id = viewModel.searchType.typeName) ,
                    color = Color.Black
                )
                DropdownMenu(
                    expanded = expanded ,
                    onDismissRequest = { expanded = false }) {
                    SearchType.values().forEachIndexed { index , searchType ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.updateSearchType(searchType)
                                expanded = false
                            }
                        ) {
                            Text(text = stringResource(id = searchType.typeName))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortBlock(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var expanded by remember { mutableStateOf(false) }
        var sortType: Int by remember { mutableStateOf(SortType.values().first().typeName) }
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.padding(0.dp)
        ){
            Text(
                text = stringResource(id = R.string.sort_by) + " " + stringResource(id = sortType),
                color = Color.Black
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                SortType.values().forEachIndexed { index, s ->
                    DropdownMenuItem(
                        onClick = {
                            sortType = s.typeName
                            expanded = false
                        }
                    ) {
                        Text(text = stringResource(id = s.typeName))
                    }
                }
            }
        }
    }
}


