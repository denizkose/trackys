package xyz.denizkose.trackys.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.denizkose.trackys.data.local.UserPreferences
import xyz.denizkose.trackys.domain.repository.CurrencyRepository
import xyz.denizkose.trackys.domain.repository.SubscriptionRepository
import xyz.denizkose.trackys.domain.usecase.CalculateMonthlyExpenseUseCase
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val currencyRepository: CurrencyRepository,
    private val calculateMonthlyExpenseUseCase: CalculateMonthlyExpenseUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _selectedSubscriptionIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _isRefreshingRates = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<Int?>(null)

    private val _effects = Channel<DashboardEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val _uiControls = combine(
        _selectedCategory,
        _selectedSubscriptionIds,
        _isRefreshingRates,
        _userMessage
    ) { category, selectedIds, isRefreshing, message ->
        Tuple4(category, selectedIds, isRefreshing, message)
    }

    private data class Tuple4<A, B, C, D>(
        val category: A,
        val selectedIds: B,
        val isRefreshing: C,
        val message: D
    )

    private val _categoryData = combine(
        userPreferences.categories,
        userPreferences.categoryColors
    ) { categoriesList, colorsMap ->
        Pair(categoriesList, colorsMap)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        subscriptionRepository.getAllSubscriptions(),
        currencyRepository.getExchangeRates(),
        currencyRepository.getBaseCurrency(),
        _categoryData,
        _uiControls
    ) { subs, rates, baseCur, (categoriesList, colorsMap), controls ->
        val category = controls.category
        val selectedIds = controls.selectedIds
        val isRefreshing = controls.isRefreshing
        val message = controls.message

        val filteredSubs = if (category == null) subs else subs.filter { it.category.equals(category, ignoreCase = true) }

        var totalMonth = 0.0

        val items = filteredSubs.map { sub ->
            val targetBaseRate = rates[baseCur] ?: 1.0
            val subCurrencyRate = rates[sub.currency] ?: 1.0
            val conversionRate = if (sub.currency.equals(baseCur, ignoreCase = true)) 1.0 else (targetBaseRate / subCurrencyRate)

            val monthlyCost = calculateMonthlyExpenseUseCase(sub, conversionRate)
            totalMonth += monthlyCost

            SubscriptionItemUi(
                subscription = sub,
                monthlyEquivalent = monthlyCost,
                formattedCost = "${String.format(Locale.US, "%.2f", sub.cost)} ${sub.currency}"
            )
        }

        // Обчислюємо кількість підписок у кожній категорії
        val counts = subs.groupingBy { it.category.lowercase() }.eachCount()

        // Об'єднуємо категорії з налаштувань та фактично наявних у підписках
        val allCategories = (categoriesList + subs.map { it.category }).distinct().sorted()

        DashboardUiState(
            isLoading = false,
            baseCurrency = baseCur,
            totalMonthlyCost = totalMonth,
            selectedCategory = category,
            categories = allCategories,
            categoryCounts = counts,
            categoryColors = colorsMap,
            totalSubscriptionsCount = subs.size,
            subscriptions = items,
            selectedSubscriptionIds = selectedIds,
            isRefreshingRates = isRefreshing,
            userMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    init {
        refreshRates()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.SelectCategory -> {
                _selectedCategory.update { if (it == event.category) null else event.category }
            }

            is DashboardEvent.DeleteSubscription -> {
                viewModelScope.launch {
                    subscriptionRepository.deleteSubscription(event.subscription)
                }
            }

            is DashboardEvent.ToggleSubscriptionSelection -> {
                _selectedSubscriptionIds.update { current ->
                    if (current.contains(event.id)) {
                        current - event.id
                    } else {
                        current + event.id
                    }
                }
            }

            DashboardEvent.ClearSelection -> {
                _selectedSubscriptionIds.update { emptySet() }
            }

            DashboardEvent.DeleteSelectedSubscriptions -> {
                viewModelScope.launch {
                    val idsToDelete = _selectedSubscriptionIds.value.toList()
                    if (idsToDelete.isNotEmpty()) {
                        subscriptionRepository.deleteSubscriptionsByIds(idsToDelete)
                        _selectedSubscriptionIds.update { emptySet() }
                    }
                }
            }

            is DashboardEvent.ChangeCategoryForSelected -> {
                viewModelScope.launch {
                    val selectedIds = _selectedSubscriptionIds.value.toList()
                    if (selectedIds.isNotEmpty()) {
                        subscriptionRepository.updateCategoryForSubscriptions(selectedIds, event.newCategory)
                        _selectedSubscriptionIds.update { emptySet() }
                    }
                }
            }

            DashboardEvent.RefreshRates -> refreshRates()
            DashboardEvent.DismissMessage -> _userMessage.update { null }
        }
    }

    fun onAddSubscriptionClicked() {
        viewModelScope.launch {
            _effects.send(DashboardEffect.NavigateToAddEdit())
        }
    }

    fun onSubscriptionClicked(id: Long) {
        if (_selectedSubscriptionIds.value.isNotEmpty()) {
            onEvent(DashboardEvent.ToggleSubscriptionSelection(id))
        } else {
            viewModelScope.launch {
                _effects.send(DashboardEffect.NavigateToAddEdit(id))
            }
        }
    }

    private fun refreshRates() {
        viewModelScope.launch {
            _isRefreshingRates.update { true }
            val currentBase = uiState.value.baseCurrency
            val result = currencyRepository.refreshRates(currentBase)
            if (result.isFailure) {
                _userMessage.update { xyz.denizkose.trackys.R.string.error_refresh_rates }
            }
            _isRefreshingRates.update { false }
        }
    }
}
