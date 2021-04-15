package com.devdd.recipe.data.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devdd.recipe.utils.extensions.dataStore
import com.devdd.recipe.utils.extensions.getValueAsFlow
import com.devdd.recipe.utils.extensions.setValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.UUID.randomUUID
import javax.inject.Inject
import javax.inject.Singleton

interface DataStorePreference {


    val preferences: Flow<Preferences>

    suspend fun <T> setValue(key: Preferences.Key<T>, value: Any)

    fun <T> getValue(key: Preferences.Key<T>, default: T): Flow<T>

    suspend fun <T> remove(key: Preferences.Key<T>)

    fun generateDeviceId()
    val deviceId: Flow<String>

    suspend fun shouldUploadDeviceIdToServer(shouldUpload: Boolean)
    val shouldUploadDeviceIdToServer: Flow<Boolean>

    suspend fun setGuestToken(token: String)
    val guestToken: Flow<String>

    suspend fun setRecipePreference(pref: String)
    val recipePreference: Flow<String>

    suspend fun setSelectedLocale(language: String)
    val selectedLanguage: Flow<String>

    /***
     * clears all the stored data
     */
    suspend fun clearPreferences()
}

@Singleton
class DataStorePreferences @Inject constructor(@ApplicationContext private val context: Context) :
    DataStorePreference {

    private val dataStore: DataStore<Preferences>
        get() = context.dataStore

    /**
     * override preferences
     * */

    init {
        generateDeviceId()
    }

    override val preferences: Flow<Preferences>
        get() = dataStore.data

    override suspend fun <T> setValue(key: Preferences.Key<T>, value: Any) {
        try {
            dataStore.setValue(key, value as T)
        } catch (ex: ClassCastException) {
            Timber.e(ex)
        }
    }

    override fun <T> getValue(key: Preferences.Key<T>, default: T): Flow<T> {
        return dataStore.getValueAsFlow(key, default)
    }

    override suspend fun <T> remove(key: Preferences.Key<T>) {
        dataStore.edit {
            it.remove(key)
        }
    }

    @SuppressLint("HardwareIds")
    override fun generateDeviceId() = runBlocking {
        val existingId: String = deviceId.catch { emit("") }.first()
        if (existingId.isBlank()) {
            val guid = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: randomUUID().toString()
            dataStore.setValue(PREF_KEY_DEVICE_ID, guid)
            shouldUploadDeviceIdToServer(true)
        }
    }

    override val deviceId: Flow<String>
        get() = dataStore.getValueAsFlow(PREF_KEY_DEVICE_ID, "")

    override suspend fun shouldUploadDeviceIdToServer(shouldUpload: Boolean) {
        dataStore.setValue(PREF_KEY_SHOULD_UPLOAD_DEVICE_ID, shouldUpload)
    }

    override val shouldUploadDeviceIdToServer: Flow<Boolean>
        get() = dataStore.getValueAsFlow(PREF_KEY_SHOULD_UPLOAD_DEVICE_ID, true)

    override suspend fun setGuestToken(token: String) {
        dataStore.setValue(PREF_KEY_GUEST_TOKEN, token)
    }

    override val guestToken: Flow<String>
        get() = dataStore.getValueAsFlow(PREF_KEY_GUEST_TOKEN, "")


    override suspend fun setRecipePreference(pref: String) {
        dataStore.setValue(PREF_KEY_SELECTED_RECIPE_PREF, pref)
    }

    override val recipePreference: Flow<String>
        get() = dataStore.getValueAsFlow(PREF_KEY_SELECTED_RECIPE_PREF, "")

    override suspend fun setSelectedLocale(language: String) {
        dataStore.setValue(PREF_KEY_SELECTED_LANGUAGE, language)
    }

    override val selectedLanguage: Flow<String>
        get() = dataStore.getValueAsFlow(PREF_KEY_SELECTED_LANGUAGE, "")

    override suspend fun clearPreferences() {
        dataStore.edit {
            it.clear()
        }
    }

    private companion object {
        private object PreferencesName {
            const val GUEST_TOKEN = "guest_token"
            const val SELECTED_RECIPE = "selected_recipe_pref"
            const val SELECTED_LANGUAGE = "selected_language"
            const val DEVICE_ID = "device_id"
            const val SHOULD_UPLOAD_DEVICE_ID = "should_upload_device_id"
        }

        val PREF_KEY_GUEST_TOKEN: Preferences.Key<String> =
            stringPreferencesKey(PreferencesName.GUEST_TOKEN)

        val PREF_KEY_SELECTED_RECIPE_PREF: Preferences.Key<String> =
            stringPreferencesKey(PreferencesName.SELECTED_RECIPE)

        val PREF_KEY_SELECTED_LANGUAGE: Preferences.Key<String> =
            stringPreferencesKey(PreferencesName.SELECTED_LANGUAGE)

        val PREF_KEY_DEVICE_ID: Preferences.Key<String> =
            stringPreferencesKey(PreferencesName.DEVICE_ID)

        val PREF_KEY_SHOULD_UPLOAD_DEVICE_ID: Preferences.Key<Boolean> =
            booleanPreferencesKey(PreferencesName.SHOULD_UPLOAD_DEVICE_ID)


    }

}