package com.blitzlabx.zcode.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zcode_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme") // light / dark / system
        val AUTO_DETECT = booleanPreferencesKey("auto_detect")
        val PRESERVE_EXACT = booleanPreferencesKey("preserve_exact")
        val AUTO_COPY = booleanPreferencesKey("auto_copy")
        val SAVE_HISTORY = booleanPreferencesKey("save_history")
        val CONFIRM_DESTRUCTIVE = booleanPreferencesKey("confirm_destructive")
        val REQUIRE_PASSWORD_SECURE = booleanPreferencesKey("require_password_secure")
        val CLEAR_PASSWORD_AFTER = booleanPreferencesKey("clear_password_after")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val DEFAULT_CODE_TYPE = stringPreferencesKey("default_code_type")
        val DEFAULT_HASH = stringPreferencesKey("default_hash")
        val DEFAULT_LANGUAGE = stringPreferencesKey("default_language")
        val DEFAULT_MODE = stringPreferencesKey("default_mode")
    }

    val theme: Flow<String> = context.dataStore.data.map { it[Keys.THEME] ?: "system" }
    val autoDetect: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_DETECT] ?: true }
    val preserveExact: Flow<Boolean> = context.dataStore.data.map { it[Keys.PRESERVE_EXACT] ?: true }
    val autoCopy: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_COPY] ?: false }
    val saveHistory: Flow<Boolean> = context.dataStore.data.map { it[Keys.SAVE_HISTORY] ?: true }
    val confirmDestructive: Flow<Boolean> = context.dataStore.data.map { it[Keys.CONFIRM_DESTRUCTIVE] ?: true }
    val requirePasswordSecure: Flow<Boolean> = context.dataStore.data.map { it[Keys.REQUIRE_PASSWORD_SECURE] ?: true }
    val clearPasswordAfter: Flow<Boolean> = context.dataStore.data.map { it[Keys.CLEAR_PASSWORD_AFTER] ?: true }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    val defaultCodeType: Flow<String> = context.dataStore.data.map { it[Keys.DEFAULT_CODE_TYPE] ?: "compact" }
    val defaultHash: Flow<String> = context.dataStore.data.map { it[Keys.DEFAULT_HASH] ?: "SHA-256" }
    val defaultLanguage: Flow<String> = context.dataStore.data.map { it[Keys.DEFAULT_LANGUAGE] ?: "z-alpha" }
    val defaultMode: Flow<String> = context.dataStore.data.map { it[Keys.DEFAULT_MODE] ?: "standard" }

    suspend fun setTheme(value: String) = context.dataStore.edit { it[Keys.THEME] = value }
    suspend fun setAutoDetect(value: Boolean) = context.dataStore.edit { it[Keys.AUTO_DETECT] = value }
    suspend fun setPreserveExact(value: Boolean) = context.dataStore.edit { it[Keys.PRESERVE_EXACT] = value }
    suspend fun setAutoCopy(value: Boolean) = context.dataStore.edit { it[Keys.AUTO_COPY] = value }
    suspend fun setSaveHistory(value: Boolean) = context.dataStore.edit { it[Keys.SAVE_HISTORY] = value }
    suspend fun setConfirmDestructive(value: Boolean) = context.dataStore.edit { it[Keys.CONFIRM_DESTRUCTIVE] = value }
    suspend fun setRequirePasswordSecure(value: Boolean) = context.dataStore.edit { it[Keys.REQUIRE_PASSWORD_SECURE] = value }
    suspend fun setClearPasswordAfter(value: Boolean) = context.dataStore.edit { it[Keys.CLEAR_PASSWORD_AFTER] = value }
    suspend fun setOnboardingDone(value: Boolean) = context.dataStore.edit { it[Keys.ONBOARDING_DONE] = value }
    suspend fun setDefaultCodeType(value: String) = context.dataStore.edit { it[Keys.DEFAULT_CODE_TYPE] = value }
    suspend fun setDefaultHash(value: String) = context.dataStore.edit { it[Keys.DEFAULT_HASH] = value }
    suspend fun setDefaultLanguage(value: String) = context.dataStore.edit { it[Keys.DEFAULT_LANGUAGE] = value }
    suspend fun setDefaultMode(value: String) = context.dataStore.edit { it[Keys.DEFAULT_MODE] = value }
}
