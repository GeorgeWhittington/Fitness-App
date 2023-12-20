package com.foxden.fitnessapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "SettingsDatastore")


class SettingsDataStoreManager(private val context: Context) {


    // Function to save a string value
    suspend fun saveStringSetting(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        Log.d("DataStore Tag", "Saving height unit: $value")
        context.dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    // Function to get a string value
    fun getStringSetting(key: String, defaultValue: String = ""): Flow<String> {
        val dataStoreKey = stringPreferencesKey(key)
        Log.d("GET DataStore Tag", "GET")
        return context.dataStore.data

            .map { preferences ->
                preferences[dataStoreKey] ?: defaultValue
            }
    }

    // Function to save a switch value
    suspend fun saveSwitchSetting(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    // Function to get a switch value
    fun getSwitchSetting(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        val dataStoreKey = booleanPreferencesKey(key)
        return context.dataStore.data
            .map { preferences ->
                preferences[dataStoreKey] ?: defaultValue
            }
    }

    val checkDarkmode: LiveData<Boolean> = liveData {
        emitSource(context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey("DarkmodeKey")] ?: false
            }
            .asLiveData())
    }

}
