package xyz.denizkose.trackys.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import xyz.denizkose.trackys.data.remote.dto.ExchangeRateResponseDto

interface CurrencyApiService {

    @GET("rates")
    suspend fun getLatestRates(
        @Query("base") baseCurrency: String
    ): List<ExchangeRateResponseDto>
}
