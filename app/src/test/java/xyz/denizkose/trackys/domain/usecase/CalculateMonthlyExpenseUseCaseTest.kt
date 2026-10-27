package xyz.denizkose.trackys.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import xyz.denizkose.trackys.domain.model.BillingCycle
import xyz.denizkose.trackys.domain.model.Subscription
import xyz.denizkose.trackys.domain.model.SubscriptionCategory
import java.time.LocalDate

class CalculateMonthlyExpenseUseCaseTest {

    private lateinit var useCase: CalculateMonthlyExpenseUseCase

    @Before
    fun setUp() {
        useCase = CalculateMonthlyExpenseUseCase()
    }

    private fun createSubscription(cost: Double, cycle: BillingCycle): Subscription {
        return Subscription(
            id = 1,
            name = "Test Sub",
            cost = cost,
            currency = "USD",
            category = SubscriptionCategory.ENTERTAINMENT,
            billingCycle = cycle,
            firstPaymentDate = LocalDate.now(),
            isActive = true
        )
    }

    @Test
    fun `monthly subscription returns exact cost`() {
        val sub = createSubscription(cost = 10.0, cycle = BillingCycle.MONTHLY)
        val result = useCase(sub, exchangeRate = 1.0)
        assertEquals(10.0, result, 0.001)
    }

    @Test
    fun `yearly subscription divides cost by 12`() {
        val sub = createSubscription(cost = 120.0, cycle = BillingCycle.YEARLY)
        val result = useCase(sub, exchangeRate = 1.0)
        assertEquals(10.0, result, 0.001)
    }

    @Test
    fun `subscription with currency exchange rate converts correctly`() {
        val sub = createSubscription(cost = 100.0, cycle = BillingCycle.MONTHLY)
        // Наприклад, курс 0.5
        val result = useCase(sub, exchangeRate = 0.5)
        assertEquals(50.0, result, 0.001)
    }
}