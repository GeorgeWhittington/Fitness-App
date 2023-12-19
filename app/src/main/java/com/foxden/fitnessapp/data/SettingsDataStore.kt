package com.foxden.fitnessapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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




}