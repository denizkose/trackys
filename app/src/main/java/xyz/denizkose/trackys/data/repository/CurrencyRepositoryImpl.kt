package xyz.denizkose.trackys.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xyz.denizkose.trackys.data.local.UserPreferences
import xyz.denizkose.trackys.data.local.dao.ExchangeRateDao
import xyz.denizkose.trackys.data.local.entity.ExchangeRateEntity
import xyz.denizkose.trackys.data.remote.CurrencyApiService
import xyz.denizkose.trackys.domain.repository.CurrencyRepository
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService,
    private val exchangeRateDao: ExchangeRateDao,
    private val userPreferences: UserPreferences
) : CurrencyRepository {

    override fun getExchangeRates(): Flow<Map<String, Double>> {
        return exchangeRateDao.getAllRates().map { list ->
            list.associate { it.currencyCode to it.rateAgainstBase }
        }
    }

    override suspend fun refreshRates(baseCurrency: String): Result<Unit> {
        return try {
            // В API v2 ми отримуємо список об'єктів для кожної пари
            val responseList = apiService.getLatestRates(baseCurrency)

            if (responseList.isEmpty()) {
                return Result.failure(IllegalStateException("No exchange rates available for $baseCurrency"))
            }

            val entitiesMap = mutableMapOf<String, Double>()
            
            responseList.forEach { dto ->
                if (dto.quote != null && dto.rate != null) {
                    entitiesMap[dto.quote] = dto.rate
                }
            }
            entitiesMap[baseCurrency] = 1.0

            if (entitiesMap.isEmpty()) {
                return Result.failure(IllegalStateException("Parsed exchange rates are empty"))
            }

            val entities = entitiesMap.map { (code, rate) ->
                ExchangeRateEntity(currencyCode = code, rateAgainstBase = rate)
            }

            exchangeRateDao.clearRates()
            exchangeRateDao.insertAllRates(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getBaseCurrency(): Flow<String> = userPreferences.baseCurrency

    override suspend fun setBaseCurrency(currencyCode: String) {
        userPreferences.setBaseCurrency(currencyCode)
    }
}
