package xyz.denizkose.trackys.presentation.screens.add_edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import xyz.denizkose.trackys.R
import xyz.denizkose.trackys.core.BillingCycleHelper
import xyz.denizkose.trackys.core.CategoryHelper
import xyz.denizkose.trackys.core.CurrencyHelper
import xyz.denizkose.trackys.domain.model.BillingCycle
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryInput by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var cycleExpanded by remember { mutableStateOf(false) }

    val popularCurrencies = CurrencyHelper.POPULAR_CURRENCIES.map { it.code }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                AddEditEffect.NavigateBack -> onNavigateBack()
                is AddEditEffect.ShowSnackbar -> snackbarHostState.showSnackbar(context.getString(effect.messageId))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.paymentDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate =
                                Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            viewModel.onEvent(AddEditEvent.DateChanged(selectedDate))
                        }
                        showDatePicker = false
                    }
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showNewCategoryDialog = false },
            title = { Text(stringResource(R.string.add_custom_category)) },
            text = {
                OutlinedTextField(
                    value = newCategoryInput,
                    onValueChange = { newCategoryInput = it },
                    label = { Text(stringResource(R.string.enter_category_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryInput.isNotBlank()) {
                            viewModel.onEvent(AddEditEvent.AddNewCategory(newCategoryInput))
                            newCategoryInput = ""
                            showNewCategoryDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        newCategoryInput = ""
                        showNewCategoryDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) stringResource(R.string.edit_subscription) else stringResource(R.string.new_subscription)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (state.isEditMode) {
                        IconButton(onClick = { viewModel.onEvent(AddEditEvent.DeleteClicked) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Назва підписки
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.onEvent(AddEditEvent.NameChanged(it)) },
                    label = { Text(stringResource(R.string.service_name)) },
                    placeholder = { Text(stringResource(R.string.service_name_placeholder)) },
                    isError = state.nameError != null,
                    supportingText = state.nameError?.let { { Text(stringResource(it)) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Вартість та валюта
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.cost,
                        onValueChange = { viewModel.onEvent(AddEditEvent.CostChanged(it)) },
                        label = { Text(stringResource(R.string.cost)) },
                        placeholder = { Text(stringResource(R.string.cost_placeholder)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.costError != null,
                        supportingText = state.costError?.let { { Text(stringResource(it)) } },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    // Вибір валюти
                    var currencyExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = currencyExpanded,
                        onExpandedChange = { currencyExpanded = it },
                        modifier = Modifier.width(140.dp)
                    ) {
                        OutlinedTextField(
                            value = CurrencyHelper.getDisplayLabel(state.currency),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.currency)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = currencyExpanded,
                            onDismissRequest = { currencyExpanded = false }
                        ) {
                            popularCurrencies.forEach { cur ->
                                DropdownMenuItem(
                                    text = { Text(CurrencyHelper.getDisplayLabel(cur)) },
                                    onClick = {
                                        viewModel.onEvent(AddEditEvent.CurrencyChanged(cur))
                                        currencyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Категорія
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = CategoryHelper.getLocalizedName(state.category, context),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        state.availableCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(CategoryHelper.getLocalizedName(cat, context))
                                },
                                onClick = {
                                    viewModel.onEvent(AddEditEvent.CategoryChanged(cat))
                                    categoryExpanded = false
                                }
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.add_custom_category),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                categoryExpanded = false
                                showNewCategoryDialog = true
                            }
                        )
                    }
                }

                // Цикл оплати
                ExposedDropdownMenuBox(
                    expanded = cycleExpanded,
                    onExpandedChange = { cycleExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = BillingCycleHelper.getLocalizedName(state.billingCycle, context),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.billing_cycle)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cycleExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = cycleExpanded,
                        onDismissRequest = { cycleExpanded = false }
                    ) {
                        BillingCycle.entries.forEach { cycle ->
                            DropdownMenuItem(
                                text = {
                                    Text(BillingCycleHelper.getLocalizedName(cycle, context))
                                },
                                onClick = {
                                    viewModel.onEvent(AddEditEvent.BillingCycleChanged(cycle))
                                    cycleExpanded = false
                                }
                            )
                        }
                    }
                }

                // Дата першого платежу
                OutlinedTextField(
                    value = state.paymentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.payment_date)) },
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = stringResource(R.string.select_date))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка збереження
                Button(
                    onClick = { viewModel.onEvent(AddEditEvent.SaveClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(if (state.isEditMode) stringResource(R.string.save_changes) else stringResource(R.string.create_subscription))
                    }
                }
            }
        }
    }
}
