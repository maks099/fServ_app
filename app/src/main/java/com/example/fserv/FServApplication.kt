package com.example.fserv

import android.app.Application
import com.example.fserv.api.DataRepository
import com.example.fserv.utils.PreferencesRepository

class FServApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        DataRepository.initialize()
        PreferencesRepository.initialize(this)

    }
}