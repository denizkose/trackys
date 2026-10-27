package xyz.denizkose.trackys.presentation.screens.dashboard

import androidx.annotation.StringRes
import xyz.denizkose.trackys.domain.model.Subscription

data class SubscriptionItemUi(
    val subscription: Subscription,
    val monthlyEquivalent: Double,
    val formattedCost: String
)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val baseCurrency: String = "USD",
    val totalMonthlyCost: Double = 0.0,
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val categoryCounts: Map<String, Int> = emptyMap(),
    val categoryColors: Map<String, String> = emptyMap(),
    val totalSubscriptionsCount: Int = 0,
    val subscriptions: List<SubscriptionItemUi> = emptyList(),
    val selectedSubscriptionIds: Set<Long> = emptySet(),
    val isRefreshingRates: Boolean = false,
    @StringRes val userMessage: Int? = null
) {
    val isSelectionMode: Boolean get() = selectedSubscriptionIds.isNotEmpty()
}

sealed interface DashboardEvent {
    data class SelectCategory(val category: String?) : DashboardEvent
    data class DeleteSubscription(val subscription: Subscription) : DashboardEvent
    data class ToggleSubscriptionSelection(val id: Long) : DashboardEvent
    data object ClearSelection : DashboardEvent
    data object DeleteSelectedSubscriptions : DashboardEvent
    data class ChangeCategoryForSelected(val newCategory: String) : DashboardEvent
    data object RefreshRates : DashboardEvent
    data object DismissMessage : DashboardEvent
}

sealed interface DashboardEffect {
    data class NavigateToAddEdit(val subscriptionId: Long? = null) : DashboardEffect
}
