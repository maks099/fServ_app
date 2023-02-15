package com.example.fserv.utils

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build


    fun getMetaData(application: Application, key: String): Any? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            application.packageManager.getProperty(key, application.packageName)
        } else {
            val appInfo = application.packageManager
                .getApplicationInfo(application.packageName, PackageManager.GET_META_DATA)
            return appInfo.metaData[key]
        }
    }



