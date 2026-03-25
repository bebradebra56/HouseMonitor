package com.housemo.monisto.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "house_monitor_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val UNIT_SYSTEM = stringPreferencesKey("unit_system")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val THEME_DARK = booleanPreferencesKey("theme_dark")
        val DEFAULT_CURRENCY = stringPreferencesKey("default_currency")
    }

    val unitSystem: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[UNIT_SYSTEM] ?: "Metric"
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }

    val themeDark: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[THEME_DARK] ?: false
    }

    val defaultCurrency: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DEFAULT_CURRENCY] ?: "USD"
    }

    suspend fun setUnitSystem(value: String) {
        context.dataStore.edit { it[UNIT_SYSTEM] = value }
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = value }
    }

    suspend fun setThemeDark(value: Boolean) {
        context.dataStore.edit { it[THEME_DARK] = value }
    }

    suspend fun setDefaultCurrency(value: String) {
        context.dataStore.edit { it[DEFAULT_CURRENCY] = value }
    }
}
