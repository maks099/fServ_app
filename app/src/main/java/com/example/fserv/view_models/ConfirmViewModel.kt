package com.example.fserv.view_models

import androidx.compose.material.SnackbarResult
import androidx.lifecycle.ViewModel
import com.example.fserv.R
import com.example.fserv.api.DataRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmViewModel(): ViewModel() {

    private val dataRepository: DataRepository = DataRepository()


    fun confirmAccount(token: String): Call<String> {
        return dataRepository.confirmAccount(token)
    }


}