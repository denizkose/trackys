package xyz.denizkose.trackys.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xyz.denizkose.trackys.data.local.dao.SubscriptionDao
import xyz.denizkose.trackys.data.mapper.toDomain
import xyz.denizkose.trackys.data.mapper.toEntity
import xyz.denizkose.trackys.domain.model.Subscription
import xyz.denizkose.trackys.domain.repository.SubscriptionRepository
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) : SubscriptionRepository {

    override fun getAllSubscriptions(): Flow<List<Subscription>> {
        return subscriptionDao.getAllSubscriptions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSubscriptionById(id: Long): Subscription? {
        return subscriptionDao.getSubscriptionById(id)?.toDomain()
    }

    override suspend fun insertSubscription(subscription: Subscription) {
        subscriptionDao.insertSubscription(subscription.toEntity())
    }

    override suspend fun insertSubscriptions(subscriptions: List<Subscription>) {
        subscriptionDao.insertSubscriptions(subscriptions.map { it.toEntity() })
    }

    override suspend fun updateSubscription(subscription: Subscription) {
        subscriptionDao.updateSubscription(subscription.toEntity())
    }

    override suspend fun deleteSubscription(subscription: Subscription) {
        subscriptionDao.deleteSubscription(subscription.toEntity())
    }

    override suspend fun reassignCategory(oldCategory: String, newCategory: String) {
        subscriptionDao.reassignCategory(oldCategory, newCategory)
    }

    override suspend fun clearAllSubscriptions() {
        subscriptionDao.clearAllSubscriptions()
    }

    override suspend fun deleteSubscriptionsByIds(ids: List<Long>) {
        subscriptionDao.deleteSubscriptionsByIds(ids)
    }

    override suspend fun updateCategoryForSubscriptions(ids: List<Long>, newCategory: String) {
        subscriptionDao.updateCategoryForSubscriptions(ids, newCategory)
    }
}
