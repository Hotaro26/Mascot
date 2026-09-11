package com.hotaro.mascot.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private val dataStore = context.dataStore

    val activeSchemeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[ACTIVE_SCHEME_KEY] ?: "Dynamic"
        }

    val themeModeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: "System"
        }

    val isAmoledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_AMOLED_KEY] ?: false
        }

    suspend fun saveActiveScheme(scheme: String) {
        dataStore.edit { preferences ->
            preferences[ACTIVE_SCHEME_KEY] = scheme
        }
    }

    suspend fun saveThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    suspend fun saveIsAmoled(isAmoled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_AMOLED_KEY] = isAmoled
        }
    }

    companion object {
        val ACTIVE_SCHEME_KEY = stringPreferencesKey("active_scheme")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val IS_AMOLED_KEY = booleanPreferencesKey("is_amoled")
    }
}
