package xyz.denizkose.trackys.data.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionExportDto(
    val name: String,
    val cost: Double,
    val currency: String,
    val category: String,
    val billingCycle: String,
    val firstPaymentDate: String,
    val isActive: Boolean = true
)
