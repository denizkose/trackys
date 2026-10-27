package xyz.denizkose.trackys.presentation.screens.dashboard

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xyz.denizkose.trackys.MainDispatcherRule
import xyz.denizkose.trackys.domain.model.BillingCycle
import xyz.denizkose.trackys.domain.model.Subscription
import xyz.denizkose.trackys.domain.model.SubscriptionCategory
import xyz.denizkose.trackys.domain.repository.CurrencyRepository
import xyz.denizkose.trackys.domain.repository.SubscriptionRepository
import xyz.denizkose.trackys.domain.usecase.CalculateMonthlyExpenseUseCase
import java.time.LocalDate

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val subscriptionRepository: SubscriptionRepository = mockk(relaxed = true)
    private val currencyRepository: CurrencyRepository = mockk(relaxed = true)
    private val calculateMonthlyExpenseUseCase = CalculateMonthlyExpenseUseCase()

    private lateinit var viewModel: DashboardViewModel

    private val sampleSubscription = Subscription(
        id = 1L,
        name = "Spotify",
        cost = 10.0,
        currency = "USD",
        category = SubscriptionCategory.ENTERTAINMENT,
        billingCycle = BillingCycle.MONTHLY,
        firstPaymentDate = LocalDate.now(),
        isActive = true
    )

    @Before
    fun setUp() {
        coEvery { subscriptionRepository.getAllSubscriptions() } returns flowOf(
            listOf(
                sampleSubscription
            )
        )
        coEvery { currencyRepository.getExchangeRates() } returns flowOf(mapOf("USD" to 1.0))
        coEvery { currencyRepository.getBaseCurrency() } returns flowOf("USD")
        coEvery { currencyRepository.refreshRates(any()) } returns Result.success(Unit)

        viewModel = DashboardViewModel(
            subscriptionRepository = subscriptionRepository,
            currencyRepository = currencyRepository,
            calculateMonthlyExpenseUseCase = calculateMonthlyExpenseUseCase
        )
    }

    @Test
    fun `uiState emits subscriptions and calculated monthly cost correctly`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.subscriptions.size)
            assertEquals("Spotify", state.subscriptions.first().subscription.name)
            assertEquals(10.0, state.totalMonthlyCost, 0.001)
            assertEquals("USD", state.baseCurrency)
        }
    }
}