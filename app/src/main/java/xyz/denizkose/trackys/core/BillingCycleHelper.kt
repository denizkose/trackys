package xyz.denizkose.trackys.core

import android.content.Context
import xyz.denizkose.trackys.R
import xyz.denizkose.trackys.domain.model.BillingCycle

object BillingCycleHelper {
    fun getLocalizedName(cycle: BillingCycle, context: Context): String {
        return when (cycle) {
            BillingCycle.MONTHLY -> context.getString(R.string.billing_cycle_monthly)
            BillingCycle.WEEKLY -> context.getString(R.string.billing_cycle_weekly)
            BillingCycle.YEARLY -> context.getString(R.string.billing_cycle_yearly)
        }
    }
}
