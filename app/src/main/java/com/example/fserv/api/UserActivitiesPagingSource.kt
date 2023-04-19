package com.example.fserv.api

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.fserv.model.server.UserActivity
import com.example.fserv.model.server.UserActivityResponse



private const val TAG = "FlickrResponsePagingSource"

class UserActivitiesPagingSource(private val api: Api) :
    PagingSource<Int, UserActivity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserActivity> {
        return try {

            val page = params.key ?: 0
            val response: UserActivityResponse =  api.getActivities(
                clientId = DataRepository.get().userId,
                page = page
            )

            Log.d("ActivityPagingSource", "page $page" )
            Log.d(TAG, response.toString())

            LoadResult.Page(
                data = response.tickets,
                prevKey = if (page == 0) null else page.minus(1),
                nextKey = if (response.tickets.isEmpty()) null else page.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int , UserActivity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
