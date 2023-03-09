package com.example.fserv.view_models

import android.app.Activity
import android.app.PendingIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fserv.api.DataRepository
import com.example.fserv.api.TicketRepository
import com.example.fserv.model.app.createPaymentsClient
import com.example.fserv.model.app.getPaymentDataRequest
import com.example.fserv.model.server.UserActivityObj
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentDataRequest
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitiesListViewModel : ViewModel() {
    var snackIsShowing by mutableStateOf(false)
    private lateinit var paymentSheet: PaymentSheet
     var customerConfig: PaymentSheet.CustomerConfiguration? = null
    private lateinit var paymentIntentClientSecret: String

    var topUpSum by mutableStateOf("")
    var googlePayIsReady by mutableStateOf(false)
    val isRefreshing = MutableStateFlow(false)
    private val ticketRepository = TicketRepository.get()
    private val dataRepo = DataRepository.get()
    var account by mutableStateOf(-1)



    fun getSecret(activity: ComponentActivity, onSuccess: () -> Unit = {}){
        "http://10.0.2.2:3000/payment-sheet".httpPost().responseJson { _, _, result ->
            if (result is Result.Success) {
                val responseJson = result.get().obj()
                paymentIntentClientSecret = responseJson.getString("paymentIntent")
                customerConfig = PaymentSheet.CustomerConfiguration(
                    responseJson.getString("customer"),
                    responseJson.getString("ephemeralKey")
                )
                val publishableKey = responseJson.getString("publishableKey")
                PaymentConfiguration.init(activity, publishableKey)
                Log.d("TAG", "open stripe method succeeded")

            }
        }
    }




    fun handlePaymentResult(result: PaymentSheetResult) {
        when(result) {
            PaymentSheetResult.Canceled -> {
                Log.d("TAG", "canceled")
            }
            PaymentSheetResult.Completed ->                Log.d("TAG", "success")

            is PaymentSheetResult.Failed ->                 Log.d("TAG", "fails")

        }
    }

    fun getCustomInfos(): Flow<PagingData<UserActivityObj>> {
        getBilling()
        return  ticketRepository.getUserActivities().cachedIn(viewModelScope)
    }



    init {
        getBilling()
    }

    private fun getBilling(){
        dataRepo.getUserBilling().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String> , response: Response<String>) {
                    when(response.isSuccessful){
                        true -> response.body()?.let {
                            try {
                                account = Integer.parseInt(it)
                            } catch (ex: java.lang.NumberFormatException){
                                ex.printStackTrace()
                            }
                        }
                        false -> {}
                    }
                }

                override fun onFailure(call: Call<String> , t: Throwable) {
                    Log.d("ERROR", t.message.toString())
                }
            }
        )
    }

    fun updateAccount() {
        // TODO: make query to server
        account += Integer.parseInt(topUpSum)
    }

    fun startGooglePayAction(activity: Activity, onSuccess: (PendingIntent) -> Unit, onError: (String) -> Unit){
        val paymentDataRequestJson = getPaymentDataRequest("1000")
        if (paymentDataRequestJson == null) {
            onError("Can't fetch payment data request")
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        val task =  createPaymentsClient(activity).loadPaymentData(request)
        task.addOnCompleteListener { completedTask ->
            if (!completedTask.isSuccessful) {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        onSuccess(exception.resolution)
                    }
                    is ApiException -> {
                        onError("Error code: ${exception.statusCode}, Message: ${exception.message}")
                    }
                    else -> {
                        onError("Unexpected non API exception")
                    }
                }
            }
        }
    }


    companion object {
        private var INSTANCE: ActivitiesListViewModel? = null
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = ActivitiesListViewModel()
            }
        }

        fun get(): ActivitiesListViewModel {
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }
}