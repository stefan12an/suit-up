package com.example.suitup.splash.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SplashRepository {
    fun getUser(): Flow<Boolean?>
}

class SplashRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>): SplashRepository{

    companion object {
        val AUTOLOGIN = booleanPreferencesKey("autologin")
    }

    override fun getUser(): Flow<Boolean?> = dataStore.data.map {
        it[AUTOLOGIN]
    }

}