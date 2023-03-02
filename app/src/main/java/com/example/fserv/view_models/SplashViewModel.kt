package com.example.fserv.view_models

import android.util.Log
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
                val id = userId.replace("\"", "")
                Log.d("CLIENTid", id)
                DataRepository.get().userId = id
                Log.d("CLIENTid22222", DataRepository.get().userId)

                isUserLogged = userId.isNotEmpty()
                isFinish = true
            }
        }
    }


}