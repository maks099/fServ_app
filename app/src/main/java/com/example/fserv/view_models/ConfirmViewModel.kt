package com.example.fserv.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fserv.api.DataRepository
import com.example.fserv.utils.PreferencesRepository
import kotlinx.coroutines.launch
import retrofit2.Call

class ConfirmViewModel: ViewModel() {

    private val dataRepository: DataRepository = DataRepository.get()
    private val preferencesRepository = PreferencesRepository.get()


    fun setUserID(userID: String){
        viewModelScope.launch {
            preferencesRepository.setUserID(userID)
        }
    }

    fun confirmAccount(token: String): Call<String> {
        return dataRepository.confirmAccount(token)
    }


}