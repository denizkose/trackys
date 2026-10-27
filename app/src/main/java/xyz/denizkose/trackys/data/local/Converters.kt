package xyz.denizkose.trackys.data.local

import androidx.room.TypeConverter
import xyz.denizkose.trackys.domain.model.BillingCycle
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? = dateString?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromBillingCycle(cycle: BillingCycle): String = cycle.name

    @TypeConverter
    fun toBillingCycle(value: String): BillingCycle = BillingCycle.valueOf(value)
}
