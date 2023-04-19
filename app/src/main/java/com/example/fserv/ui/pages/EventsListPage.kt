package com.example.fserv.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
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

private lateinit var dialogState: MutableState<Boolean>
private lateinit var viewModel: EventsListViewModel
private lateinit var loadingAction: MutableState<Boolean>
@Composable
fun EventsPage(
    navController: NavController,
    state: LazyListState,
    events: LazyPagingItems<Event>,
    onEventCardClick: (Event) -> Unit
) {
    viewModel=EventsListViewModel.get()
    loadingAction = remember { mutableStateOf(false) }
    dialogState=remember {
        mutableStateOf(false)
    }

    Scaffold(
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
                )
        ) {
            Column {
                SearchPanel(viewModel.searchTerm)
                if(loadingAction.value){
                    DialogBoxLoading()
                }
                LazyRow(
                    modifier=Modifier.padding(
                            horizontal=2.dp,
                            vertical=1.dp
                        )
                ) {
                    items(viewModel.categories) { category ->
                        SimpleTags(text=stringResource(id=category.tagName),
                            isActive=category.status,
                            onClick={
                                viewModel.updatePickedCategory(category)
                            })
                    }
                }
                val isRefreshing by viewModel.isRefreshing.collectAsState()


                SwipeRefresh(
                    state=rememberSwipeRefreshState(isRefreshing),
                    onRefresh={ events.refresh() },
                    modifier=Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {


                    LazyColumn(
                        state=state,
                        modifier=Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ) {

                        items(events) { event ->
                            if (event != null) {
                                EventCard(event=event,
                                    onEventCardClick={
                                        onEventCardClick(event)
                                    })
                            }
                        }

                        events.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item { LoadingView(modifier=Modifier.fillParentMaxSize()) }
                                }
                                loadState.append is LoadState.Loading -> {
                                    item { LoadingItem() }
                                }
                                loadState.refresh is LoadState.Error -> {
                                    val e=events.loadState.refresh as LoadState.Error
                                    item {
                                        ErrorItem(message=e.error.localizedMessage!!,
                                            modifier=Modifier.fillParentMaxSize(),
                                            onClickRetry={ retry() })
                                    }
                                }
                                loadState.append is LoadState.Error -> {
                                    val e=events.loadState.append as LoadState.Error
                                    item {
                                        ErrorItem(message=e.error.localizedMessage!!,
                                            onClickRetry={ retry() })
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
fun EventCard(
    event: Event,onEventCardClick: (Event) -> Unit
) {
    Card(
        shape=RoundedCornerShape(10.dp),
        modifier=Modifier
            .padding(
                horizontal=dimensionResource(id=R.dimen.card_padding),
                vertical=dimensionResource(id=R.dimen.card_padding)
            )
            .clickable {
                loadingAction.value=true
                onEventCardClick(event)
            },
        backgroundColor=colorResource(id=R.color.text_light).copy(alpha = 0.925f)

    ) {
        Row(
            verticalAlignment=Alignment.CenterVertically,
            modifier=Modifier.padding(8.dp)
        ) {
            val imagePath="https://fserv.onrender.com/photo/" + event.gallery.first()
            AsyncImage(
                model=ImageRequest.Builder(LocalContext.current)
                    .data(imagePath)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .crossfade(true).build(),

                contentDescription=stringResource(R.string.event_image),
                contentScale=ContentScale.Crop,
                modifier=Modifier
                    .size(125.dp)
                    .padding(dimensionResource(id=R.dimen.small_padding))
                    .clip(RoundedCornerShape(10.dp))
            )
            Column {
                Text(
                    event.name,
                    style=MaterialTheme.typography.h3,
                    color=colorResource(id=R.color.action_dark),
                    modifier=Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                )

                val currentLocale=ConfigurationCompat.getLocales(LocalConfiguration.current).get(0)

                Text(
                    text="" + currentLocale?.let { event.getParsedDate(it) },
                    style=MaterialTheme.typography.subtitle1,
                    color=colorResource(id=R.color.action_dark),
                    modifier=Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                )

                Text(
                    text=event.address,
                    color=colorResource(id=R.color.action_dark),
                    style=MaterialTheme.typography.subtitle1,
                    modifier=Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SearchPanel(
    value: String
) {
    Row(
        verticalAlignment=Alignment.CenterVertically,
        modifier=Modifier.padding(
                horizontal=2.dp,
                vertical=6.dp
            )
    ) {
        OutlinedTextField(
            value=value,
            onValueChange={ newText -> viewModel.updateSearch(newText) },
            maxLines=1,
            colors=TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor=colorResource(id=R.color.text_light),
                unfocusedBorderColor=colorResource(id=R.color.text_light),
                placeholderColor=colorResource(id=R.color.text_light),
                textColor=colorResource(id=R.color.text_light)
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier=Modifier
                        .width(28.dp)
                        .height(28.dp)
                )
            },

            modifier=Modifier
                .padding(horizontal=2.dp)
                .weight(1.0f)
                .fillMaxWidth(),

        )
        IconButton(
            onClick={ dialogState.value=true },
        ) {
            Icon(
                painter=painterResource(id=R.drawable.baseline_filter_alt_24),
                contentDescription=stringResource(id=R.string.filter),
                modifier=Modifier
                    .width(36.dp)
                    .height(36.dp)
            )
        }
    }
    if (dialogState.value) {

        AlertDialog(
            modifier=Modifier
                .padding(16.dp),
            onDismissRequest={ dialogState.value=false },

            title={
                Text(
                    text=stringResource(id=R.string.search_and_filter),
                    style = MaterialTheme.typography.h5,
                    color = colorResource(id=R.color.text_light)
                )
            },
            backgroundColor = colorResource(id=R.color.action_orange).copy(alpha=0.925f),
            text={
                Column(
                    horizontalAlignment=Alignment.CenterHorizontally
                ) {
                    SearchBlock()
                    SortBlock()
                    FilterBlock(
                        isCheckedFunc={ viewModel.filters.contains(it) },
                        addFilter={ viewModel.filters+=it },
                    )
                }
            },
            buttons={})
    }

}


@Composable
fun SearchBlock() {
    Column(
        modifier =Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text=stringResource(id=R.string.search_by),
            style = MaterialTheme.typography.body1,
            color = colorResource(id=R.color.text_light)
        )
        SearchType.values().forEach { item ->
            val isSelectedItem: (SearchType) -> Boolean={ viewModel.searchType == it }
            Row(
                verticalAlignment=Alignment.CenterVertically,
                modifier=Modifier
                    .selectable(
                        selected=isSelectedItem(item),
                        onClick={ viewModel.searchType=item },
                        role=Role.RadioButton
                    )
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected=isSelectedItem(item),
                    onClick=null
                )
                Text(
                    text=stringResource(id=item.typeName),
                    style = MaterialTheme.typography.body1,
                    color = colorResource(id=R.color.text_light),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }

}

@Composable
fun SortBlock() {
    Column(
        modifier =Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text=stringResource(id=R.string.sort_by),
            style = MaterialTheme.typography.body1,
            color = colorResource(id=R.color.text_light)
        )
        SortType.values().forEach { item ->
            val isSelectedItem: (SortType) -> Boolean={ viewModel.sortType == it }
            Row(
                verticalAlignment=Alignment.CenterVertically,
                modifier=Modifier
                    .selectable(
                        selected=isSelectedItem(item),
                        onClick={ viewModel.sortType=item },
                        role=Role.RadioButton
                    )
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected=isSelectedItem(item),
                    onClick=null
                )
                Text(
                    text=stringResource(id=item.typeName),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.body1,
                    color = colorResource(id=R.color.text_light)
                )
            }
        }
    }

}


@Composable
fun FilterBlock(
    isCheckedFunc: (FilterType) -> Boolean,addFilter: (FilterType) -> Unit
) {
    Column(
        modifier =Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text=stringResource(id=R.string.sort_by),
            style = MaterialTheme.typography.body1,
            color = colorResource(id=R.color.text_light)
        )
        FilterType.values().forEach { item ->
            Row(
                verticalAlignment=Alignment.CenterVertically,
                modifier=Modifier
                    .padding(8.dp)
            ) {
                val isChecked=isCheckedFunc(item)

                Checkbox(
                    checked=isChecked,
                    onCheckedChange={
                        if (it) {
                            addFilter(item)

                        } else {
                            viewModel.filters-=item

                        }
                    },
                    colors=CheckboxDefaults.colors(
                        checkmarkColor=Color.Black
                    )
                )
                Text(
                    text=stringResource(id=item.typeName),
                    style = MaterialTheme.typography.body1,
                    color = colorResource(id=R.color.text_light)
                )
            }
        }
    }

}

