package xyz.denizkose.trackys.presentation.screens.settings

import androidx.annotation.StringRes
import xyz.denizkose.trackys.core.CurrencyHelper

data class SettingsUiState(
    val baseCurrency: String = "USD",
    val availableCurrencies: List<String> = CurrencyHelper.POPULAR_CURRENCIES.map { it.code },
    val categories: List<String> = emptyList(),
    val categoryCounts: Map<String, Int> = emptyMap(),
    val categoryColors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    @StringRes val userMessage: Int? = null
)

sealed interface SettingsEvent {
    data class SelectBaseCurrency(val currencyCode: String) : SettingsEvent
    data class DeleteCategory(val categoryName: String) : SettingsEvent
    data class SetCategoryColor(val categoryName: String, val hexColor: String) : SettingsEvent
    data object ClearAllData : SettingsEvent
    data object DismissMessage : SettingsEvent
}
