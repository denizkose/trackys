package xyz.denizkose.trackys.presentation.screens.add_edit

import androidx.annotation.StringRes
import xyz.denizkose.trackys.domain.model.BillingCycle
import java.time.LocalDate

data class AddEditUiState(
    val name: String = "",
    @StringRes val nameError: Int? = null,
    val cost: String = "",
    @StringRes val costError: Int? = null,
    val currency: String = "USD",
    val category: String = "Entertainment",
    val availableCategories: List<String> = emptyList(),
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val paymentDate: LocalDate = LocalDate.now(),
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)

sealed interface AddEditEvent {
    data class NameChanged(val name: String) : AddEditEvent
    data class CostChanged(val cost: String) : AddEditEvent
    data class CurrencyChanged(val currency: String) : AddEditEvent
    data class CategoryChanged(val category: String) : AddEditEvent
    data class AddNewCategory(val categoryName: String) : AddEditEvent
    data class BillingCycleChanged(val cycle: BillingCycle) : AddEditEvent
    data class DateChanged(val date: LocalDate) : AddEditEvent
    data object SaveClicked : AddEditEvent
    data object DeleteClicked : AddEditEvent
}

sealed interface AddEditEffect {
    data object NavigateBack : AddEditEffect
    data class ShowSnackbar(@StringRes val messageId: Int) : AddEditEffect
}
