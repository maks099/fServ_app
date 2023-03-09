package com.example.fserv

import android.app.Application
import com.example.fserv.api.DataRepository
import com.example.fserv.api.TicketRepository
import com.example.fserv.utils.PreferencesRepository
import com.example.fserv.utils.StripeUtils
import com.stripe.android.PaymentConfiguration

class FServApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        StripeUtils.initialize()

        DataRepository.initialize()
        TicketRepository.initialize()
        PreferencesRepository.initialize(this)

    }
}