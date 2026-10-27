package xyz.denizkose.trackys.data.mapper

import xyz.denizkose.trackys.data.local.entity.SubscriptionEntity
import xyz.denizkose.trackys.domain.model.Subscription

fun SubscriptionEntity.toDomain(): Subscription {
    return Subscription(
        id = id,
        name = name,
        cost = cost,
        currency = currency,
        category = category,
        billingCycle = billingCycle,
        firstPaymentDate = firstPaymentDate,
        isActive = isActive
    )
}

fun Subscription.toEntity(): SubscriptionEntity {
    return SubscriptionEntity(
        id = id,
        name = name,
        cost = cost,
        currency = currency,
        category = category,
        billingCycle = billingCycle,
        firstPaymentDate = firstPaymentDate,
        isActive = isActive
    )
}