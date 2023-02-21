package com.example.fserv.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fserv.api.DataRepository
import com.example.fserv.model.Event
import com.example.fserv.model.SearchOptions
import com.example.fserv.model.SearchType
import com.example.fserv.utils.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

private const val TAG = "EventsViewModel"
class EventsViewModel : ViewModel() {
    private val preferencesRepository = PreferencesRepository.get()

    val dataRepository: DataRepository = DataRepository.get()

    var searchType by mutableStateOf(SearchType.Name)

    var searchTerm by mutableStateOf("")

    var names by mutableStateOf(listOf<String>("1", "2"))

    fun updateSearch(newSearchTerm: String) {
        searchTerm = newSearchTerm
        makeSearch()
    }

    fun getEvents(): Flow<PagingData<Event>> =
        dataRepository.searchEvents(
            SearchOptions(
                type = searchType,
                searchTerm = searchTerm,
                userID = dataRepository.getUserId()
            )
        ).cachedIn(viewModelScope)




    private fun makeSearch(){
        Log.d(TAG, dataRepository.getUserId())

        if (searchTerm.length > 2){
            Log.d(TAG, "make search")

            viewModelScope.launch {
                Log.d(TAG, "make launch")

                preferencesRepository.userID.collectLatest {

                    userID ->
                    Log.d(TAG, userID)

                }

            }
        }
    }

    fun updateSearchType(searchType: SearchType) {
        this.searchType = searchType
        makeSearch()
    }


}