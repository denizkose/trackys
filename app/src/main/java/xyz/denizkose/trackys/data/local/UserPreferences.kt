package xyz.denizkose.trackys.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class UserPreferences(private val context: Context) {

    companion object {
        private val KEY_BASE_CURRENCY = stringPreferencesKey("base_currency")
        private val KEY_CATEGORIES = stringSetPreferencesKey("categories")
        private val KEY_CATEGORY_COLORS = stringSetPreferencesKey("category_colors")

        val DEFAULT_CATEGORIES = setOf(
            "Entertainment",
            "Work",
            "Utilities",
            "Education",
            "Health",
            "Other"
        )
    }

    val baseCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_BASE_CURRENCY] ?: "USD"
    }

    val categories: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val saved = preferences[KEY_CATEGORIES] ?: DEFAULT_CATEGORIES
        // Переконуємось, що "Other" завжди присутня у списку категорій
        val withOther = saved + "Other"
        withOther.toList().distinct().sorted()
    }

    val categoryColors: Flow<Map<String, String>> = context.dataStore.data.map { preferences ->
        val rawSet = preferences[KEY_CATEGORY_COLORS] ?: emptySet()
        val result = mutableMapOf<String, String>()
        for (entry in rawSet) {
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                result[parts[0].lowercase()] = parts[1]
            }
        }
        result
    }

    suspend fun setBaseCurrency(currencyCode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BASE_CURRENCY] = currencyCode
        }
    }

    suspend fun addCategory(categoryName: String) {
        val trimmed = categoryName.trim()
        if (trimmed.isEmpty()) return
        context.dataStore.edit { preferences ->
            val current = preferences[KEY_CATEGORIES] ?: DEFAULT_CATEGORIES
            preferences[KEY_CATEGORIES] = current + trimmed
        }
    }

    suspend fun addCategories(newCategories: Collection<String>) {
        val valid = newCategories.map { it.trim() }.filter { it.isNotEmpty() }
        if (valid.isEmpty()) return
        context.dataStore.edit { preferences ->
            val current = preferences[KEY_CATEGORIES] ?: DEFAULT_CATEGORIES
            preferences[KEY_CATEGORIES] = current + valid
        }
    }

    suspend fun setCategoryColor(categoryName: String, hexColor: String) {
        val key = categoryName.trim().lowercase()
        if (key.isEmpty()) return
        context.dataStore.edit { preferences ->
            val current = preferences[KEY_CATEGORY_COLORS] ?: emptySet()
            val filtered = current.filterNot { it.startsWith("$key=") }.toSet()
            preferences[KEY_CATEGORY_COLORS] = filtered + "$key=$hexColor"
        }
    }

    suspend fun deleteCategory(categoryName: String) {
        if (categoryName.equals("Other", ignoreCase = true)) return
        val key = categoryName.trim().lowercase()
        context.dataStore.edit { preferences ->
            val currentCategories = preferences[KEY_CATEGORIES] ?: DEFAULT_CATEGORIES
            preferences[KEY_CATEGORIES] = currentCategories.filterNot { it.equals(categoryName, ignoreCase = true) }.toSet()

            val currentColors = preferences[KEY_CATEGORY_COLORS] ?: emptySet()
            preferences[KEY_CATEGORY_COLORS] = currentColors.filterNot { it.startsWith("$key=") }.toSet()
        }
    }

    suspend fun resetToDefault() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
