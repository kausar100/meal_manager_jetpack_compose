package com.kausar.messmanagementapp.data.shared_pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_preferences")

class LoginDataStore @Inject constructor(
    private val context: Context,
) : LoginPreference {

    object KEYS {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_CONTACT_NUMBER = stringPreferencesKey("contact_number")
    }

    override fun getLoginStatus(): Flow<Boolean> {
        return context.dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preference ->
                preference[KEYS.KEY_IS_LOGGED_IN] ?: false
            }
    }

    override suspend fun saveLoginStatus(status: Boolean) {
        Log.d("saveLoginStatus: ", status.toString())
        context.dataStore.edit { preference ->
            preference[KEYS.KEY_IS_LOGGED_IN] = status
        }
    }

    override suspend fun saveUsername(name: String) {
        context.dataStore.edit { preference ->
            preference[KEYS.KEY_USERNAME] = name
        }
    }

    override fun getUsername(): Flow<String> {
        return context.dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preference ->
                preference[KEYS.KEY_USERNAME] ?: ""
            }
    }

    override suspend fun saveContactNumber(contact: String) {
        context.dataStore.edit { preference ->
            preference[KEYS.KEY_CONTACT_NUMBER] = contact
        }
    }

    override fun getContactNumber(): Flow<String> {
        return context.dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preference ->
                preference[KEYS.KEY_CONTACT_NUMBER] ?: ""
            }
    }

}