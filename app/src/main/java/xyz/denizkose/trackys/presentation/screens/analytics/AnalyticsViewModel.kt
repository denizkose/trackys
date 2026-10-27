package xyz.denizkose.trackys.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import xyz.denizkose.trackys.core.CategoryHelper
import xyz.denizkose.trackys.data.local.UserPreferences
import xyz.denizkose.trackys.domain.repository.CurrencyRepository
import xyz.denizkose.trackys.domain.repository.SubscriptionRepository
import xyz.denizkose.trackys.domain.usecase.CalculateMonthlyExpenseUseCase
import xyz.denizkose.trackys.presentation.components.ChartSlice
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository,
    currencyRepository: CurrencyRepository,
    userPreferences: UserPreferences,
    private val calculateMonthlyExpenseUseCase: CalculateMonthlyExpenseUseCase
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = combine(
        subscriptionRepository.getAllSubscriptions(),
        currencyRepository.getExchangeRates(),
        currencyRepository.getBaseCurrency(),
        userPreferences.categoryColors
    ) { subs, rates, baseCur, customColors ->

        if (subs.isEmpty()) {
            return@combine AnalyticsUiState(
                isLoading = false,
                baseCurrency = baseCur
            )
        }

        val expensesByCategory = mutableMapOf<String, Double>()
        var totalAmount = 0.0

        for (sub in subs) {
            val targetBaseRate = rates[baseCur] ?: 1.0
            val subCurrencyRate = rates[sub.currency] ?: 1.0
            val conversionRate = if (sub.currency.equals(baseCur, ignoreCase = true)) 1.0 else (targetBaseRate / subCurrencyRate)

            val monthly = calculateMonthlyExpenseUseCase(sub, conversionRate)

            expensesByCategory[sub.category] = (expensesByCategory[sub.category] ?: 0.0) + monthly
            totalAmount += monthly
        }

        val stats = expensesByCategory.map { (cat, amount) ->
            val percentage = if (totalAmount > 0) (amount / totalAmount).toFloat() else 0f
            val color = CategoryHelper.getCategoryColor(cat, customColors)

            CategoryStatUi(
                category = cat,
                amount = amount,
                percentage = percentage,
                formattedPercentage = "${String.format(Locale.US, "%.1f", percentage * 100)}%",
                color = color
            )
        }.sortedByDescending { it.amount }

        val slices = stats.map { stat ->
            ChartSlice(
                label = stat.category,
                amount = stat.amount,
                percentage = stat.percentage,
                color = stat.color
            )
        }

        AnalyticsUiState(
            isLoading = false,
            totalMonthlyCost = totalAmount,
            baseCurrency = baseCur,
            chartSlices = slices,
            categoryStats = stats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )
}
