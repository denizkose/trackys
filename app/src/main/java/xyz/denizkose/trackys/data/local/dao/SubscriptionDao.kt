package xyz.denizkose.trackys.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import xyz.denizkose.trackys.data.local.entity.SubscriptionEntity

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscriptions ORDER BY cost DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptions(subscriptions: List<SubscriptionEntity>)

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Delete
    suspend fun deleteSubscription(subscription: SubscriptionEntity)

    @Query("UPDATE subscriptions SET category = :newCategory WHERE LOWER(category) = LOWER(:oldCategory)")
    suspend fun reassignCategory(oldCategory: String, newCategory: String)

    @Query("DELETE FROM subscriptions")
    suspend fun clearAllSubscriptions()

    @Query("DELETE FROM subscriptions WHERE id IN (:ids)")
    suspend fun deleteSubscriptionsByIds(ids: List<Long>)

    @Query("UPDATE subscriptions SET category = :newCategory WHERE id IN (:ids)")
    suspend fun updateCategoryForSubscriptions(ids: List<Long>, newCategory: String)
}
