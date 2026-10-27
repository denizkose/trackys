package xyz.denizkose.trackys.domain.repository

import xyz.denizkose.trackys.domain.model.CurrencyRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getExchangeRates(): Flow<Map<String, Double>>
    suspend fun refreshRates(baseCurrency: String): Result<Unit>
    fun getBaseCurrency(): Flow<String>
    suspend fun setBaseCurrency(currencyCode: String)
}