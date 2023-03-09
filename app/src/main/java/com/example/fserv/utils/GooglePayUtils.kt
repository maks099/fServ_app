package com.example.fserv.model.app

import android.app.Activity
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


private val baseRequest = JSONObject().apply {
    put("apiVersion", 2)
    put("apiVersionMinor", 0)
}

private fun gatewayTokenizationSpecification(): JSONObject {
    return JSONObject().apply {
        put("type", "PAYMENT_GATEWAY")
        put("parameters", JSONObject(mapOf(
            "gateway" to "stripe",
            "stripe:version" to "2018-10-31",
            "stripe:publishableKey" to "pk_test_51MiulqLhiZPeDm5RWdbymgJJmXky5L7kgnOKUWfTqx85URsRtKisJvGnC0wI9GX0OR9NppKsprw0HCpWQiZVYV2o009CUsJuMJ")))
    }
}

private val allowedCardNetworks = JSONArray(listOf(
    "AMEX",
    "DISCOVER",
    "INTERAC",
    "JCB",
    "MASTERCARD",
    "VISA"))

private val allowedCardAuthMethods = JSONArray(listOf(
    "PAN_ONLY",
    "CRYPTOGRAM_3DS"))

private fun baseCardPaymentMethod(): JSONObject {
    return JSONObject().apply {

        val parameters = JSONObject().apply {
            put("allowedAuthMethods", allowedCardAuthMethods)
            put("allowedCardNetworks", allowedCardNetworks)
            put("billingAddressRequired", true)
            put("billingAddressParameters", JSONObject().apply {
                put("format", "FULL")
            })
        }

        put("type", "CARD")
        put("parameters", parameters)
    }
}

private fun cardPaymentMethod(): JSONObject {
    val cardPaymentMethod = baseCardPaymentMethod()
    cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())

    return cardPaymentMethod
}

fun createPaymentsClient(activity: Activity): PaymentsClient {
    val walletOptions = Wallet.WalletOptions.Builder()
        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
        .build()

    return Wallet.getPaymentsClient(activity, walletOptions)
}

private fun isReadyToPayRequest(): JSONObject? {
    return try {
        baseRequest.apply {
            put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
        }

    } catch (e: JSONException) {
        null
    }
}

private fun getTransactionInfo(price: String): JSONObject {
    return JSONObject().apply {
        put("totalPrice", price)
        put("totalPriceStatus", "FINAL")
        put("countryCode", "UA")
        put("currencyCode", "UAH")
    }
}

private val merchantInfo: JSONObject =
    JSONObject().put("merchantName", "Example Merchant")

fun getPaymentDataRequest(price: String): JSONObject? {
    try {
        return baseRequest.apply {
            put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
            put("transactionInfo", getTransactionInfo(price))
            put("merchantInfo", merchantInfo)

            // An optional shipping address requirement is a top-level property of the
            // PaymentDataRequest JSON object.
            val shippingAddressParameters = JSONObject().apply {
                put("phoneNumberRequired", false)
                put("allowedCountryCodes", JSONArray(listOf("US", "GB")))
            }
            put("shippingAddressParameters", shippingAddressParameters)
            put("shippingAddressRequired", true)
        }
    } catch (e: JSONException) {
        return null
    }
}

fun possiblyShowGooglePayButton(activity: Activity, onSuccess: () -> Unit){
    val isReadyToPayJson = isReadyToPayRequest() ?: return
    val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
    val task = createPaymentsClient(activity).isReadyToPay(request)
    task.addOnCompleteListener { completedTask ->
        try {
            completedTask.getResult(ApiException::class.java)?.let {
                Log.d("TAG", "heel")
                onSuccess()
            }
        } catch (exception: ApiException) {
            Log.w("isReadyToPay failed", exception)
        }
    }
}
