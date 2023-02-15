package com.example.fserv.view_models

import android.R.attr.phoneNumber
import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ApplicationProvider
import com.example.fserv.api.DataRepository
import kotlinx.coroutines.launch


private const val TAG = "LoginViewModel"
public class LoginViewModel(): ViewModel() {

    val dataRepository: DataRepository

    init {
        dataRepository = DataRepository()
    }

    var username by mutableStateOf("")
    var password by mutableStateOf("")

    fun onPasswordChange(password: String){
        this.password = password
    }

    fun onUserNameChange(username: String){
        this.username = username
    }

    fun makeTestRequest(){
        viewModelScope.launch {
            try {
                val items = dataRepository.testFetch()
                Log.d(TAG, "Items received: $items")
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to fetch gallery items", ex)
            }
        }
    }

    fun sendSMS(){
    }



}