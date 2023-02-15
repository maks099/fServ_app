package com.example.fserv.view_models

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fserv.api.DataRepository
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

private const val TAG = "LoginViewModel"
public class RegisterViewModel(): ViewModel() {

    val dataRepository: DataRepository

    var loginErrMsg = ""
    var passwordErrMsg = ""
    var loginError = false
    var passwordError = false


    init {
        dataRepository = DataRepository()
    }

    var username by mutableStateOf("")
    var password by mutableStateOf("")

    fun onPasswordChange(password: String){
        this.password = password
        if(password.length < 8){
            passwordError = true
            passwordErrMsg = "password is less 8 symbols"
        }
        else if (!password.contains("[0-9]".toRegex())) {
            passwordError = true
            passwordErrMsg = "password don`t has a number"
        }
        else if (!password.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            passwordError = true
            passwordErrMsg = "password don`t has a special symbol"
        }

        else {
            passwordError = false
            passwordErrMsg = ""
        }
    }

    fun onUserNameChange(username: String){
        this.username = username
        if(username.length < 5){
            loginError = true
            loginErrMsg = "Minimal length of login is 5 symbols"
        } else {
            loginError = false
            loginErrMsg = ""
        }
    }


    fun validate(): Boolean {
        if((username.isNotEmpty() && password.isNotEmpty())){
            return (!loginError && !passwordError)
        } else {
            return false
        }
    }

    fun registerNewUser(){
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Log.d(TAG, hashedPassword)
        // TODO: api interactions
    }



}