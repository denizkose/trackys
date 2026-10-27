package xyz.denizkose.trackys.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.denizkose.trackys.domain.model.BillingCycle
import java.time.LocalDate

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val cost: Double,
    val currency: String,
    val category: String,
    val billingCycle: BillingCycle,
    val firstPaymentDate: LocalDate,
    val isActive: Boolean
)
