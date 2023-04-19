package com.example.fserv.view_models

import android.app.Activity
import android.app.PendingIntent
import android.util.Log
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
import com.example.fserv.model.server.UserActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentDataRequest
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetContract
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
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
    var account by mutableStateOf(0.0)






    fun handleStripeResult(result: PaymentSheetResult, onError: (String) -> Unit) {
        when(result) {
            PaymentSheetResult.Canceled -> {
                Log.d("TAG", "canceled")
            }
            PaymentSheetResult.Completed -> updateAccount()

            is PaymentSheetResult.Failed -> onError("Try again")

        }
    }

    fun getCustomInfos(): Flow<PagingData<UserActivity>> {
        getBilling()
        return  ticketRepository.getUserActivities().cachedIn(viewModelScope)
    }



    init {
        //getBilling()
    }

    private fun getBilling(){
        dataRepo.getUserBilling().enqueue(
            object : Callback<Double> {
                override fun onResponse(call: Call<Double> , response: Response<Double>) {
                    when(response.isSuccessful){
                        true -> response.body()?.let {
                            try {
                                account = it
                            } catch (ex: java.lang.NumberFormatException){
                                ex.printStackTrace()
                            }
                        }
                        false -> {}
                    }
                }

                override fun onFailure(call: Call<Double> , t: Throwable) {
                    Log.d("ERROR", t.message.toString())
                }
            }
        )
    }

    fun updateAccount() {
        dataRepo.updateUserBilling(topUpSum).enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String> , response: Response<String>) {
                    when(response.isSuccessful){
                        true -> {
                            account+=Integer.parseInt(topUpSum)
                            topUpSum = ""
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

    fun startGooglePayAction(activity: Activity, onSuccess: (PendingIntent) -> Unit, onError: (String) -> Unit){
        val paymentDataRequestJson = getPaymentDataRequest(topUpSum)
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

    fun getStripePaymentSheet(
        activity: Activity,
        onSuccess: (PaymentSheetContract.Args) -> Unit,
        onError: (String) -> Unit
    ){
        ticketRepository.getPaymentSheet(topUpSum).enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String> , response: Response<String>) {
                    when(response.isSuccessful){
                        true -> response.body()?.let {
                            try {
                                val jObject=JSONObject(it)
                                val paymentIntentClientSecret: String = jObject.getString("paymentIntent")
                                val publishableKey: String = jObject.getString("publishableKey")
                                PaymentConfiguration.init(activity, publishableKey)
                                val args = PaymentSheetContract.Args.createPaymentIntentArgs(paymentIntentClientSecret)
                                onSuccess(args)
                            } catch (ex: java.lang.NumberFormatException){
                                ex.printStackTrace()
                            }
                        }
                        false -> onError("Bad Response")
                    }
                }

                override fun onFailure(call: Call<String> , t: Throwable) {
                    onError(t.message.toString())
                }
            }
        )
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