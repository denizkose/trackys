package xyz.denizkose.trackys.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponseDto(
    @SerialName("date")
    val date: String? = null,
    @SerialName("base")
    val base: String? = null,
    @SerialName("quote")
    val quote: String? = null,
    @SerialName("rate")
    val rate: Double? = null
)
