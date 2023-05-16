package com.example.fserv.view_models

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fserv.R
import com.example.fserv.api.DataRepository
import com.example.fserv.model.server.Client
import com.example.fserv.utils.CryptLib
import com.example.fserv.utils.PreferencesRepository
import com.example.fserv.utils.getMetaData
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "AuthorizaitonViewModel"
public class AuthorizationViewModel(val application: Application): ViewModel() {

    private val dataRepository: DataRepository = DataRepository.get()
    private val preferencesRepository = PreferencesRepository.get()

    var emailErrorMsg = -1
    var passwordErrMsg = -1

    private val maxEmailLength = 40
    private val maxPasswordLength = 40

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var actionButtonStatus by mutableStateOf(true)
    var dialogStatus by mutableStateOf(false)

    fun onPasswordChange(password: String){
        if(password.length < maxPasswordLength){
            this.password = password
        }

        if (!password.contains("[0-9]".toRegex())) {
            passwordErrMsg = R.string.error_password_number
        }
        else if (!password.contains("[A-Z]".toRegex())) {
            passwordErrMsg = R.string.error_password_uppercase
        }
        else if (!password.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            passwordErrMsg = R.string.error_password_special
        }
        else if(password.length < 8){
            passwordErrMsg = R.string.error_password_length
        }

        else {
            passwordErrMsg = -1
        }
    }

    fun onEmailChange(email: String){
        if(email.length < maxEmailLength){
            this.email = email
        }

        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailErrorMsg = -1
        } else {
            emailErrorMsg = R.string.error_email_correct

        }
    }

    fun registerNewUser(
        onFailure: (Int, String) -> Unit,
        onSuccess: () -> Unit
    ) {
        actionButtonStatus = false

        if(validateEmail() && validatePassword()){
            dialogStatus = true
            dataRepository.registerNewUser(getClient())
                .enqueue(
                    object : Callback<String>{
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            dialogStatus = false
                            when(response.isSuccessful){
                                true -> onSuccess()
                                false -> onFailure(R.string.server_error, response.errorBody()?.string().toString())
                            }
                        }
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            dialogStatus = false
                            onFailure(R.string.error, t.message.toString())
                        }
                    }
                )
        } else onFailure(R.string.error_form, "")
    }

    private fun getClient(): Client{
        return Client(
            email = email,
            password = getEncryptedPassword(),
            firstName = "",
            lastName = ""
        )
    }

    fun loginClient(
        onFailure: (Int, String) -> Unit,
        onSuccess: () -> Unit
    ) {
        actionButtonStatus = false

        if(validateEmail() && validatePassword()){
            dialogStatus = true

            val userInfo = Client(email = email, password = getEncryptedPassword())
            dataRepository.loginClient(userInfo).enqueue(
                object : Callback<String>{
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        dialogStatus = false

                        when(response.isSuccessful){
                            true -> {
                                response.body()?.let { setUserID(it) }
                                onSuccess()
                            }
                            false -> onFailure(R.string.server_error, response.errorBody()?.string().toString())
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        dialogStatus = false
                        onFailure(R.string.error, t.message.toString())
                    }
                }
            )
        } else onFailure(R.string.error_form, "")
    }

    private fun getEncryptedPassword(): String{
        val key = getMetaData(application, "ENCRYPTION_KEY").toString()
        val cryptLib = CryptLib()
        return cryptLib.encryptPlainTextWithRandomIV(password, key)
    }

    fun setUserID(userID: String){
        viewModelScope.launch {
            dataRepository.userId = userID
            preferencesRepository.setUserID(userID)
        }
    }



    private fun validateEmail(): Boolean {
        return email.isNotEmpty() && emailErrorMsg == -1
    }
    
    fun forgotPassword(
        onFailure: (Int, String) -> Unit,
        onSuccess: (Int, String) -> Unit
    ){
        actionButtonStatus = false
        if (validateEmail()){
            dialogStatus = true

            dataRepository
                .forgotPassword(email)
                .enqueue(
                    object : Callback<String>{
                        override fun onResponse(call: Call<String> , response: Response<String>) {
                            dialogStatus = false
                            when(response.isSuccessful){
                                true -> onSuccess(R.string.email_for_reset_is_sended, "")
                                false -> onFailure(R.string.server_error, response.errorBody()?.string().toString())
                            }
                        }
                        override fun onFailure(call: Call<String> , t: Throwable) {
                            dialogStatus = false
                            onFailure(R.string.error, t.message.toString())
                        }
                    }
                )
        } else onFailure(R.string.email_is_not_valid, "")
    }

    private fun validatePassword(): Boolean {
        return password.isNotEmpty() && passwordErrMsg == -1
    }

    fun resetPassword(
        token: String,
        onFailure: (Int, String) -> Unit,
        onSuccess: (Int, String) -> Unit
    ) {
        actionButtonStatus = false
        if (validatePassword()){
            dialogStatus = true

            dataRepository
                .resetPassword(token, getEncryptedPassword())
                .enqueue(
                    object : Callback<String>{
                        override fun onResponse(call: Call<String> , response: Response<String>) {
                            dialogStatus = false
                            when(response.isSuccessful){
                                true -> onSuccess(R.string.password_is_updated, "")
                                false -> onFailure(R.string.server_error, response.errorBody()?.string().toString())
                            }
                        }
                        override fun onFailure(call: Call<String> , t: Throwable) {
                            dialogStatus = false
                            onFailure(R.string.error, t.message.toString())
                        }
                    }
                )
        } else onFailure(R.string.password_is_not_valid, "")
    }


    fun confirmAccount(
         token: String,
         onSuccess: () -> Unit,
         onFailure: (Int, String) -> Unit) {
         dataRepository.confirmAccount(token)
         .enqueue(
             object : Callback<String>{
                 override fun onResponse(call: Call<String>, response: Response<String>) {
                     when(response.isSuccessful){
                         true -> {
                             response.body()?.let { setUserID(it) }
                             onSuccess()
                         }
                         false -> onFailure(R.string.server_error, response.errorBody()?.string().toString())
                     }
                 }
                 override fun onFailure(call: Call<String>, t: Throwable) {
                     onFailure(R.string.error, t.message.toString())
                 }
             }
         )
    }
}



