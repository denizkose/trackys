package xyz.denizkose.trackys.presentation.screens.analytics

import androidx.compose.ui.graphics.Color
import xyz.denizkose.trackys.presentation.components.ChartSlice

data class CategoryStatUi(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val formattedPercentage: String,
    val color: Color
)

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val totalMonthlyCost: Double = 0.0,
    val baseCurrency: String = "USD",
    val chartSlices: List<ChartSlice> = emptyList(),
    val categoryStats: List<CategoryStatUi> = emptyList()
)
