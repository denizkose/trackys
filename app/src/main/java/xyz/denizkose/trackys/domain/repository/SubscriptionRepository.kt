package xyz.denizkose.trackys.domain.repository

import kotlinx.coroutines.flow.Flow
import xyz.denizkose.trackys.domain.model.Subscription

interface SubscriptionRepository {
    fun getAllSubscriptions(): Flow<List<Subscription>>
    suspend fun getSubscriptionById(id: Long): Subscription?
    suspend fun insertSubscription(subscription: Subscription)
    suspend fun insertSubscriptions(subscriptions: List<Subscription>)
    suspend fun updateSubscription(subscription: Subscription)
    suspend fun deleteSubscription(subscription: Subscription)
    suspend fun reassignCategory(oldCategory: String, newCategory: String)
    suspend fun clearAllSubscriptions()
    suspend fun deleteSubscriptionsByIds(ids: List<Long>)
    suspend fun updateCategoryForSubscriptions(ids: List<Long>, newCategory: String)
}
