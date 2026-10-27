package xyz.denizkose.trackys.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xyz.denizkose.trackys.data.local.dao.ExchangeRateDao
import xyz.denizkose.trackys.data.local.dao.SubscriptionDao
import xyz.denizkose.trackys.data.local.entity.ExchangeRateEntity
import xyz.denizkose.trackys.data.local.entity.SubscriptionEntity

@Database(
    entities = [SubscriptionEntity::class, ExchangeRateEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}