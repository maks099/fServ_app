package com.example.fserv.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PreferencesRepository private constructor(private val dataStore: DataStore<Preferences>) {


    val userID : Flow<String> = dataStore.data.map {
        it[USER_ID_KEY] ?: ""
    }.distinctUntilChanged()


    suspend fun setUserID(isPinned: String){
        dataStore.edit {
            it[USER_ID_KEY] = isPinned
        }
    }

    val lastResultId: Flow<String> = dataStore.data.map {
        it[PREF_LAST_RESULT_ID] ?: ""
    }.distinctUntilChanged()

    suspend fun setLastResultId(lastResultId: String){
        dataStore.edit {
            it[PREF_LAST_RESULT_ID] = lastResultId
        }
    }

    companion object{
        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("lastResultId")
        private var INSTANCE: PreferencesRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("_settings")
                }
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE ?: throw java.lang.IllegalStateException("PreferenceRepository must be initialized")
        }


    }

}