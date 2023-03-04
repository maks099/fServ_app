package com.example.fserv.view_models

// for a 'val' variable

// for a `var` variable also add

// or just
import android.util.Log
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
import com.example.fserv.model.server.NotificationResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


private const val TAG = "EventsViewModel"
class EventsListViewModel : ViewModel() {
    var isRefreshing = MutableStateFlow(false)
    private val dataRepository: DataRepository = DataRepository.get()

    var searchType by mutableStateOf(SearchType.Name)
    var sortType by mutableStateOf(SortType.DateAscending)

    var filters by mutableStateOf(FilterType.values().asList())

    var searchTerm by mutableStateOf("")
    var categories by mutableStateOf(Category.values())
    var pickedCategory by mutableStateOf(Category.All)




    fun updateSearch(newSearchTerm: String) = { -> searchTerm = newSearchTerm }

    fun getEvents(): Flow<PagingData<Event>> {
        Log.d("ClientID", dataRepository.userId)
        return dataRepository.searchEvents(
            SearchOptions(
                searchType = searchType,
                searchTerm = searchTerm,
                pickedCategory = pickedCategory,
                filters = filters,
                sortType = sortType,
                userID = dataRepository.userId
            )
        ).cachedIn(viewModelScope)
    }






    fun updatePickedCategory(category: Category) {
        this.pickedCategory = category
        val copy = categories.clone()
        copy.forEach {
            it.status = category.code == it.code
        }
        categories = copy


    }

    companion object {
        private var INSTANCE: EventsListViewModel? = null
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = EventsListViewModel()
            }
        }
        fun get(): EventsListViewModel {
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }


}
