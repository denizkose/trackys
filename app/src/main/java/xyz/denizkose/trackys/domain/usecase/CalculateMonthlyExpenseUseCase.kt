package xyz.denizkose.trackys.domain.usecase

import xyz.denizkose.trackys.domain.model.BillingCycle
import xyz.denizkose.trackys.domain.model.Subscription

class CalculateMonthlyExpenseUseCase {

    /**
     * Convert sub to eq for 1 month
     */
    operator fun invoke(subscription: Subscription, exchangeRate: Double = 1.0): Double {
        val costInTargetCurrency = subscription.cost * exchangeRate

        return when (subscription.billingCycle) {
            BillingCycle.MONTHLY -> costInTargetCurrency
            BillingCycle.YEARLY -> costInTargetCurrency / 12.0
            BillingCycle.WEEKLY -> costInTargetCurrency * 4.3333333333
        }
    }
}