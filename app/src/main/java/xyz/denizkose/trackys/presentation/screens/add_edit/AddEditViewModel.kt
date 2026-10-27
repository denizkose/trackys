package xyz.denizkose.trackys.presentation.screens.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.denizkose.trackys.data.local.UserPreferences
import xyz.denizkose.trackys.domain.model.Subscription
import xyz.denizkose.trackys.domain.repository.CurrencyRepository
import xyz.denizkose.trackys.domain.repository.SubscriptionRepository
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val currencyRepository: CurrencyRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subscriptionId: Long? =
        savedStateHandle.get<Long>("subscriptionId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(AddEditUiState(isEditMode = subscriptionId != null))
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<AddEditEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var currentSubscription: Subscription? = null

    init {
        observeCategories()
        loadInitialData()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            userPreferences.categories.collect { categories ->
                _uiState.update { currentState ->
                    val currentCat = currentState.category
                    val validCategory = if (categories.contains(currentCat) || currentState.isEditMode) {
                        currentCat
                    } else {
                        categories.firstOrNull() ?: "Other"
                    }
                    currentState.copy(
                        category = validCategory,
                        availableCategories = categories
                    )
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            if (subscriptionId != null) {
                _uiState.update { it.copy(isLoading = true) }
                val sub = subscriptionRepository.getSubscriptionById(subscriptionId)
                if (sub != null) {
                    currentSubscription = sub
                    _uiState.update {
                        it.copy(
                            name = sub.name,
                            cost = sub.cost.toString(),
                            currency = sub.currency,
                            category = sub.category,
                            billingCycle = sub.billingCycle,
                            paymentDate = sub.firstPaymentDate,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.send(AddEditEffect.ShowSnackbar(xyz.denizkose.trackys.R.string.subscription_not_found))
                    _effects.send(AddEditEffect.NavigateBack)
                }
            } else {
                currencyRepository.getBaseCurrency().collect { baseCur ->
                    _uiState.update { it.copy(currency = baseCur) }
                }
            }
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.NameChanged -> _uiState.update {
                it.copy(
                    name = event.name,
                    nameError = null
                )
            }

            is AddEditEvent.CostChanged -> _uiState.update {
                it.copy(
                    cost = event.cost,
                    costError = null
                )
            }

            is AddEditEvent.CurrencyChanged -> _uiState.update { it.copy(currency = event.currency) }
            is AddEditEvent.CategoryChanged -> _uiState.update { it.copy(category = event.category) }
            is AddEditEvent.AddNewCategory -> {
                viewModelScope.launch {
                    val newCategory = event.categoryName.trim()
                    if (newCategory.isNotEmpty()) {
                        userPreferences.addCategory(newCategory)
                        _uiState.update { it.copy(category = newCategory) }
                    }
                }
            }
            is AddEditEvent.BillingCycleChanged -> _uiState.update { it.copy(billingCycle = event.cycle) }
            is AddEditEvent.DateChanged -> _uiState.update { it.copy(paymentDate = event.date) }
            AddEditEvent.SaveClicked -> saveSubscription()
            AddEditEvent.DeleteClicked -> deleteSubscription()
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = xyz.denizkose.trackys.R.string.error_name_empty) }
            isValid = false
        }

        val costValue = state.cost.toDoubleOrNull()
        if (costValue == null || costValue <= 0.0) {
            _uiState.update { it.copy(costError = xyz.denizkose.trackys.R.string.error_cost_invalid) }
            isValid = false
        }

        return isValid
    }

    private fun saveSubscription() {
        if (!validate()) return

        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val subscriptionToSave = Subscription(
                id = currentSubscription?.id ?: 0,
                name = state.name.trim(),
                cost = state.cost.toDouble(),
                currency = state.currency.uppercase(),
                category = state.category.trim(),
                billingCycle = state.billingCycle,
                firstPaymentDate = state.paymentDate,
                isActive = true
            )

            if (state.isEditMode) {
                subscriptionRepository.updateSubscription(subscriptionToSave)
            } else {
                subscriptionRepository.insertSubscription(subscriptionToSave)
            }

            _effects.send(AddEditEffect.NavigateBack)
        }
    }

    private fun deleteSubscription() {
        val sub = currentSubscription ?: return
        viewModelScope.launch {
            subscriptionRepository.deleteSubscription(sub)
            _effects.send(AddEditEffect.NavigateBack)
        }
    }
}
