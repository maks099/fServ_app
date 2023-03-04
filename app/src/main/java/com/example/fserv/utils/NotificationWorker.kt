package com.example.fserv.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fserv.MainActivity
import com.example.fserv.R
import com.example.fserv.api.DataRepository
import com.example.fserv.model.server.NotificationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.suspendCoroutine

private const val NOTIFICATION_CHANNEL_ID = "fServ_channel_id_1"

class NotificationWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("RRRR", "do work")
        val preferencesRepository = PreferencesRepository.get()
        val clientId = preferencesRepository.userID.first().replace("\"", "")

        val dataRepository = DataRepository()

        return try {
            dataRepository.searchNotifications(clientId).enqueue(
                object : Callback<NotificationResponse> {
                    override fun onResponse(
                        call: Call<NotificationResponse>,
                        response: Response<NotificationResponse>
                    ) {
                        if (response.isSuccessful){
                            val list = response.body()?.notifications
                            if (list != null && list.isNotEmpty()) {
                                notifyUser(list.first().message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    }
                }
            )

            Result.success()
        } catch (ex: Exception){
            Log.e("TAG" , "Backround task failed" , ex)
            Result.failure()
        }

    }

    private fun notifyUser(message: String) {
        val resources = context.resources
        val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.event_is_removed))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.warning))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(0, notification)
    }
}