package xyz.denizkose.trackys.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import xyz.denizkose.trackys.core.NotificationHelper
import xyz.denizkose.trackys.domain.repository.SubscriptionRepository
import xyz.denizkose.trackys.domain.usecase.GetNextPaymentDateUseCase
import java.time.LocalDate

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val subscriptionRepository: SubscriptionRepository,
    private val getNextPaymentDateUseCase: GetNextPaymentDateUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val subscriptions = subscriptionRepository.getAllSubscriptions().first()
            val tomorrow = LocalDate.now().plusDays(1)

            for (sub in subscriptions) {
                if (!sub.isActive) continue

                val nextPayment = getNextPaymentDateUseCase(sub.firstPaymentDate, sub.billingCycle)

                // Якщо платіж заплановано на завтра — надсилаємо Push
                if (nextPayment == tomorrow) {
                    NotificationHelper.showReminderNotification(
                        context = appContext,
                        notificationId = sub.id.toInt(),
                        title = appContext.getString(xyz.denizkose.trackys.R.string.reminder_title, sub.name),
                        message = appContext.getString(xyz.denizkose.trackys.R.string.reminder_message, sub.cost.toString(), sub.currency)
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}