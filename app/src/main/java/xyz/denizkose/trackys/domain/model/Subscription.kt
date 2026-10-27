package xyz.denizkose.trackys.domain.model

import java.time.LocalDate

data class Subscription(
    val id: Long = 0,
    val name: String,
    val cost: Double,
    val currency: String,
    val category: String,
    val billingCycle: BillingCycle,
    val firstPaymentDate: LocalDate,
    val isActive: Boolean = true
)
