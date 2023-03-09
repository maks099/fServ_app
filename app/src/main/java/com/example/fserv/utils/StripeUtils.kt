package com.example.fserv.utils

import android.app.Activity
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.example.fserv.api.DataRepository
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class StripeUtils {
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    private lateinit var paymentIntentClientSecret: String
    private var isReady = false

    fun openStripeMethod(activity: Fragment, onSuccess: () -> Unit){
        paymentSheet = PaymentSheet(activity, ::onPaymentSheetResult)
        "http://10.0.2.2:3000/payment-sheet".httpPost().responseJson { _, _, result ->
            if (result is Result.Success) {
                val responseJson = result.get().obj()
                paymentIntentClientSecret = responseJson.getString("paymentIntent")
                customerConfig = PaymentSheet.CustomerConfiguration(
                    responseJson.getString("customer"),
                    responseJson.getString("ephemeralKey")
                )
                val publishableKey = responseJson.getString("publishableKey")
                //PaymentConfiguration.init(activity.context, publishableKey)
                isReady = true
            }
        }
    }

    fun presentPaymentSheet() {

        if(isReady){
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                PaymentSheet.Configuration(
                    merchantDisplayName = "My merchant name",
                    customer = customerConfig,
                    // Set `allowsDelayedPaymentMethods` to true if your business
                    // can handle payment methods that complete payment after a delay, like SEPA Debit and Sofort.
                    allowsDelayedPaymentMethods = true
                )
            )
        } else{

        }
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Log.d("TEST", "canceled")
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                Log.d("TEST", "error: ${paymentSheetResult.error}")
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                Log.d("TEST", "paymentSheetResult.toString()")

                // Display for example, an order confirmation screen
                print("Completed")
            }
        }
    }

    companion object {
        private var INSTANCE: StripeUtils? = null
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = StripeUtils()

            }
        }
        fun get(): StripeUtils {
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }
}
