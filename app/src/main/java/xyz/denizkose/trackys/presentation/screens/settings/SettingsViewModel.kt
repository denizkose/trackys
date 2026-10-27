package xyz.denizkose.trackys.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.denizkose.trackys.data.local.UserPreferences
import xyz.denizkose.trackys.data.local.dto.SubscriptionExportDto
import xyz.denizkose.trackys.domain.model.BillingCycle
import xyz.denizkose.trackys.domain.model.Subscription
import xyz.denizkose.trackys.domain.repository.CurrencyRepository
import xyz.denizkose.trackys.domain.repository.SubscriptionRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val userPreferences: UserPreferences,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeBaseCurrency()
        observeCategoriesAndSubscriptions()
    }

    private fun observeBaseCurrency() {
        viewModelScope.launch {
            currencyRepository.getBaseCurrency().collect { currency ->
                _uiState.update { it.copy(baseCurrency = currency) }
            }
        }
    }

    private fun observeCategoriesAndSubscriptions() {
        viewModelScope.launch {
            combine(
                userPreferences.categories,
                subscriptionRepository.getAllSubscriptions(),
                userPreferences.categoryColors
            ) { categoryList, subscriptions, colorsMap ->
                val counts = subscriptions.groupingBy { it.category.lowercase() }.eachCount()
                Triple(categoryList, counts, colorsMap)
            }.collect { (categoryList, counts, colorsMap) ->
                _uiState.update {
                    it.copy(
                        categories = categoryList,
                        categoryCounts = counts,
                        categoryColors = colorsMap
                    )
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SelectBaseCurrency -> {
                viewModelScope.launch {
                    val oldCurrency = _uiState.value.baseCurrency
                    if (event.currencyCode == oldCurrency) return@launch

                    _uiState.update { it.copy(isLoading = true) }

                    val result = currencyRepository.refreshRates(event.currencyCode)

                    if (result.isSuccess) {
                        currencyRepository.setBaseCurrency(event.currencyCode)
                        _uiState.update { it.copy(isLoading = false) }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                userMessage = xyz.denizkose.trackys.R.string.error_refresh_rates
                            )
                        }
                    }
                }
            }

            is SettingsEvent.DeleteCategory -> {
                if (event.categoryName.equals("Other", ignoreCase = true)) {
                    _uiState.update {
                        it.copy(userMessage = xyz.denizkose.trackys.R.string.cannot_delete_other)
                    }
                    return
                }
                viewModelScope.launch {
                    // Перепідключаємо всі підписки з цією категорією на 'Other'
                    subscriptionRepository.reassignCategory(event.categoryName, "Other")
                    userPreferences.deleteCategory(event.categoryName)
                }
            }

            is SettingsEvent.SetCategoryColor -> {
                viewModelScope.launch {
                    userPreferences.setCategoryColor(event.categoryName, event.hexColor)
                }
            }

            SettingsEvent.ClearAllData -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    subscriptionRepository.clearAllSubscriptions()
                    userPreferences.resetToDefault()
                    currencyRepository.refreshRates("USD")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = xyz.denizkose.trackys.R.string.reset_success
                        )
                    }
                }
            }

            SettingsEvent.DismissMessage -> {
                _uiState.update { it.copy(userMessage = null) }
            }
        }
    }

    suspend fun getExportJson(): String {
        val subscriptions = subscriptionRepository.getAllSubscriptions().first()
        val dtos = subscriptions.map { sub ->
            SubscriptionExportDto(
                name = sub.name,
                cost = sub.cost,
                currency = sub.currency,
                category = sub.category,
                billingCycle = sub.billingCycle.name,
                firstPaymentDate = sub.firstPaymentDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                isActive = sub.isActive
            )
        }
        return json.encodeToString(dtos)
    }

    suspend fun importFromJson(jsonString: String): Boolean {
        return try {
            val dtos = json.decodeFromString<List<SubscriptionExportDto>>(jsonString)
            val subscriptions = dtos.map { dto ->
                Subscription(
                    id = 0,
                    name = dto.name,
                    cost = dto.cost,
                    currency = dto.currency,
                    category = dto.category,
                    billingCycle = BillingCycle.valueOf(dto.billingCycle),
                    firstPaymentDate = LocalDate.parse(dto.firstPaymentDate, DateTimeFormatter.ISO_LOCAL_DATE),
                    isActive = dto.isActive
                )
            }
            // Додаємо всі нові категорії з файлу імпорту до DataStore налаштувань
            val importedCategories = dtos.map { it.category }
            userPreferences.addCategories(importedCategories)

            subscriptionRepository.insertSubscriptions(subscriptions)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
