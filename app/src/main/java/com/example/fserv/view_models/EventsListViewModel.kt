package com.example.fserv.view_models

// for a 'val' variable

// for a `var` variable also add

// or just
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fserv.api.DataRepository
import com.example.fserv.model.app.*
import com.example.fserv.model.server.Client
import com.example.fserv.model.server.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.Type


private const val TAG = "EventsViewModel"
class EventsListViewModel : ViewModel() {
    var isRefreshing = MutableStateFlow(false)
    val dataRepository: DataRepository = DataRepository.get()

    var searchType by mutableStateOf(SearchType.Name)
    var sortType by mutableStateOf(SortType.DateAscending)

    var filters by mutableStateOf(FilterType.values().asList())

    var searchTerm by mutableStateOf("")
    var categories by mutableStateOf(Category.values())
    var pickedCategory by mutableStateOf(Category.All)


    init {
        dataRepository.getClientFromServer().enqueue(
            object : retrofit2.Callback<String> {
                override fun onResponse(call: Call<String> , response: Response<String>) {
                    val collectionType: Type = object : TypeToken<List<Client?>?>() {}.type
                    val responseTypedList: List<Client> = Gson()
                        .fromJson(
                            response.body(),
                            collectionType
                        ) as List<Client>
                    dataRepository.updateClient(responseTypedList[0])
                }

                override fun onFailure(call: Call<String> , t: Throwable) {}
            }

        )
    }

    fun updateSearch(newSearchTerm: String) {
        searchTerm = newSearchTerm
        //makeSearch()

    }

    fun getEvents(): Flow<PagingData<Event>> =
        dataRepository.searchEvents(
            SearchOptions(
                searchType = searchType,
                searchTerm = searchTerm,
                pickedCategory = pickedCategory,
                filters = filters,
                sortType = sortType,
                userID = dataRepository.getClient()._id
            )
        ).cachedIn(viewModelScope)





    fun updatePickedCategory(category: Category) {
        this.pickedCategory = category
        val copy = categories.clone()
        copy.forEach {
            it.status = category.code == it.code
        }
        categories = copy


    }


}
