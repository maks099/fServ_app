package com.example.fserv.api

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.EventResponse
import com.example.fserv.model.server.myInfo
import com.example.fserv.model.server.myInfoResponse



private const val TAG = "FlickrResponsePagingSource"

class CustomPagingSource(private val api: Api) :
    PagingSource<Int, Event>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Event> {
        return try {

            val page = params.key ?: 0
            val response: EventResponse =  api.getActivities(
                clientId = DataRepository.get().getClient()._id,
                page = page
            )

            Log.d("ActivityPagingSource", "page $page" )
            Log.d(TAG, response.toString())

            LoadResult.Page(
                data = response.events,
                prevKey = if (page == 0) null else page.minus(1),
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
