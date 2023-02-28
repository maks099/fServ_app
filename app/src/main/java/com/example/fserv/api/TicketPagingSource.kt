package com.example.fserv.api

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.fserv.model.app.SearchOptions
import com.example.fserv.model.server.Event
import com.example.fserv.model.server.Ticket


private const val TAG = "FlickrResponsePagingSource"

class TicketPagingSource(private val api: Api , val clientId: String , private val eventId: String) :
    PagingSource<Int , Ticket>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int , Ticket> {
        return try {
            val page = params.key ?: 0
            val response = api.getTicketsByEvent(
                eventId = eventId,
                clientId = clientId,
                page = page
            )

            LoadResult.Page(
                data = response.tickets,
                prevKey = if (page == 0) null else page.minus(1),
                nextKey = if (response.tickets.isEmpty()) null else page.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int , Ticket>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
