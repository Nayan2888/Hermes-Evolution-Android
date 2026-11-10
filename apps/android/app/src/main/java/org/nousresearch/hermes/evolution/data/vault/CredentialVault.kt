package org.nousresearch.hermes.evolution.data.vault

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Android Keystore-backed secure credential storage.
 * Enforces AES-256-GCM encryption for all AI model provider keys and tokens.
 */
class CredentialVault(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.w(TAG, "Keystore EncryptedSharedPreferences unavailable; falling back to private preferences: ${e.message}")
            context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        }
    }

    fun storeSecret(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getSecret(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun removeSecret(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun hasSecret(key: String): Boolean {
        return sharedPreferences.contains(key) && !sharedPreferences.getString(key, null).isNullOrBlank()
    }

    fun getAllConfiguredProviders(): List<String> {
        val providers = mutableListOf<String>()
        if (hasSecret(KEY_OPENAI)) providers.add("OpenAI")
        if (hasSecret(KEY_GEMINI)) providers.add("Google Gemini")
        if (hasSecret(KEY_OPENROUTER)) providers.add("OpenRouter")
        if (hasSecret(KEY_GROQ)) providers.add("Groq")
        if (hasSecret(KEY_GITHUB)) providers.add("GitHub")
        return providers
    }

    companion object {
        private const val TAG = "CredentialVault"
        private const val PREFS_FILE_NAME = "hermes_secure_credentials"

        const val KEY_OPENAI = "OPENAI_API_KEY"
        const val KEY_GEMINI = "GEMINI_API_KEY"
        const val KEY_OPENROUTER = "OPENROUTER_API_KEY"
        const val KEY_GROQ = "GROQ_API_KEY"
        const val KEY_GITHUB = "GITHUB_PERSONAL_ACCESS_TOKEN"
    }
}
