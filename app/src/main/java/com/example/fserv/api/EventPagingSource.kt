package com.example.fserv.api

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.fserv.model.Event
import com.example.fserv.model.SearchOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


private val TMDB_STARTING_PAGE_INDEX: Int = 1
const val TAG = "FlickrResponsePagingSource"
enum class REQUEST_TYPE {events, search}



class EventPagingSource(private val api: Api , val options: SearchOptions) :
    PagingSource<Int, Event>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Event> {
        return try {
            val page = params.key ?: 0
            val response = api.getAllEvents(
                options.userID,
                page
            )

            Log.d(TAG, response.toString())


            LoadResult.Page(
                data = response.events,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (response.events.isEmpty()) null else page.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int , Event>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
