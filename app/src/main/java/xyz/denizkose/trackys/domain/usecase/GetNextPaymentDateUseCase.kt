package xyz.denizkose.trackys.domain.usecase

import xyz.denizkose.trackys.domain.model.BillingCycle
import java.time.LocalDate

class GetNextPaymentDateUseCase {

    operator fun invoke(firstPaymentDate: LocalDate, billingCycle: BillingCycle): LocalDate {
        val today = LocalDate.now()
        var nextDate = firstPaymentDate

        if (nextDate.isAfter(today)) {
            return nextDate
        }

        while (!nextDate.isAfter(today)) {
            nextDate = when (billingCycle) {
                BillingCycle.WEEKLY -> nextDate.plusWeeks(1)
                BillingCycle.MONTHLY -> nextDate.plusMonths(1)
                BillingCycle.YEARLY -> nextDate.plusYears(1)
            }
        }
        return nextDate
    }
}