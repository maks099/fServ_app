package com.example.fserv.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fserv.api.DataRepository
import com.example.fserv.utils.PreferencesRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call

class SplashViewModel: ViewModel() {

    private val preferencesRepository = PreferencesRepository.get()
    var isUserLogged by mutableStateOf(false)
    var isFinish by mutableStateOf(false)

    fun checkUser() {
        viewModelScope.launch {
            preferencesRepository.userID.collectLatest {
                userId ->
                DataRepository.get().updateUserId(userId)
                isUserLogged = userId.isNotEmpty()
                isFinish = true
            }
        }
    }


}