package xyz.denizkose.trackys.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import xyz.denizkose.trackys.data.repository.CurrencyRepositoryImpl
import xyz.denizkose.trackys.data.repository.SubscriptionRepositoryImpl
import xyz.denizkose.trackys.domain.repository.CurrencyRepository
import xyz.denizkose.trackys.domain.repository.SubscriptionRepository
import xyz.denizkose.trackys.domain.usecase.CalculateMonthlyExpenseUseCase
import xyz.denizkose.trackys.domain.usecase.GetNextPaymentDateUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: SubscriptionRepositoryImpl
    ): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        impl: CurrencyRepositoryImpl
    ): CurrencyRepository

    companion object {
        @Provides
        @Singleton
        fun provideCalculateMonthlyExpenseUseCase(): CalculateMonthlyExpenseUseCase {
            return CalculateMonthlyExpenseUseCase()
        }

        @Provides
        @Singleton
        fun provideGetNextPaymentDateUseCase(): GetNextPaymentDateUseCase {
            return GetNextPaymentDateUseCase()
        }
    }
}