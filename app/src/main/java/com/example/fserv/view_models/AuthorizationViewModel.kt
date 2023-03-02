package com.example.fserv.view_models

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fserv.R
import com.example.fserv.api.DataRepository
import com.example.fserv.model.app.StringCoder
import com.example.fserv.model.server.Client
import com.example.fserv.utils.CryptLib
import com.example.fserv.utils.PreferencesRepository
import com.example.fserv.utils.getMetaData
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "LoginViewModel"
public class AuthorizationViewModel: ViewModel() {

    private val dataRepository: DataRepository = DataRepository.get()
    private val preferencesRepository = PreferencesRepository.get()

    var resetErrorMessage = ""
    
    var emailErrorMsg = -1
    var passwordErrMsg = -1
    var firstNameErrMsg = -1
    var lastNameErrMsg = -1

    var emailError = false
    var passwordError = false
    var firstNameError = false
    var lastNameError = false

    private val maxEmailLength = 40
    private val maxPasswordLength = 40

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var actionButtonStatus by mutableStateOf(true)

    fun onPasswordChange(password: String){
        if(password.length < maxPasswordLength){
            this.password = password
        }
        if(password.length < 8){
            passwordError = true
            passwordErrMsg = R.string.error_password_length
        }
        else if (!password.contains("[0-9]".toRegex())) {
            passwordError = true
            passwordErrMsg = R.string.error_password_number
        }
        else if (!password.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            passwordError = true
            passwordErrMsg = R.string.error_password_special
        }

        else {
            passwordError = false
            passwordErrMsg = -1
        }
    }

    fun onEmailChange(email: String){
        if(email.length < maxEmailLength){
            this.email = email
        }

        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailError = false
            emailErrorMsg = -1
        } else {
            emailError = true
            emailErrorMsg = R.string.error_email_correct

        }
    }

    fun validate(): Boolean {
        if((email.isNotEmpty() && password.isNotEmpty() && firstName.length > 2 && lastName.length > 2)){
            return (!emailError && !passwordError)
        } else {
            return false
        }
    }




    fun registerNewUser(application: Application): Call<String> {
        //actionButtonStatus = false
        val key = getMetaData(application, "ENCRYPTION_KEY").toString()
        val cryptLib = CryptLib()
        val encryptedPass = cryptLib.encryptPlainTextWithRandomIV(password, key)
        val userInfo = Client(
            email = email,
            password = encryptedPass,
            firstName = firstName,
            lastName = lastName
        )

        return dataRepository.registerNewUser(userInfo)
    }

    fun loginClient(application: Application): Call<String> {
        //actionButtonStatus = false
        val key = getMetaData(application, "ENCRYPTION_KEY").toString()
        val cryptLib = CryptLib()
        val encryptedPass = cryptLib.encryptPlainTextWithRandomIV(password, key)
        val userInfo = Client(email = email, password = encryptedPass)
        Log.d("EMAIL", email + encryptedPass)


        return dataRepository.loginClient(userInfo)
    }

    fun setUserID(userID: String){
        viewModelScope.launch {
            dataRepository.userId = userID
            preferencesRepository.setUserID(userID)
        }
    }

    fun onFirsNameChange(firstName: String) {
        this.firstName = firstName
        if(firstName.length < 3){
            firstNameError = true
            firstNameErrMsg = R.string.name_length_error

        } else {
            firstNameError = false
            firstNameErrMsg = -1
        }
    }

    fun onLastNameChange(lastName: String) {
        this.lastName = lastName
        if(lastName.length < 3){
            lastNameError = true
            lastNameErrMsg = R.string.name_length_error

        } else {
            lastNameError = false
            lastNameErrMsg = -1
        }
    }

    fun validateLoginAction(): Boolean {
        if((email.isNotEmpty() && password.isNotEmpty())){
            return (!emailError && !passwordError)
        } else {
            return false
        }
    }



    private fun validateEmail(): Boolean {
        return email.isNotEmpty() && !emailError
    }
    
    fun resetPassword(
        onFailure: (Int, String) -> Unit,
        onSuccess: (Int, String) -> Unit
    ){
        actionButtonStatus = false
        if (validateEmail()){
            dataRepository
                .resetClientPassword(email)
                .enqueue(
                    object : Callback<String>{
                        override fun onResponse(call: Call<String> , response: Response<String>) {
                            when(response.isSuccessful){
                                true -> onSuccess(R.string.email_for_reset_is_sended, "")
                                false -> onFailure(R.string.server_error, response.errorBody()?.string().toString())
                            }
                            actionButtonStatus = true
                        }
                        override fun onFailure(call: Call<String> , t: Throwable) {
                            onFailure(R.string.server_error, t.message.toString())
                            actionButtonStatus = true
                        }
                    }
                )
        } else {
            onFailure(R.string.email_is_not_valid, "")
        }
    }
}



