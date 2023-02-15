package com.example.fserv.view_models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fserv.R
import com.example.fserv.api.DataRepository
import com.example.fserv.model.UserInfo
import com.example.fserv.utils.CryptLib
import com.example.fserv.utils.getMetaData
import retrofit2.Call

private const val TAG = "LoginViewModel"
public class AuthorizationViewModel: ViewModel() {

    private val dataRepository: DataRepository = DataRepository()

    var emailErrorMsg = -1
    var passwordErrMsg = -1
    var emailError = false
    var passwordError = false

    val maxEmailLength = 40
    val maxPasswordLength = 40

    var email by mutableStateOf("")
    var password by mutableStateOf("") // helloworld23#
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
        if((email.isNotEmpty() && password.isNotEmpty())){
            return (!emailError && !passwordError)
        } else {
            return false
        }
    }




    fun registerNewUser(application: Application): Call<String> {
        actionButtonStatus = false
        val key = getMetaData(application, "ENCRYPTION_KEY").toString()
        val cryptLib = CryptLib()
        val encryptedPass = cryptLib.encryptPlainTextWithRandomIV(password, key)
        val userInfo = UserInfo(email, encryptedPass)

        return dataRepository.registerNewUser(userInfo)
    }

    fun loginClient(application: Application): Call<String> {
        actionButtonStatus = false
        val key = getMetaData(application, "ENCRYPTION_KEY").toString()
        val cryptLib = CryptLib()
        val encryptedPass = cryptLib.encryptPlainTextWithRandomIV(password, key)
        val userInfo = UserInfo(email, encryptedPass)

        return dataRepository.loginClient(userInfo)
    }

}



